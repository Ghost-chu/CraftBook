package com.sk89q.craftbook.mechanics.minecart;

import com.mcsunnyside.craftbooklimiter.QuotaManager;
import com.sk89q.craftbook.AbstractCraftBookMechanic;
import com.sk89q.craftbook.util.EventUtil;
import com.sk89q.util.yaml.YAMLProcessor;
import org.bukkit.entity.Minecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.vehicle.VehicleCreateEvent;

public class EmptySlowdown extends AbstractCraftBookMechanic {

    public EmptySlowdown(QuotaManager quotaManager) {
        super(quotaManager);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onVehicleCreate(VehicleCreateEvent event) {

        if (!EventUtil.passesFilter(event)) return;

        if (!(event.getVehicle() instanceof Minecart)) return;

        if (!quotaManager.tickAndCheckNext(event.getVehicle().getLocation().getChunk(), true, this.getClass())) {
            return;
        }

        ((Minecart) event.getVehicle()).setSlowWhenEmpty(false);
    }

    @Override
    public void loadConfiguration (YAMLProcessor config, String path) {

    }
}