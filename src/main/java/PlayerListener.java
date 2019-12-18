package com.einspaten.bukkit.mcpillage;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;

import java.util.Random;

/**
 * Handle events for all Player related events
 *
 * @author SpartanerSpaten
 */
public class PlayerListener implements Listener {
    private final MCPillagePlugin plugin;

    public PlayerListener(MCPillagePlugin instance) {
        this.plugin = instance;
    }

    @EventHandler
    public void playerInteract(PlayerInteractEvent playerInteractEvent) {
        if (playerInteractEvent.hasBlock() && playerInteractEvent.getClickedBlock().getType() == Material.ENDER_CHEST) {
            if (playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK) {
                playerInteractEvent.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayJoin(PlayerJoinEvent playerJoinEvent) {
        int team = plugin.getMemberShip(playerJoinEvent.getPlayer().getUniqueId​().toString());
        if (team == 1 || team == 2) {
            String teamColor = this.plugin.teamColor.get(team - 1);
            playerJoinEvent.setJoinMessage("Welcome back §d§l" + playerJoinEvent.getPlayer().getName() + "§r from " + teamColor + "Team" + team);
        }
    }


    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        int team = plugin.getMemberShip(event.getPlayer().getUniqueId​().toString());
        World world = Bukkit.getServer().getWorld("world");
        if (team == 1) {
            int topY = world.getHighestBlockYAt(this.plugin.factionTeam1.getMiddleX(), this.plugin.factionTeam1.getMiddleZ()) + 1;
            Location location = new Location(world, this.plugin.factionTeam1.getMiddleX(), topY, this.plugin.factionTeam1.getMiddleZ());
            event.setRespawnLocation(location);

        } else if (team == 2) {
            int topY = world.getHighestBlockYAt(this.plugin.factionTeam2.getMiddleX(), this.plugin.factionTeam2.getMiddleZ()) + 1;
            Location location = new Location(world, this.plugin.factionTeam2.getMiddleX(), topY, this.plugin.factionTeam2.getMiddleZ());
            event.setRespawnLocation(location);
        }
    }


    @EventHandler
    public void onTeleportation(PlayerTeleportEvent event) {
        PlayerTeleportEvent.TeleportCause cause = event.getCause();

        if (cause == PlayerTeleportEvent.TeleportCause.COMMAND || cause == PlayerTeleportEvent.TeleportCause.PLUGIN) {

            if (event.getPlayer().isOp() && event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                return;
            }

            // Tries too catch if
            Location desiredLoc = event.getTo();
            //if(desiredLoc.getWorld().getName().equalsIgnoreCase("world")){
            Player player = event.getPlayer();
            int team = plugin.getMemberShip(event.getPlayer().getUniqueId​().toString());
            World world = desiredLoc.getWorld();
            int deviationX = 0;
            int deviationZ = 0;
            int posX, posZ, posY;
            if (team == 1) {
                posX = 2500;
            } else {
                posX = -2500;
            }
            posZ = 0;
            if (!world.getName().equalsIgnoreCase("world")) {
                Random random = new Random();
                deviationX = random.nextInt() % 50;
                deviationZ = random.nextInt() % 50;
            }

            if (world.getName().equalsIgnoreCase("farm_end")) {
                posX = 0;
                posZ = 0;
            }
            Location location;
            if (!world.getName().equalsIgnoreCase("farm_nether")) {
                posY = world.getHighestBlockYAt(posX + deviationX, deviationZ) + 1;
                location = new Location(world, posX + deviationX, posY, posZ + deviationZ);
            } else {
                location = new Location(world, posX + deviationX, desiredLoc.getY() + 3, posZ + deviationZ);
                Block temp = location.add(0, -1, 0).getBlock();
                temp.setType(Material.AIR);
                temp = location.add(0, -1, 0).getBlock();
                temp.setType(Material.AIR);
                temp = location.add(0, -1, 0).getBlock();
                temp.setType(Material.NETHERRACK);
                location.add(0, 1, 0);

            }
            event.setTo(location);
        } else if (cause == PlayerTeleportEvent.TeleportCause.END_GATEWAY || cause == PlayerTeleportEvent.TeleportCause.END_PORTAL || cause == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getLogger().info(event.getPlayer().getName() + " joined the server! :D");

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        if (!plugin.eventManager.getWar()) {
            Player player = event.getPlayer();
            World world = Bukkit.getServer().getWorld("world");
            Location location = new Location(world, 0, 100, 0);
            player.teleport(location);
        }
    }
}