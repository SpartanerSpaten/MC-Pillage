package com.einspaten.bukkit.mcpillage;

import java.util.HashMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Sample plugin for Bukkit
 *
 * @author Dinnerbone
 */
public class MCPillagePlugin extends JavaPlugin {

    public final Faction factionTeam1 = new Faction(2500, 0, 1, this);
    public final Faction factionTeam2 = new Faction(-2500, 0, 2, this);
    public final EventManager eventManager = new EventManager(this);


    private final SamplePlayerListener playerListener = new SamplePlayerListener(this);
    private final SampleBlockListener blockListener = new SampleBlockListener(this);


    private final HashMap<String, Number> members = new HashMap<String, Number>();

    int getMemberShip(String user){
        // Read DB Here or load it before hand or call Usermanager

        Number team = members.get(user);
        if(team != null){
            return team.intValue();
        } else{
            return -1;
        }
    }


    @Override
    public void onDisable() {
        // TODO: Place any custom disable code here

        // NOTE: All registered events are automatically unregistered when a plugin is disabled

        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        getLogger().info("Goodbye world!");
    }

    @Override
    public void onEnable() {
        // TODO: Place any custom enable code here including the registration of any events

        // Register our events
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(playerListener, this);
        pm.registerEvents(blockListener, this);

        getCommand("war").setExecutor(new  SampleStartWar(this));
        getCommand("faction").setExecutor(new FactionCommand(this));

        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        PluginDescriptionFile pdfFile = this.getDescription();
        getLogger().info( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );

        members.put("Spartaner44", 2);

    }
}