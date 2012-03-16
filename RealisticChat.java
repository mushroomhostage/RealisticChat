package me.exphc.RealisticChat;

import java.util.logging.Logger;

import java.util.Set;
import java.util.HashSet;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.*;

class RealisticChatListener implements Listener {
    RealisticChat plugin;

    public RealisticChatListener(RealisticChat plugin) {
        this.plugin = plugin;

        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerChat(PlayerChatEvent event) {
        Player sender = event.getPlayer();

        // Get a copy of the recipients, so we can modify it (for some reason, no clone()?)
        Set<Player> recipients = new HashSet<Player>(event.getRecipients());

        for (Player recipient: recipients) {
            if (sender.equals(recipient)) {
                // Talking to ourselves
                // TODO: still scramble?
                continue;
            }

            if (!sender.getWorld().equals(recipient.getWorld())) {
                // Not in this world!
                // TODO: cross-world communication device?
                event.getRecipients().remove(recipient);
                continue;
            }

            double distance = sender.getLocation().distance(recipient.getLocation());

            plugin.log.info("distance="+distance);

            // Limit distance
            if (distance > plugin.getConfig().getInt("hearingRangeMeters", 50)) {
                // TODO: earphones?
                event.getRecipients().remove(recipient);
                continue;
            }
            if (distance > plugin.getConfig().getInt("scrambleRangeMeters", 25)) {
                // TODO: delete random letters
            }
        }
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
