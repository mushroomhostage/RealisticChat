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

        int hearingRangeMeters = plugin.getConfig().getInt("hearingRangeMeters", 50);
        int scrambleRangeMeters = plugin.getConfig().getInt("scrambleRangeMeters", 25);

        // Yelling costs hunger and increases range
        int yell = countExclamationMarks(message);
        if (yell > 0) {
            sendInfo.add("yell="+yell);

            int hunger = plugin.getConfig().getInt("hungerPerYell", 1) * yell;
            sender.setFoodLevel(sender.getFoodLevel() - hunger);


            int delta = plugin.getConfig().getInt("yellRangeIncrease", 10) * yell;
            hearingRangeMeters += delta;
            scrambleRangeMeters += delta;
        }

        // Whispering decreases range
        int whisper = countParenthesizeNests(message);
        if (whisper > 0) {
            sendInfo.add("whisper="+whisper);

            int delta = plugin.getConfig().getInt("whisperRangeDecrease", 10) * whisper;
            hearingRangeMeters -= delta;
            scrambleRangeMeters -= delta;
        }

        // Megaphone
        ItemStack senderHeld = sender.getItemInHand();
        if (senderHeld != null && senderHeld.getType() == Material.DIAMOND) { // TODO: configurable item
            sendInfo.add("mega");
            int factor = 2;  // TODO: hold more, increase more? but need a cap, base and max
            hearingRangeMeters *= factor;
            scrambleRangeMeters *= factor;
            // TODO: should only increase in a conical volume in front of the player! Like a real megaphone
        }

        // Log that the player tried to talk
        sendInfo.add("r="+hearingRangeMeters+"/"+scrambleRangeMeters);
        plugin.log.info("<" + sender.getName() + ": "+joinList(sendInfo)+"> "+message);

        // Send to recipients
        for (Player recipient: event.getRecipients()) {
            ArrayList<String> recvInfo = new ArrayList<String>();

            // TODO: special item to hold, to receive all or send to all (infinity compass?)

            if (sender.equals(recipient)) {
                // Talking to ourselves
                // TODO: still scramble? if talking through something
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
            ItemStack senderHolding = sender.getItemInHand();
            if (senderHolding != null && senderHolding.getType() == Material.COMPASS) { // TODO: configurable
                ItemStack recipientHolding = recipient.getItemInHand();
                // Is recipient also listening to their walkie?
                if (recipientHolding != null && recipientHolding.getType() == Material.COMPASS) { // TODO: hold anywhere in hotbar to receive
                    if (distance < plugin.getConfig().getInt("walkieRange", 1000)) {
                        ArrayList<String> recvInfoWalkie = new ArrayList<String>(recvInfo);

                        recvInfoWalkie.add("walkie");

                        // TODO: reduce clarity if too far away, like with normal chat
                        // TODO: show as from walkie-talkie, but with callsign of sender?
                        deliverMessage(recipient, sender, "[via walkie] " + message, recvInfoWalkie);

                        // also fall through and deliver message locally
                    }
                }
            }

            // Limit distance
            if (distance > hearingRangeMeters) {
                // TODO: earphones? to receive further
                continue;
            }
            if (distance > scrambleRangeMeters) {
                // At distances hearingRangeMeters..scrambleRangeMeters (50..25), break up
                // with increasing probability the further away they are.
                // 24 = perfectly clear
                // 25 = slightly garbled
                // (distance-25)/50
                // 50 = half clear
                double noise = (distance - scrambleRangeMeters) / hearingRangeMeters;
                double clarity = 1 - noise;

                recvInfo.add("clarity="+clarity);

                deliverMessage(recipient, sender, breakUpMessage(message, clarity), recvInfo);
            } else {
                deliverMessage(recipient, sender, message, recvInfo);
            }
        }

        // Deliver the message manually, so we can customize the chat display 
        event.setCancelled(true);
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
