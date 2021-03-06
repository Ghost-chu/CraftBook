package com.sk89q.craftbook.mechanics.minecart;

import com.mcsunnyside.craftbooklimiter.QuotaManager;
import com.sk89q.craftbook.AbstractCraftBookMechanic;
import com.sk89q.craftbook.util.EventUtil;
import com.sk89q.util.yaml.YAMLProcessor;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.vehicle.VehicleEnterEvent;

public class MobBlocker extends AbstractCraftBookMechanic {

    public MobBlocker(QuotaManager quotaManager) {
        super(quotaManager);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onVehicleEnter(VehicleEnterEvent event) {

        if (!EventUtil.passesFilter(event)) return;

        if (!event.getVehicle().getWorld().isChunkLoaded(event.getVehicle().getLocation().getBlockX() >> 4, event.getVehicle().getLocation().getBlockZ() >> 4))
            return;

        Vehicle vehicle = event.getVehicle();

        if (!(vehicle instanceof Minecart)) return;

        if (!quotaManager.tickAndCheckNext(event.getVehicle().getLocation().getChunk(), true, this.getClass())) {
            return;
        }

        if (!(event.getEntered() instanceof Player))
            event.setCancelled(true);
    }

    @Override
    public void loadConfiguration (YAMLProcessor config, String path) {

    }
}