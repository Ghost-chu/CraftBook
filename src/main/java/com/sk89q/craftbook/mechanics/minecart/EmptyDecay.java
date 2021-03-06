package com.sk89q.craftbook.mechanics.minecart;

import com.mcsunnyside.craftbooklimiter.QuotaManager;
import com.sk89q.craftbook.AbstractCraftBookMechanic;
import com.sk89q.craftbook.bukkit.CraftBookPlugin;
import com.sk89q.craftbook.util.EventUtil;
import com.sk89q.util.yaml.YAMLProcessor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.world.ChunkLoadEvent;

public class EmptyDecay extends AbstractCraftBookMechanic {

    public EmptyDecay(QuotaManager quotaManager) {
        super(quotaManager);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onVehicleExit(VehicleExitEvent event) {

        if (!EventUtil.passesFilter(event)) return;

        Vehicle vehicle = event.getVehicle();

        if (!(vehicle instanceof RideableMinecart)) return;
        if (!quotaManager.tickAndCheckNext(event.getExited().getLocation().getChunk(), true, this.getClass())) {
            return;
        }
        CraftBookPlugin.inst().getServer().getScheduler().runTaskLater(CraftBookPlugin.inst(), new Decay((RideableMinecart) vehicle), delay);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onChunkLoad(ChunkLoadEvent event) {

        if(!EventUtil.passesFilter(event)) return;

        for (Entity ent : event.getChunk().getEntities()) {
            if (ent == null || !ent.isValid())
                continue;
            if (!(ent instanceof RideableMinecart))
                continue;
            if (!ent.isEmpty())
                continue;
            CraftBookPlugin.inst().getServer().getScheduler().runTaskLater(CraftBookPlugin.inst(), new Decay((RideableMinecart) ent), delay);
        }
    }

    private static class Decay implements Runnable {

        RideableMinecart cart;

        Decay(RideableMinecart cart) {

            this.cart = cart;
        }

        @Override
        public void run() {

            if (cart == null || !cart.isValid() || !cart.isEmpty()) return;
            cart.remove();
        }
    }

    private int delay;

    @Override
    public void loadConfiguration (YAMLProcessor config, String path) {

        config.setComment(path + "time-in-ticks", "The time in ticks that the cart will wait before decaying.");
        delay = config.getInt(path + "time-in-ticks", 20);
    }
}