package com.einspaten.bukkit.mcpillage;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Sample plugin for Bukkit
 *
 * @author SpartanerSpaten
 */
public class MCPillagePlugin extends JavaPlugin {

    public final DataBase db = new DataBase();
    public final Faction factionTeam1 = new Faction(2500, 0, 1, this);
    public final Faction factionTeam2 = new Faction(-2500, 0, 2, this);
    public final EventManager eventManager = new EventManager(this);

    private final PlayerListener playerListener = new PlayerListener(this);
    private final BlockListener blockListener = new BlockListener(this);

    private final HashMap<String, Number> members = new HashMap<String, Number>();

    public ArrayList<String> teamColor = new ArrayList<String>();

    public void readFromFactions() {
        for (String uuid : factionTeam1.members) {
            getLogger().info("Team: 1 " + uuid);
            members.put(uuid, 1);
        }
        for (String uuid : factionTeam2.members) {
            getLogger().info("Team: 2 " + uuid);
            members.put(uuid, 2);
        }
    }

    public int getMemberShip(String uuid) {
        Number team = members.get(uuid);
        if (team != null) {
            return team.intValue();
        } else {
            return -1;
        }
    }

    public void addPlayer(String uuid, int team) {
        members.put(uuid, team);
        if (team == 1) {
            factionTeam1.addMember(uuid);
        } else {
            factionTeam2.addMember(uuid);
        }
    }

    public void removePlayer(String uuid, int team) {
        members.remove(uuid);
        if (team == 1) {
            factionTeam1.removeMember(uuid);
        } else {
            factionTeam2.removeMember(uuid);
        }
    }


    @Override
    public void onDisable() {
        this.db.close();
    }

    @Override
    public void onEnable() {

        // Register our events
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(playerListener, this);
        pm.registerEvents(blockListener, this);

        getCommand("war").setExecutor(new WarCommand(this));
        getCommand("faction").setExecutor(new FactionCommand(this));
        getCommand("farm").setExecutor(new FarmWorld(this));

        teamColor.add("§c");
        teamColor.add("§9");

        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        PluginDescriptionFile pdfFile = this.getDescription();
        getLogger().info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");

        Bukkit.getServer().setWhitelist(true);
        readFromFactions();
    }
}