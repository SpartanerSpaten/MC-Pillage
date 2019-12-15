package com.einspaten.bukkit.mcpillage;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Random;


public class FarmWorld implements CommandExecutor {

    private final MCPillagePlugin plugin;

    public FarmWorld(MCPillagePlugin plugin) {
        this.plugin = plugin;
    }

    public boolean createWorld(String name, Player player) {
        player.sendMessage(name);
        if (name.equalsIgnoreCase("farm_nether") && name.equalsIgnoreCase("farm_end") && name.equalsIgnoreCase("farm_world")) {
            player.sendMessage("Invalid world name. Valid world name is one of 'farm_nether', 'farm_end' or 'farm_world'.");
            return false;
        }

        WorldCreator wc = new WorldCreator(name);
        long myseed = new Random().nextLong();
        player.sendMessage("Using seed: " + myseed);
        wc.seed(myseed);

        if (name.equalsIgnoreCase("farm_nether")) {
            wc.environment(Environment.NETHER);
        } else if (name.equalsIgnoreCase("farm_end")) {
            wc.environment(Environment.THE_END);
        } else if (name.equalsIgnoreCase("farm_world")) {
            wc.environment(Environment.NORMAL);
        }

        Bukkit.getServer().unloadWorld(name, false);
        World w = Bukkit.getServer().createWorld(wc);
        w.setAutoSave(true);
        Bukkit.broadcastMessage("Recreating world: " + name);

        return true;
    }


    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
        Player player = (Player) sender;

        if (split.length > 0) {
            if (split[0].equalsIgnoreCase("nether")) {
                World world = Bukkit.getServer().getWorld("farm_nether");
                if (world == null) {
                    createWorld("farm_nether", player);
                    world = Bukkit.getServer().getWorld("farm_nether");
                }
                Location location = new Location(world, 0, 100, 0);
                player.teleport(location);
            } else if (split[0].equalsIgnoreCase("end")) {
                World world = Bukkit.getServer().getWorld("farm_end");
                if (world == null) {
                    createWorld("farm_end", player);
                    world = Bukkit.getServer().getWorld("farm_end");
                }
                Location location = new Location(world, 0, 100, 0);
                player.teleport(location);
            } else if (split[0].equalsIgnoreCase("overworld")) {
                World world = Bukkit.getServer().getWorld("farm_world");
                if (world == null) {
                    createWorld("farm_world", player);
                    world = Bukkit.getServer().getWorld("farm_world");
                }
                Location location = new Location(world, 0, 100, 0);
                player.teleport(location);
            } else if (split[0].equalsIgnoreCase("create") && player.isOp()) {
                if (split.length > 1) {
                    createWorld(split[1], player);
                } else {
                    player.sendMessage("Not enough arguments");
                }
            }
        }
        return true;
    }
}