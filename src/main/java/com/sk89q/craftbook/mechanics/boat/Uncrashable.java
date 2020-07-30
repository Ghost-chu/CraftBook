package com.sk89q.craftbook.mechanics.boat;

import com.mcsunnyside.craftbooklimiter.QuotaManager;
import org.bukkit.entity.Boat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.util.Vector;

import com.sk89q.craftbook.AbstractCraftBookMechanic;
import com.sk89q.craftbook.util.EventUtil;
import com.sk89q.util.yaml.YAMLProcessor;

public class Uncrashable extends AbstractCraftBookMechanic {

    public Uncrashable(QuotaManager quotaManager) {
        super(quotaManager);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onVehicleDestroy(VehicleDestroyEvent event) {

        if(!EventUtil.passesFilter(event)) return;

        if (!(event.getVehicle() instanceof Boat)) return;
        if(!quotaManager.tickAndCheckNext(event.getVehicle().getLocation().getChunk(), true ,this.getClass())){
            return;
        }
        if (event.getAttacker() == null) {
            event.getVehicle().setVelocity(new Vector(0, 0, 0));
            event.setCancelled(true);
        }
    }

    @Override
    public void loadConfiguration (YAMLProcessor config, String path) {

    }
}