package com.einspaten.bukkit.mcpillage;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HomeCommand implements CommandExecutor {

    private final MCPillagePlugin plugin;

    public HomeCommand(MCPillagePlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {

        Player player = (Player) sender;

        com.einspaten.bukkit.mcpillage.PluginPlayer plugin_player = this.plugin.getPlayer(player.getUniqueId().toString());

        if (split.length > 0 && split[0].equalsIgnoreCase("set")) {
            if (player.getLocation().getWorld().getName().equalsIgnoreCase("world")) {
                player.sendMessage(ChatColor.GRAY + "You only can set your home point in the build world !");
                return false;
            }
            plugin_player.setHome_x((int) player.getLocation().getX());
            plugin_player.setHome_y((int) player.getLocation().getY());
            plugin_player.setHome_z((int) player.getLocation().getZ());
            this.plugin.db.updateHome(player.getUniqueId().toString(), (int) player.getLocation().getX(), (int) player.getLocation().getY(), (int) player.getLocation().getZ());
        } else {
            player.sendMessage(plugin_player.teleportHome());
        }

        return true;
    }
}
