package com.sk89q.craftbook.vehicles.cart;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;

public class CollisionEntry implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onVehicleEntityCollision(VehicleEntityCollisionEvent event) {

        if (event.getVehicle() instanceof RideableMinecart) {
            if (!event.getVehicle().isEmpty()) return;
            if (!(event.getEntity() instanceof LivingEntity)) return;
            event.getVehicle().setPassenger(event.getEntity());

            event.setCollisionCancelled(true);
            return;
        }
    }
}