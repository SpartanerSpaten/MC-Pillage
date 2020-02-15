package com.einspaten.bukkit.mcpillage;

import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Random;


public class FarmWorld implements CommandExecutor {

    private static String playerMessage = ChatColor.GRAY + "Farm World Help Page \n"
            + " * /farm overworld Teleports you into the farm overworld\n"
            + " * /farm nether : Teleports you into the nether \n"
            + " * /farm end: Teleports you into the end\n";

    private final MCPillagePlugin plugin;

    public FarmWorld(MCPillagePlugin plugin) {
        this.plugin = plugin;
        createWorld("farm_end");
        createWorld("farm_nether");
        createWorld("farm_world");
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
                    player.sendMessage(ChatColor.RED + "ERROR: World not found pls contact a system admin");
                    return true;
                }
                Location location = new Location(world, 0, 100, 0);
                player.teleport(location);
            } else if (split[0].equalsIgnoreCase("end")) {
                World world = Bukkit.getServer().getWorld("farm_end");
                if (world == null) {
                    player.sendMessage(ChatColor.RED + "ERROR: World not found pls contact a system admin");
                    return true;
                }
                Location location = new Location(world, 0, world.getHighestBlockYAt(0, 0) + 1, 0);
                player.teleport(location);
            } else if (split[0].equalsIgnoreCase("overworld")) {
                World world = Bukkit.getServer().getWorld("farm_world");
                if (world == null) {
                    player.sendMessage("World not found pls contact a system admin");
                    return true;
                }
                Location location = new Location(world, 0, world.getHighestBlockYAt(0, 0) + 1, 0);
                player.teleport(location);
            } else if (split[0].equalsIgnoreCase("create") && player.isOp()) {
                if (split.length > 1) {
                    save_players(split[1]); // Teleports players
                    createWorld(split[1]); // Deletes world file and creates new one
                } else {
                    player.sendMessage("Not enough arguments");
                }
            } else {

                player.sendMessage(playerMessage);
            }
        } else {
            player.sendMessage(playerMessage);
        }
        return true;
    }

    private void save_players(String name) {

        // Teleports player out of farm world when resettet
        World world = Bukkit.getWorld(name);
        World build_world = Bukkit.getWorld("world");
        if (world != null && build_world != null) {
            int y = build_world.getHighestBlockYAt(0, 0);
            for (Player player : world.getPlayers()) {
                player.sendMessage(ChatColor.GRAY + "Duo a reset of the world you are in we teleport you too the build world spawn !");
                player.teleport(new Location(world, 0, y, 0));
            }
        }
    }


    public boolean createWorld(String name) {

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
        if (w != null) {
            w.setAutoSave(true);
        }

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