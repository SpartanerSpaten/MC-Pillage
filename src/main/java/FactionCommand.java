package com.einspaten.bukkit.mcpillage;

import org.bukkit.World;
import org.bukkit.Location;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FactionCommand implements CommandExecutor {

    private final MCPillagePlugin plugin;

    public FactionCommand(MCPillagePlugin plugin){
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
        Player player = (Player) sender;
        if(split.length > 0){
            if(split[0].equalsIgnoreCase("spawn")){
                // Only Trigger Teleport Event Teleport Handler will figure it out where Player should go
                World world = Bukkit.getServer().getWorld("world");
                Location location = new Location(world, 0, 100, 0);
                player.teleport(location);

            }else if (split[0].equalsIgnoreCase("info")){

            } else if (split[0].equalsIgnoreCase("members")){

            } else if(split[0].equalsIgnoreCase("promote")){
                // Whitelist, Ban, Pin Blackboard
                // Only 2 Roles

            }
        }
        return true;
    }
}