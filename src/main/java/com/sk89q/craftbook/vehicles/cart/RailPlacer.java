package com.sk89q.craftbook.vehicles.cart;

import org.bukkit.Material;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.inventory.ItemStack;

import com.sk89q.craftbook.AbstractCraftBookMechanic;
import com.sk89q.worldedit.blocks.BlockType;

public class RailPlacer extends AbstractCraftBookMechanic {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onVehicleMove(VehicleMoveEvent event) {

        if (!(event.getVehicle() instanceof StorageMinecart)) return;

        if(event.getTo().getBlock().getType() == Material.AIR && !BlockType.canPassThrough(event.getTo().getBlock().getRelative(0, -1, 0).getTypeId()) && ((StorageMinecart)event.getVehicle()).getInventory().contains(Material.RAILS)) {

            if(((StorageMinecart)event.getVehicle()).getInventory().removeItem(new ItemStack(Material.RAILS, 1)).isEmpty())
                event.getTo().getBlock().setType(Material.RAILS);
        }
    }
}