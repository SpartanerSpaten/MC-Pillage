package com.einspaten.bukkit.mcpillage;

import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;


public class AutoTrigger implements Listener {

    private final MCPillagePlugin plugin;

    private BukkitTask task;

    public AutoTrigger(MCPillagePlugin plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
        task = new com.einspaten.bukkit.mcpillage.WarTask().runTaskTimer(this.plugin, 10, 1000);
    }
}
