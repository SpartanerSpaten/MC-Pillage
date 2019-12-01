package com.einspaten.bukkit.mcpillage;

import java.util.Random;

import org.bukkit.World;
import org.bukkit.Location;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 * Handle events for all Player related events
 * @author SpartanerSpaten
 */
public class PlayerListener implements Listener {
    private final MCPillagePlugin plugin;

    public PlayerListener(MCPillagePlugin instance) {
        plugin = instance;
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event){
        Player player = event.getPlayer();
        int team = plugin.getMemberShip(event.getPlayer().getName());
        World world = Bukkit.getServer().getWorld("world");
        if(team == 1){
            int topY = world.getHighestBlockYAt(2500, 0) + 1;
            Location location = new Location(world, 2500, topY, 0);
            event.setRespawnLocation(location);

        } else{
            int topY = world.getHighestBlockYAt(-2500, 0) + 1;
            Location location = new Location(world, -2500, topY, 0);
            event.setRespawnLocation(location);
        }
    }


    @EventHandler
    public void onTeleportation(PlayerTeleportEvent event){
        PlayerTeleportEvent.TeleportCause cause = event.getCause();

        if(cause == PlayerTeleportEvent.TeleportCause.COMMAND || cause == PlayerTeleportEvent.TeleportCause.PLUGIN){
            // Tries too catch if
            Location desiredLoc = event.getTo();
            //if(desiredLoc.getWorld().getName().equalsIgnoreCase("world")){
            Player player = event.getPlayer();
            int team = plugin.getMemberShip(event.getPlayer().getName());
            World world = desiredLoc.getWorld();
            int deviationX = 0;
            int deviationZ = 0;
            int posX, posZ, posY;

            if(world.getName().equalsIgnoreCase("world") && world.getName().equalsIgnoreCase("nether_farming")){
                Random random = new Random();
                deviationX = random.nextInt() % 100;
                deviationZ = random.nextInt() % 100;
            }
            if(team == 1){
                posX = 2500; posZ = 0;
            }else{
                posX = -2500; posZ = 0;
            }
            if(!world.getName().equalsIgnoreCase("nether_farming")){
                posY = world.getHighestBlockYAt(posX + deviationX, deviationZ) + 1;
            }else {
                posY = (int)desiredLoc.getY();
            }

            Location location = new Location(world, posX + deviationX, posY, posZ + deviationZ);
            event.setTo(location);
        }
        else if(cause == PlayerTeleportEvent.TeleportCause.END_GATEWAY || cause == PlayerTeleportEvent.TeleportCause.END_PORTAL || cause == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getLogger().info(event.getPlayer().getName() + " joined the server! :D");

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        if(!plugin.eventManager.getWar()){
            Player player = event.getPlayer();
            World world = Bukkit.getServer().getWorld("world");
            Location location = new Location(world, 0, 100, 0);
            player.teleport(location);
        }
        plugin.getLogger().info(event.getPlayer().getName() + " left the server! :'(");
        //Location location = new Location(world, x, y, z);
        //player.teleport(location);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

    }
}