package me.exphc.RealisticChat;


import java.util.logging.Logger;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Random;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.*;

class RealisticChatListener implements Listener {
    RealisticChat plugin;
    Random random;

    public RealisticChatListener(RealisticChat plugin) {
        this.random = new Random();

        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerChat(PlayerChatEvent event) {
        Player sender = event.getPlayer();
        String message = event.getMessage();
        ArrayList<String> sendInfo = new ArrayList<String>();

        // TODO: change to sound pressure level instead of distance based
        // see http://en.wikipedia.org/wiki/Sound_pressure#Examples_of_sound_pressure_and_sound_pressure_levels
        // for added realism, also to allow 130 dB = threshold of pain (player damage), nausea, http://en.wikipedia.org/wiki/Sound_cannon
        double hearingRangeMeters = plugin.getConfig().getDouble("hearingRangeMeters", 50.0);
        double garbleRangeDivisor = plugin.getConfig().getDouble("garbleRangeDivisor", 2.0); // so last 1/2.0 (half) of distance, speech is unclear

        // Yelling costs hunger and increases range
        int yell = countExclamationMarks(message);
        if (yell > 0) {
            yell = Math.min(yell, plugin.getConfig().getInt("yellMax", 4));

            sendInfo.add("yell="+yell);

            final int defaultHunger[] = { 1, 2, 4, 20 };
            final double defaultRangeIncrease[] = { 10.0, 50.0, 100.0, 500.0 };

            int hunger = plugin.getConfig().getInt("yell."+yell+".hunger", defaultHunger[yell - 1]);
            // TODO: check food level first, and clamp yell if insufficient (or take away health hearts too?)
            sender.setFoodLevel(sender.getFoodLevel() - hunger);

            hearingRangeMeters += plugin.getConfig().getDouble("yell."+yell+".rangeIncrease", defaultRangeIncrease[yell - 1]);
        }

        // Whispering decreases range
        int whisper = countParenthesizeNests(message);
        if (whisper > 0) {
            sendInfo.add("whisper="+whisper);

            hearingRangeMeters -= plugin.getConfig().getDouble("whisperRangeDecrease", 40.0) * whisper;
            if (hearingRangeMeters < 1) {
                hearingRangeMeters = 1;
            }
        }

        // Megaphone
        ItemStack senderHeld = sender.getItemInHand();
        if (senderHeld != null && senderHeld.getType() == Material.DIAMOND) { // TODO: configurable item
            sendInfo.add("mega");
            double factor = plugin.getConfig().getDouble("megaphoneFactor", 2.0);  // TODO: hold more, increase more? but need a cap, base and max
            hearingRangeMeters *= factor;
            // TODO: should only increase in a conical volume in front of the player! Like a real megaphone
            // http://en.wikipedia.org/wiki/Megaphone
        }

        // Log that the player tried to talk
        double clearRangeMeters = hearingRangeMeters / garbleRangeDivisor;
        sendInfo.add("r="+hearingRangeMeters+"/"+clearRangeMeters);
        plugin.log.info("<" + sender.getName() + ": "+joinList(sendInfo)+"> "+message);

        // Send to recipients
        for (Player recipient: event.getRecipients()) {
            ArrayList<String> recvInfo = new ArrayList<String>();

            // TODO: special item to hold, to receive all or send to all (infinity compass?)

            if (sender.equals(recipient)) {
                // Talking to ourselves
                // TODO: still garble? if talking through something
                deliverMessage(recipient, sender, message, recvInfo);
                continue;
            }

            if (!sender.getWorld().equals(recipient.getWorld())) {
                // Not in this world!
                // TODO: cross-world communication device?
                continue;
            }

            double distance = sender.getLocation().distance(recipient.getLocation());
            recvInfo.add("d="+distance);

            // Talking into walkie-talkie device
            if (hasWalkieTalking(sender) && hasWalkieListening(recipient)) {
                if (distance < plugin.getConfig().getDouble("walkieRange", 1000.0)) {
                    ArrayList<String> recvInfoWalkie = new ArrayList<String>(recvInfo);

                    recvInfoWalkie.add("walkie");

                    // TODO: reduce clarity if too far away, like with normal chat
                    // TODO: show as from walkie-talkie, but with callsign of sender?
                    deliverMessage(recipient, sender, "[via walkie] " + message, recvInfoWalkie);

                    // also fall through and deliver message locally
                }
            }

            // TODO: earphones? to receive further
            // better yet: http://en.wikipedia.org/wiki/Ear_trumpet - 1600s precursor to modern electric hearing aid

            // Limit distance
            if (distance > hearingRangeMeters) {
                continue;
            }

            if (distance > clearRangeMeters) {
                // At distances hearingRangeMeters..garbleRangeMeters (50..25), break up
                // with increasing probability the further away they are.
                // 24 = perfectly clear
                // 25 = slightly garbled
                // (distance-25)/50
                // 50 = half clear
                double noise = (distance - clearRangeMeters) / hearingRangeMeters;
                double clarity = 1 - noise;

                recvInfo.add("clarity="+clarity);

                deliverMessage(recipient, sender, megaphoneDirection(recipient, sender) + breakUpMessage(message, clarity), recvInfo);
            } else {
                deliverMessage(recipient, sender, megaphoneDirection(recipient, sender) + message, recvInfo);
            }
        }

        // Deliver the message manually, so we can customize the chat display 
        event.setCancelled(true);
    }

    /** Get whether the player has a walkie-talkie ready to talk into.
    */
    private boolean hasWalkieTalking(Player player) {
        ItemStack held = player.getItemInHand();
        
        return held != null && held.getType() == Material.COMPASS; // TODO: configurable
    }

    /** Get whether the player has a walkie-talkie ready for listening.
    */
    private boolean hasWalkieListening(Player player) {
        ItemStack[] contents = player.getInventory().getContents();

        final int HOTBAR_SIZE = 9;
        // Player can hear walkie if placed anywhere within their hotbar slots (not elsewhere)
        for (int i = 0; i < HOTBAR_SIZE; i += 1) {
            ItemStack item = contents[i];

            if (item != null && item.getType() == Material.COMPASS) {
                return true;
            }
        }

        return false;
    }


    private String joinList(ArrayList<String> list) {
        StringBuilder sb = new StringBuilder();
        for (String item: list) {
            sb.append(item + ",");
        }
        String s = sb.toString();
        if (s.length() == 0) {
            return "";
        } else {
            return s.substring(0, s.length() - 1);
        } 
    }

    /** Count number of trailing exclamation marks
     */
    private int countExclamationMarks(String message) {
        int yell = 0;
        while (message.length() > 1 && message.endsWith("!")) {
            message = message.substring(0, message.length() - 1);
            yell += 1;
        }

        return yell;
    }
  
    /** Count number of nested surrounding parenthesizes
     */
    private int countParenthesizeNests(String message) {
        int whisper = 0;
        while (message.length() > 2 && message.startsWith("(") && message.endsWith(")")) {
            message = message.substring(1, message.length() - 1);
            whisper += 1;
        }
        return whisper;
    }

    /** Randomly "break up" a message as if it was incompletely heard
      *
      * @param message The clear message
      * @param clarity Probability of getting through
      * @return The broken up message
      */
    private String breakUpMessage(String message, double clarity) {
        // Delete random letters
        StringBuilder newMessage = new StringBuilder();

        // This string character iteration method is cumbersome, but it is
        // the most correct, especially if players are using plane 1 characters
        // see http://mindprod.com/jgloss/codepoint.html
        int i = 0;
        while (i < message.length()) {
            int c = message.codePointAt(i);
            i += Character.charCount(c);

            if (random.nextDouble() < clarity) {
                newMessage.appendCodePoint(c);
            } else {
                newMessage.append(' ');
            }
            // TODO: third case, dark gray? (barely gets through)
        }

        return new String(newMessage);
    }

    private void deliverMessage(Player recipient, Player sender, String message, ArrayList<String> info) {
        ChatColor senderColor = (sender.equals(recipient) ? ChatColor.YELLOW : ChatColor.GREEN);
        ChatColor messageColor = ChatColor.WHITE;

        recipient.sendMessage(senderColor + sender.getDisplayName() + ": " + messageColor + message);
        plugin.log.info("[RealisticChat] ("+joinList(info)+") "+sender.getName() + " -> " + recipient.getName() + ": " + message);
    }
    
    private String megaphoneDirection(Player recipient, Player sender){
    	String addition = "";
    	double recX = recipient.getLocation().getX();
    	double recZ = recipient.getLocation().getZ();
    	double senX = sender.getLocation().getX();
    	double senZ = sender.getLocation().getZ();
        ItemStack senderHeld = sender.getItemInHand();
    	if (senderHeld != null && senderHeld.getType() == Material.DIAMOND){
    		if (recZ > senZ)
    			addition = addition + "[North";
    		if (recZ < senZ)
    			addition = addition + "[South";
    		if (recX > senX)
    			addition = addition + "West]";
    		if (recX < senX)
    			addition = addition + "East]";
    	}
    	return addition;
    }
}

public class RealisticChat extends JavaPlugin { 
    Logger log = Logger.getLogger("Minecraft");
    RealisticChatListener listener;


    public void onEnable() {
        listener = new RealisticChatListener(this);
    }

    public void onDisable() {
    }
}
