package com.sk89q.craftbook.vehicles.cart;

import org.bukkit.entity.Minecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.util.Vector;

import com.sk89q.craftbook.AbstractCraftBookMechanic;
import com.sk89q.craftbook.bukkit.CraftBookPlugin;

public class FallModifier extends AbstractCraftBookMechanic {

    Vector fallSpeed;

    @Override
    public boolean enable() {

        fallSpeed = new Vector(CraftBookPlugin.inst().getConfiguration().minecartFallHorizontalSpeed, CraftBookPlugin.inst().getConfiguration().minecartFallVerticalSpeed, CraftBookPlugin.inst().getConfiguration().minecartFallHorizontalSpeed);
        return true;
    }

    @Override
    public void disable() {
        fallSpeed = null;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onVehicleCreate(VehicleCreateEvent event) {

        if (!(event.getVehicle() instanceof Minecart)) return;

        ((Minecart) event.getVehicle()).setFlyingVelocityMod(fallSpeed);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onVehicleMove(VehicleMoveEvent event) {

        if (!(event.getVehicle() instanceof Minecart)) return;

        ((Minecart) event.getVehicle()).setFlyingVelocityMod(fallSpeed);
    }
}