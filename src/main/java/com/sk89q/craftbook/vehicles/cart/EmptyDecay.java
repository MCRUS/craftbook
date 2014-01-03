package com.sk89q.craftbook.vehicles.cart;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.world.ChunkLoadEvent;

import com.sk89q.craftbook.AbstractCraftBookMechanic;
import com.sk89q.craftbook.bukkit.CraftBookPlugin;

public class EmptyDecay extends AbstractCraftBookMechanic {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onVehicleExit(VehicleExitEvent event) {

        Vehicle vehicle = event.getVehicle();

        if (!(vehicle instanceof RideableMinecart)) return;

        CraftBookPlugin.inst().getServer().getScheduler().runTaskLater(CraftBookPlugin.inst(), new Decay((RideableMinecart) vehicle), CraftBookPlugin.inst().getConfiguration().minecartDecayTime);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onChunkLoad(ChunkLoadEvent event) {

        for (Entity ent : event.getChunk().getEntities()) {
            if (ent == null || !ent.isValid())
                continue;
            if (!(ent instanceof RideableMinecart))
                continue;
            if (!ent.isEmpty())
                continue;
            CraftBookPlugin.inst().getServer().getScheduler().runTaskLater(CraftBookPlugin.inst(), new Decay((RideableMinecart) ent), CraftBookPlugin.inst().getConfiguration().minecartDecayTime);
        }
    }

    static class Decay implements Runnable {

        RideableMinecart cart;

        public Decay(RideableMinecart cart) {

            this.cart = cart;
        }

        @Override
        public void run() {

            if (cart == null || !cart.isValid() || !cart.isEmpty()) return;
            cart.remove();
        }
    }
}