package com.sk89q.craftbook.mechanics.minecart;

import com.mcsunnyside.craftbooklimiter.QuotaManager;
import com.sk89q.craftbook.AbstractCraftBookMechanic;
import com.sk89q.craftbook.util.EventUtil;
import com.sk89q.util.yaml.YAMLProcessor;
import org.bukkit.entity.Minecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.util.Vector;

public class SpeedModifiers extends AbstractCraftBookMechanic {

    public SpeedModifiers(QuotaManager quotaManager) {
        super(quotaManager);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onVehicleCreate(VehicleCreateEvent event) {

        if (!EventUtil.passesFilter(event)) return;

        if (!(event.getVehicle() instanceof Minecart)) return;

        if (!quotaManager.tickAndCheckNext(event.getVehicle().getLocation().getChunk(), true, this.getClass())) {
            return;
        }

        if (offRail > 0)
            ((Minecart) event.getVehicle()).setDerailedVelocityMod(new Vector(offRail, offRail, offRail));
        if (maxSpeed != 1)
            ((Minecart) event.getVehicle()).setMaxSpeed(((Minecart) event.getVehicle()).getMaxSpeed() * maxSpeed);
    }

    private double maxSpeed;
    private double offRail;

    @Override
    public void loadConfiguration (YAMLProcessor config, String path) {

        config.setComment(path + "max-speed", "Sets the max speed modifier of carts. Normal max speed speed is 0.4D");
        maxSpeed = config.getDouble(path + "max-speed", 1);

        config.setComment(path + "off-rail-speed", "Sets the off-rail speed modifier of carts. 0 is none.");
        offRail = config.getDouble(path + "off-rail-speed", 0);
    }
}