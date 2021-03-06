package com.sk89q.craftbook.mechanics.minecart;

import com.mcsunnyside.craftbooklimiter.QuotaManager;
import com.sk89q.craftbook.AbstractCraftBookMechanic;
import com.sk89q.craftbook.util.EventUtil;
import com.sk89q.util.yaml.YAMLProcessor;
import org.bukkit.entity.Minecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;

public class NoCollide extends AbstractCraftBookMechanic {

    public NoCollide(QuotaManager quotaManager) {
        super(quotaManager);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onCartCollision(VehicleEntityCollisionEvent event) {

        if(!EventUtil.passesFilter(event)) return;

        if (event.getVehicle() instanceof Minecart) {
            if (event.getVehicle().isEmpty() && !empty) return;
            if (!event.getVehicle().isEmpty() && !full) return;
            if (!quotaManager.tickAndCheckNext(event.getVehicle().getLocation().getChunk(), true, this.getClass())) {
                return;
            }
            event.setCollisionCancelled(true);
        }
    }

    private boolean empty;
    private boolean full;

    @Override
    public void loadConfiguration (YAMLProcessor config, String path) {

        config.setComment(path + "empty-carts", "Enable No Collide for empty carts.");
        empty = config.getBoolean(path + "empty-carts", true);

        config.setComment(path + "full-carts", "Enable No Collide for occupied carts.");
        full = config.getBoolean(path + "full-carts", false);
    }
}