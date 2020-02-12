package com.einspaten.bukkit.mcpillage;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.Objects;

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
            if (event.getEntityType() == EntityType.PRIMED_TNT) {
                if (event.getEntity().hasMetadata("x")) {
                    try {
                        int tnt_x = (int) event.getEntity().getLocation().getX();
                        int tnt_z = (int) event.getEntity().getLocation().getZ();
                        int pos_x = (int) (event.getEntity().getMetadata("x").get(0).value());
                        int pos_z = (int) (event.getEntity().getMetadata("z").get(0).value());
                        int size = (int) (event.getEntity().getMetadata("size").get(0).value());
                        if (!((tnt_x < pos_x + size && tnt_x > pos_x - size - 1) && (tnt_z < pos_z + size && tnt_z > pos_z - size - 1))) {
                            event.setCancelled(true);
                            event.blockList().clear(); // Maybe clear all Blocks
                            return;
                        } else {
                            // Outer Circle where blocks could be destroyed that did not belong to gs
                            Location loc;
                            for (int i = 0; i < event.blockList().size(); i++) {
                                loc = event.blockList().get(i).getLocation();
                                if (!((loc.getX() < pos_x + size && loc.getX() > pos_x - size - 1) && (loc.getZ() < pos_z + size && loc.getZ() > pos_z - size - 1))) {
                                    event.blockList().remove(i);
                                    i--;
                                }
                            }
                        }
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                        event.setCancelled(true);
                        event.blockList().clear(); // Maybe clear all Blocks
                        return;
                    }

                }
            }
            event.setCancelled(true);
            event.blockList().clear(); // Maybe clear all Blocks
        }
    }

    @EventHandler
    public void onPistonExtendEvent(BlockPistonExtendEvent event) {
        Location piston_location = event.getBlock().getLocation();
        Plot plot = this.plugin.db.loadPlot((int) piston_location.getX(), (int) piston_location.getZ());

        if (plot == null) {
            return;
        }

        int pos_x = plot.getPos_x();
        int pos_z = plot.getPos_z();
        int size = plot.getSize();

        Location loc;
        for (Block block : event.getBlocks()) {
            loc = block.getLocation();
            if (!((loc.getX() < pos_x + size && loc.getX() > pos_x - size - 1) && (loc.getZ() < pos_z + size && loc.getZ() > pos_z - size - 1))) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onPistonRetractEvent(BlockPistonRetractEvent event) {
        Location piston_location = event.getBlock().getLocation();
        Plot plot = this.plugin.db.loadPlot((int) piston_location.getX(), (int) piston_location.getZ());

        if (plot == null) {
            return;
        }

        int pos_x = plot.getPos_x();
        int pos_z = plot.getPos_z();
        int size = plot.getSize();

        Location loc;
        for (Block block : event.getBlocks()) {
            loc = block.getLocation();
            if (!((loc.getX() < pos_x + size && loc.getX() > pos_x - size - 1) && (loc.getZ() < pos_z + size && loc.getZ() > pos_z - size - 1))) {
                event.setCancelled(true);
                return;
            }
        }
    }


    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        if (Objects.requireNonNull(event.getIgnitingBlock()).getLocation().getWorld().getName().equalsIgnoreCase("world")) {
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
        if (event.getSource().getLocation().getWorld().getName().equalsIgnoreCase("world")) {
            if (event.getSource().getType() == org.bukkit.Material.FIRE) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        if (!event.getPlayer().isOp()) {
            if (event.getBlock().getWorld().getName().equalsIgnoreCase("world")) {

                if (!this.plugin.getPlayer(event.getPlayer().getUniqueId().toString()).onMyPlot((int) location.getX(), (int) location.getZ())) {
                    if (event.getPlayer().isOp()) {
                        event.getPlayer().sendMessage(ChatColor.GRAY + "You are not on your plot !");
                    } else {
                        event.setCancelled(true);
                    }
                }
            } else {
                if (location.getX() < 10 && location.getX() > -10 && location.getZ() < 10 && location.getZ() < -10) {
                    event.setCancelled(!event.getPlayer().isOp());
                }
            }
        } else {
            event.getPlayer().sendMessage(ChatColor.GRAY + "You a");
        }

    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        Location location = event.getBlock().getLocation();

        if (event.getBlock().getWorld().getName().equalsIgnoreCase("world")) {

            if (!this.plugin.getPlayer(event.getPlayer().getUniqueId().toString()).onMyPlot((int) location.getX(), (int) location.getZ())) {
                if (event.getPlayer().isOp()) {
                    event.getPlayer().sendMessage(ChatColor.GRAY + "You are not on your plot !");
                } else {
                    event.setCancelled(true);
                }
            }
        } else {

            if (location.getX() < 10 && location.getX() > -10 && location.getZ() < 10 && location.getZ() < -10) {
                event.setCancelled(!event.getPlayer().isOp());
            }
        }
    }

}