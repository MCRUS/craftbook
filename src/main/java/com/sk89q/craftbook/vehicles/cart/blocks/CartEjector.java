package com.sk89q.craftbook.vehicles.cart.blocks;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;

import com.sk89q.craftbook.util.ItemInfo;
import com.sk89q.craftbook.util.RedstoneUtil.Power;
import com.sk89q.craftbook.util.SignUtil;
import com.sk89q.craftbook.vehicles.cart.events.CartBlockImpactEvent;
import com.sk89q.worldedit.bukkit.BukkitUtil;

public class CartEjector extends CartBlockMechanism {

    public CartEjector (ItemInfo material) {
        super(material);
    }

    @EventHandler
    public void onVehicleImpact(CartBlockImpactEvent event) {

        // care?
        if (!event.getBlocks().matches(getMaterial())) return;
        if (event.getMinecart().isEmpty()) return;

        // enabled?
        if (Power.OFF == isActive(event.getBlocks())) return;

        // go
        Block ejectTarget;
        if (!event.getBlocks().hasSign()) {
            ejectTarget = event.getBlocks().rail;
        } else if (!event.getBlocks().matches("eject")) {
            ejectTarget = event.getBlocks().rail;
        } else {
            ejectTarget = event.getBlocks().rail.getRelative(SignUtil.getFront(event.getBlocks().sign));
        }
        // if you use just
        // cart.getPassenger().teleport(ejectTarget.getLocation());
        // the client tweaks as bukkit tries to teleport you, then changes its mind and leaves you in the cart.
        // the cart also comes to a dead halt at the time of writing, and i have no idea why.
        Entity ent = event.getMinecart().getPassenger();
        event.getMinecart().eject();
        ent.teleport(BukkitUtil.center(ejectTarget.getLocation()));

        // notice!
        // if a client tries to board a cart immediately before it crosses an ejector,
        // it may appear to them that they crossed the ejector and it failed to activate.
        // what's actually happening is that the server didn't see them enter the cart
        // until -after- it had triggered the ejector... it's just client anticipating.
    }

    @Override
    public String getName() {

        return "Ejector";
    }

    @Override
    public String[] getApplicableSigns() {

        return new String[] {"Eject"};
    }
}