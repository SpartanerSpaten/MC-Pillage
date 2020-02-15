package com.einspaten.bukkit.mcpillage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CityCommand implements CommandExecutor {

    private static String help_page = ChatColor.GRAY + "City Help Page \n"
            + " * /city spawn \n";

    private final MCPillagePlugin plugin;

    public CityCommand(MCPillagePlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
        Player player = (Player) sender;
        if (split.length > 0) {
            if (split[0].equalsIgnoreCase("spawn")) {
                World world = Bukkit.getServer().getWorld("world");
                Location location = new Location(world, 0, world.getHighestBlockYAt(0, 0), 0);
                player.teleport(location);
                return true;
            }
        }
        player.sendMessage(help_page);
        return true;
    }
}
