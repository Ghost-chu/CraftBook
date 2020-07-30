package com.sk89q.craftbook.mechanics.minecart;

import com.mcsunnyside.craftbooklimiter.QuotaManager;
import com.sk89q.craftbook.AbstractCraftBookMechanic;
import com.sk89q.craftbook.util.EventUtil;
import com.sk89q.util.yaml.YAMLProcessor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;

public class CollisionEntry extends AbstractCraftBookMechanic {

    public CollisionEntry(QuotaManager quotaManager) {
        super(quotaManager);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onVehicleEntityCollision(VehicleEntityCollisionEvent event) {

        if(!EventUtil.passesFilter(event)) return;

        if (event.getVehicle() instanceof RideableMinecart) {
            if (!event.getVehicle().isEmpty()) return;
            if (!(event.getEntity() instanceof HumanEntity)) return;
            if (!quotaManager.tickAndCheckNext(event.getEntity().getLocation().getChunk(), true, this.getClass())) {
                return;
            }
            event.getVehicle().addPassenger(event.getEntity());

            event.setCollisionCancelled(true);
        }
    }

    @Override
    public void loadConfiguration (YAMLProcessor config, String path) {

    }
}