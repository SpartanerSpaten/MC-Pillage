package com.einspaten.bukkit.mcpillage;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

/**
 * Sample block listener
 *
 * @author SpartanerSpaten
 */
public class BlockListener implements Listener {

    private final MCPillagePlugin plugin;

    public BlockListener(MCPillagePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockExplode(EntityExplodeEvent event) {
        /*
         * Prevents any damage by any explosion
         */
        if (event.getLocation().getWorld().getName().equalsIgnoreCase("world")) {
            event.setCancelled(true);
            event.blockList().clear();
        }
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        event.setCancelled(true);
    }

    public void onBlockIgnite(BlockIgniteEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getWorld().getName().equalsIgnoreCase("world")) {
            if (plugin.factionTeam1.atSpawn(event.getBlock().getX(), event.getBlock().getZ())) {
                // Spawn of Team1
                boolean memberOfTeam2 = plugin.factionTeam2.isMember(event.getPlayer().getUniqueId​().toString());
                if (memberOfTeam2) {
                    event.setCancelled(true);
                    return;
                }
                int role = plugin.factionTeam1.getRoleUUid(event.getPlayer().getUniqueId().toString());
                if (role != 2) {
                    event.setCancelled(true);
                }
                return;
            } else if (plugin.factionTeam2.atSpawn(event.getBlock().getX(), event.getBlock().getZ())) {
                // Spawn of Team2
                boolean memberOfTeam1 = plugin.factionTeam1.isMember(event.getPlayer().getUniqueId​().toString());
                if (memberOfTeam1) {
                    event.setCancelled(true);
                    return;
                }
                int role = plugin.factionTeam2.getRoleUUid(event.getPlayer().getUniqueId().toString());
                if (role != 2) {
                    event.setCancelled(true);
                }
                return;
            } else if (plugin.factionTeam1.permissions(event.getBlock().getX(), event.getBlock().getZ())) {
                int attackingTeam = plugin.eventManager.getCurrentlyAttacking();
                boolean memberOfTeam2 = plugin.factionTeam2.isMember(event.getPlayer().getUniqueId​().toString());
                if (memberOfTeam2 && attackingTeam != 2) {
                    event.setCancelled(true);
                }
                return;

            } else if (plugin.factionTeam2.permissions(event.getBlock().getX(), event.getBlock().getZ())) {
                int attackingTeam = plugin.eventManager.getCurrentlyAttacking();
                boolean memberOfTeam1 = plugin.factionTeam1.isMember(event.getPlayer().getUniqueId​().toString());
                if (memberOfTeam1 && attackingTeam != 1) {
                    event.setCancelled(true);
                }
                return;
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlock().getWorld().getName().equalsIgnoreCase("world")) {
            if (plugin.factionTeam1.atSpawn(event.getBlock().getX(), event.getBlock().getZ())) {
                // Spawn of Team1
                boolean memberOfTeam2 = plugin.factionTeam2.isMember(event.getPlayer().getUniqueId​().toString());
                if (memberOfTeam2) {
                    event.setCancelled(true);
                    return;
                }
                int role = plugin.factionTeam1.getRoleUUid(event.getPlayer().getUniqueId().toString());
                if (role != 2) {
                    event.setCancelled(true);
                }
                return;
            } else if (plugin.factionTeam2.atSpawn(event.getBlock().getX(), event.getBlock().getZ())) {
                // Spawn of Team2
                boolean memberOfTeam1 = plugin.factionTeam1.isMember(event.getPlayer().getUniqueId​().toString());
                if (memberOfTeam1) {
                    event.setCancelled(true);
                    return;
                }
                int role = plugin.factionTeam2.getRoleUUid(event.getPlayer().getUniqueId().toString());
                if (role != 2) {
                    event.setCancelled(true);
                }
                return;
            } else if (plugin.factionTeam1.permissions(event.getBlock().getX(), event.getBlock().getZ())) {
                int attackingTeam = plugin.eventManager.getCurrentlyAttacking();
                boolean memberOfTeam2 = plugin.factionTeam2.isMember(event.getPlayer().getUniqueId​().toString());
                if (memberOfTeam2 && attackingTeam != 2) {
                    event.setCancelled(true);
                }
                return;

            } else if (plugin.factionTeam2.permissions(event.getBlock().getX(), event.getBlock().getZ())) {
                int attackingTeam = plugin.eventManager.getCurrentlyAttacking();
                boolean memberOfTeam1 = plugin.factionTeam1.isMember(event.getPlayer().getUniqueId​().toString());
                if (memberOfTeam1 && attackingTeam != 1) {
                    event.setCancelled(true);
                }
                return;
            }
            event.setCancelled(true);
        }
    }

}