package com.einspaten.bukkit.mcpillage;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Objects;

/**
 * Sample plugin for Bukkit
 *
 * @author SpartanerSpaten
 */
public class MCPillagePlugin extends JavaPlugin {

    public final Database db = new Database();
    private final PlayerListener playerListener = new PlayerListener(this);
    private final BlockListener blockListener = new BlockListener(this);
    private final EntityListener entityListener = new EntityListener(this);
    private static PluginManager pm;
    private final HashMap<String, PluginPlayer> members = new HashMap<String, PluginPlayer>();

    public static PluginManager getPluginManager() {
        return pm;
    }

    public void addPlayer(String uuid) {
        members.put(uuid, this.db.loadPlayer(uuid));
    }

    public void removePlayer(String uuid) {
        members.remove(uuid);
    }

    public PluginPlayer[] getAllPlayers() {
        return members.values().toArray(new PluginPlayer[0]);
    }

    public PluginPlayer getPlayer(String uuid) {
        return members.get(uuid);
    }

    @Override
    public void onDisable() {
        this.db.close();
    }

    @Override
    public void onEnable() {

        // Register our events
        pm = getServer().getPluginManager();
        pm.registerEvents(playerListener, this);
        pm.registerEvents(blockListener, this);
        pm.registerEvents(entityListener, this);

        com.einspaten.bukkit.mcpillage.FarmWorld farm_world = new com.einspaten.bukkit.mcpillage.FarmWorld(this);

        getCommand("farm").setExecutor(farm_world);
        getCommand("eggshop").setExecutor(new ShopClass(this));
        getCommand("home").setExecutor(new HomeCommand(this));
        getCommand("city").setExecutor(new CityCommand(this));
        getCommand("money").setExecutor(new com.einspaten.bukkit.mcpillage.MoneyCommand(this));
        getCommand("plot").setExecutor(new com.einspaten.bukkit.mcpillage.PlotCommand(this));

        // Loads all farm worlds or creates them when necessary
        farm_world.createWorld("farm_world");
        farm_world.createWorld("farm_nether");
        farm_world.createWorld("farm_end");

        Bukkit.getServer().setWhitelist(false);
        Objects.requireNonNull(Bukkit.getWorld("world")).setDifficulty(Objects.requireNonNull(Difficulty.HARD));

    }
}