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

import java.io.File;
import java.util.Random;


public class FarmWorld implements CommandExecutor {

    private final MCPillagePlugin plugin;

    public FarmWorld(MCPillagePlugin plugin) {
        this.plugin = plugin;
    }

    public static boolean deleteDirectory(File dir) {
        if (!dir.exists() || !dir.isDirectory()) {
            return false;
        }

        String[] files = dir.list();
        for (int i = 0, len = files.length; i < len; i++) {
            File f = new File(dir, files[i]);
            if (f.isDirectory()) {
                deleteDirectory(f);
            } else {
                f.delete();
            }
        }
        return dir.delete();
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
            } else if (split[0].equalsIgnoreCase("help")) {
                String playerMessage = "****** Farmworld Commands ****** \n"
                        + "§loverworld§r : Teleports you into the farm overworld\n"
                        + "§lnether§r : Teleports you into the nether - A save place will be generated but be careful\n"
                        + "§lend§r : Teleports you into the end\n";
                player.sendMessage(playerMessage);
            }
        }
        return true;
    }

    public boolean createWorld(String name, Player player) {
        player.sendMessage(name);

        deleteWorld(name);

        WorldCreator wc = new WorldCreator(name);
        long myseed = new Random().nextLong();
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
        Bukkit.broadcastMessage("§b§k=== §r Recreating world: " + name + " §b§k=== ");

        return true;
    }

    public boolean deleteWorld(String name) {
        World world = this.plugin.getServer().getWorld(name);
        if (world == null) {
            // We can only delete loaded worlds
            return false;
        }

        try {
            File worldFile = world.getWorldFolder();
            this.plugin.getServer().unloadWorld(name, false);
            return deleteDirectory(worldFile);
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }

}