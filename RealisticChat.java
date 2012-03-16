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

        // Send to recipients
        for (Player recipient: event.getRecipients()) {
            if (sender.equals(recipient)) {
                // Talking to ourselves
                // TODO: still scramble?
                deliverMessage(recipient, sender, message); // TODO: indicate as coming from self?
                continue;
            }

            if (!sender.getWorld().equals(recipient.getWorld())) {
                // Not in this world!
                // TODO: cross-world communication device?
                continue;
            }

            double distance = sender.getLocation().distance(recipient.getLocation());

            plugin.log.info("distance="+distance);

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

                plugin.log.info("clarity = "+clarity);
                deliverMessage(recipient, sender, breakUpMessage(message, clarity));
            } else {
                deliverMessage(recipient, sender, message);
            }
        }

        // Deliver the message manually, so we can customize the chat display 
        event.setCancelled(true);
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
                // TODO: third case, dark gray? (barely gets through)
            } else {
                newMessage.append(' ');
            }
        }

        return new String(newMessage);
    }

    private void deliverMessage(Player recipient, Player sender, String message) {
        recipient.sendMessage(ChatColor.GREEN + sender.getDisplayName() + ": " + message);
        plugin.log.info("[RealisticChat] "+sender.getName() + " -> " + recipient.getName() + ": " + message);
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
