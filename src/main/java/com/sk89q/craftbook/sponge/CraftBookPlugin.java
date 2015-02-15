package com.sk89q.craftbook.sponge;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.state.ServerStartingEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.util.event.Subscribe;

import com.sk89q.craftbook.core.CraftBookAPI;
import com.sk89q.craftbook.core.Mechanic;
import com.sk89q.craftbook.core.util.MechanicDataCache;
import com.sk89q.craftbook.sponge.mechanics.Elevator;
import com.sk89q.craftbook.sponge.mechanics.Snow;
import com.sk89q.craftbook.sponge.mechanics.area.Bridge;
import com.sk89q.craftbook.sponge.mechanics.area.Door;
import com.sk89q.craftbook.sponge.mechanics.area.Gate;
import com.sk89q.craftbook.sponge.mechanics.minecart.EmptyDecay;
import com.sk89q.craftbook.sponge.util.SpongeDataCache;

@Plugin(id = "CraftBook", name = "CraftBook", version = "4.0"/*, dependencies = "required-after:WorldEdit@[6.0,)"*/)
public class CraftBookPlugin extends CraftBookAPI {

    public static Game game;

    private Set<Mechanic> enabledMechanics = new HashSet<Mechanic>();

    public static Logger logger = LoggerFactory.getLogger(CraftBookPlugin.class);

    MechanicDataCache cache;

    @Subscribe
    public void onPreInitialization(ServerStartingEvent event) {

        game = event.getGame();
        instance = this;

        logger.info("Starting CraftBook");

        cache = new SpongeDataCache();

        discoverMechanics();

        for(Class<? extends Mechanic> mech : getAvailableMechanics()) {

            //TODO is enabled check.

            try {
                Mechanic mechanic = createMechanic(mech);
                enabledMechanics.add(mechanic);
                game.getEventManager().register(this, mechanic);

                logger.info("Enabled: " + mech.getName());
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    @Override
    public void discoverMechanics() {

        logger.info("Enumerating Mechanics");
        registerMechanic(Elevator.class);
        registerMechanic(Snow.class);
        registerMechanic(Bridge.class);
        registerMechanic(Door.class);
        registerMechanic(Gate.class);

        registerMechanic(EmptyDecay.class);
        logger.info("Found " + getAvailableMechanics().size() + ".");
    }

    @Override
    public MechanicDataCache getCache () {
        return cache;
    }
}