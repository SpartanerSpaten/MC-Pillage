package com.einspaten.bukkit.mcpillage;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Sample block listener
 * @author Dinnerbone
 */
public class BlockListener implements Listener {

    private final MCPillagePlugin plugin;

    public BlockListener(MCPillagePlugin plugin){
        this.plugin = plugin;
    }

    public void test(){
        String test = "Spartaner44";
        if(!plugin.factionTeam2.isMember(test)){
            plugin.factionTeam2.addMember(test);
        }
    }

    @EventHandler
    public void onBlockExplode(EntityExplodeEvent event) {
        /*
         * Prevents any damage by any explosion
         */
        event.setCancelled(true);
        event.blockList().clear();

    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        test();
        if(event.getBlock().getWorld().getName().equalsIgnoreCase("world")){
            if(plugin.factionTeam1.permissions(event.getBlock().getX(),event.getBlock().getZ())){
                int attackingTeam = plugin.eventManager.getCurrentlyAttacking();
                if(plugin.factionTeam2.isMember(event.getPlayer().getName()) && !(attackingTeam == 1)){
                    event.setCancelled(true);
                }
                return;

            } else if(plugin.factionTeam2.permissions(event.getBlock().getX(),event.getBlock().getZ())){
                int attackingTeam = plugin.eventManager.getCurrentlyAttacking();
                if(plugin.factionTeam1.isMember(event.getPlayer().getName()) && !(attackingTeam == 2)){
                    event.setCancelled(true);
                }
                return;
            }
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        test();
        if(event.getBlock().getWorld().getName().equalsIgnoreCase("world")){
            if(plugin.factionTeam1.permissions(event.getBlock().getX(),event.getBlock().getZ())){
                int attackingTeam = plugin.eventManager.getCurrentlyAttacking();
                if(plugin.factionTeam2.isMember(event.getPlayer().getName()) && !(attackingTeam == 1)){
                    event.setCancelled(true);
                }
                return;

            } else if(plugin.factionTeam2.permissions(event.getBlock().getX(),event.getBlock().getZ())){
                int attackingTeam = plugin.eventManager.getCurrentlyAttacking();
                if(plugin.factionTeam1.isMember(event.getPlayer().getName()) && !(attackingTeam == 2)){
                    event.setCancelled(true);
                }
                return;
            }
            event.setCancelled(true);
        }
    }

}