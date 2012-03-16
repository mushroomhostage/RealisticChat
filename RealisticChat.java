package me.exphc.RealisticChat;


import java.util.logging.Logger;
import java.util.Set;
import java.util.HashSet;
import java.util.Random;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
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

        plugin.log.info("<" + sender.getName() + "> "+message);

        int hearingRangeMeters = plugin.getConfig().getInt("hearingRangeMeters", 50);
        int scrambleRangeMeters = plugin.getConfig().getInt("scrambleRangeMeters", 25);

        // Yelling costs hunger and increases range
        int yell = countExclamationMarks(message);
        if (yell > 0) {
            int hunger = plugin.getConfig().getInt("hungerPerYell", 1) * yell;
            sender.setFoodLevel(sender.getFoodLevel() - hunger);

            int extra = plugin.getConfig().getInt("yellRangeIncrease", 10) * yell;
            hearingRangeMeters += extra;
            scrambleRangeMeters += extra;
        }


        // Send to recipients
        for (Player recipient: event.getRecipients()) {
            if (sender.equals(recipient)) {
                // Talking to ourselves
                // TODO: still scramble? if talking through something
                deliverMessage(recipient, sender, message, "self");
                continue;
            }

            if (!sender.getWorld().equals(recipient.getWorld())) {
                // Not in this world!
                // TODO: cross-world communication device?
                continue;
            }

            double distance = sender.getLocation().distance(recipient.getLocation());

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

                deliverMessage(recipient, sender, breakUpMessage(message, clarity), "d="+distance+", clarity="+clarity);
            } else {
                deliverMessage(recipient, sender, message, "d="+distance);
            }
        }

        // Deliver the message manually, so we can customize the chat display 
        event.setCancelled(true);
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

    private void deliverMessage(Player recipient, Player sender, String message, String info) {
        ChatColor senderColor = (sender.equals(recipient) ? ChatColor.YELLOW : ChatColor.GREEN);
        ChatColor messageColor = ChatColor.WHITE;

        recipient.sendMessage(senderColor + sender.getDisplayName() + ": " + messageColor + message);
        plugin.log.info("[RealisticChat] ("+info+") "+sender.getName() + " -> " + recipient.getName() + ": " + message);
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
