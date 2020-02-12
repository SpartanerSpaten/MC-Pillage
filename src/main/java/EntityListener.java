package com.einspaten.bukkit.mcpillage;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class EntityListener implements Listener {
    private com.einspaten.bukkit.mcpillage.MCPillagePlugin plugin;

    EntityListener(com.einspaten.bukkit.mcpillage.MCPillagePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    void handleEntitySpawn(EntitySpawnEvent event) {
        if (event.getEntityType() == EntityType.PRIMED_TNT) {
            Plot temp = this.plugin.db.loadPlot((int) event.getEntity().getLocation().getX(), (int) event.getEntity().getLocation().getZ());
            if (temp != null) {
                event.getEntity().setMetadata("x", new FixedMetadataValue(this.plugin, temp.getPos_x()));
                event.getEntity().setMetadata("z", new FixedMetadataValue(this.plugin, temp.getPos_z()));
                event.getEntity().setMetadata("size", new FixedMetadataValue(this.plugin, temp.getSize()));
            }

        }
    }


}
