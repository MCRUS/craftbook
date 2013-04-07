package com.sk89q.craftbook.cart;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Minecart;

import com.sk89q.craftbook.util.CartUtils;

public class CartLift extends CartMechanism {

    @Override
    public void impact(final Minecart cart, CartMechanismBlocks blocks, boolean minor) {
        // validate
        if (cart == null) return;
        if (blocks.sign == null) return;
        if (!(blocks.matches("cartlift up") || blocks.matches("cartlift down"))) return;

        // go
        boolean up = blocks.matches("cartlift up");
        Block destination = blocks.sign;

        BlockFace face;
        if (up) face = BlockFace.UP;
        else face = BlockFace.DOWN;

        while (true) {

            if(destination.getLocation().getY() == 0 && !up)
                return;
            if(destination.getLocation().getY() == destination.getWorld().getMaxHeight() && up)
                return;
            destination = destination.getRelative(face);

            BlockState state = destination.getState();
            if (state instanceof Sign && blocks.base.getTypeId() == destination.getRelative(BlockFace.UP).getTypeId()) {
                String testLine = ((Sign) state).getLine(2);

                if (testLine.equalsIgnoreCase("[CartLift Up]") || testLine.equalsIgnoreCase("[CartLift Down]")
                        || testLine.equalsIgnoreCase("[CartLift]")) {
                    destination = destination.getRelative(BlockFace.UP, 2);
                    break;
                }
            }
        }

        CartUtils.teleport(cart, destination.getLocation());
    }

    @Override
    public String getName() {

        return "CartLift";
    }

    @Override
    public String[] getApplicableSigns() {

        return new String[] {"CartLift Up", "CartLift Down", "CartLift"};
    }
}