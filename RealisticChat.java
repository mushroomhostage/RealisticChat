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

        // Send to recipients
        for (Player recipient: event.getRecipients()) {
            if (sender.equals(recipient)) {
                // Talking to ourselves
                // TODO: still scramble?
                continue;
            }

            if (!sender.getWorld().equals(recipient.getWorld())) {
                // Not in this world!
                // TODO: cross-world communication device?
                continue;
            }

            double distance = sender.getLocation().distance(recipient.getLocation());

            plugin.log.info("distance="+distance);

            String message = event.getMessage();

            // Limit distance
            if (distance > plugin.getConfig().getInt("hearingRangeMeters", 50)) {
                // TODO: earphones? to receive further
                continue;
            }
            if (distance > plugin.getConfig().getInt("scrambleRangeMeters", 25)) {
                // Delete random letters
                StringBuilder newMessage = new StringBuilder();

                // This string character iteration method is cumbersome, but it is
                // the most correct, especially if players are using plane 1 characters
                // see http://mindprod.com/jgloss/codepoint.html
                int i = 0;
                double clarity = 0.5;    // probability of getting through
                while (i < message.length()) {
                    int c = message.codePointAt(i);
                    i += Character.charCount(c);

                    if (random.nextDouble() < clarity) {
                        newMessage.appendCodePoint(c);
                    } else {
                        newMessage.append(' ');
                    }
                }

                deliverMessage(recipient, sender, new String(newMessage));
            } else {
                deliverMessage(recipient, sender, message);
            }
        }

        // Deliver the message manually, so we can customize the chat display 
        event.setCancelled(true);
    }

    private void deliverMessage(Player recipient, Player sender, String message) {
        sender.sendMessage(ChatColor.GREEN + sender.getDisplayName() + ": " + message);
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
