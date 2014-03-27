package com.sk89q.craftbook.vehicles.cart;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;

import com.sk89q.craftbook.AbstractCraftBookMechanic;
import com.sk89q.craftbook.util.EventUtil;

public class CollisionEntry extends AbstractCraftBookMechanic {

    @EventHandler(priority = EventPriority.LOW)
    public void onVehicleEntityCollision(VehicleEntityCollisionEvent event) {

        if(!EventUtil.passesFilter(event)) return;

        if (event.getVehicle() instanceof RideableMinecart) {
            if (!event.getVehicle().isEmpty()) return;
            if (!(event.getEntity() instanceof HumanEntity)) return;
            event.getVehicle().setPassenger(event.getEntity());

            event.setCollisionCancelled(true);
            return;
        }
    }
}