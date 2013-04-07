package com.sk89q.craftbook.cart;

import static com.sk89q.craftbook.util.CartUtils.stop;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.util.Vector;

import com.sk89q.craftbook.util.SignUtil;

public class CartStation extends CartMechanism {

    @Override
    public void impact(Minecart cart, CartMechanismBlocks blocks, boolean minor) {
        // validate
        if (cart == null) return;
        if (!blocks.matches("station")) return;

        // go
        switch (isActive(blocks.rail, blocks.base, blocks.sign)) {
            case ON:
                // standardize its speed and direction.
                launch(cart, blocks.sign);
                break;
            case OFF:
            case NA:
                // park it.
                stop(cart);
                // recenter it
                Location l = blocks.rail.getLocation().add(0.5, 0.5, 0.5);
                if (!cart.getLocation().equals(l)) {
                    cart.teleport(l);
                }
                // recentering and parking almost completely prevents more than one cart from getting onto the same
                // station.
                break;
        }
    }

    private void launch(Minecart cart, Block director) {

        cart.setVelocity(propel(SignUtil.getFacing(director)));
    }

    /**
     * WorldEdit's Vector type collides with Bukkit's Vector type here. It's not pleasant.
     */
    public static Vector propel(BlockFace face) {

        return new Vector(face.getModX() * 0.2, face.getModY() * 0.2, face.getModZ() * 0.2);
    }

    @Override
    public void enter(Minecart cart, Entity entity, CartMechanismBlocks blocks) {
        // validate
        if (cart == null) return;
        if (!blocks.matches("station")) return;

        if (!((Sign) blocks.sign.getState()).getLine(2).equalsIgnoreCase("AUTOSTART")) return;

        // go
        switch (isActive(blocks.rail, blocks.base, blocks.sign)) {
            case ON:
                // standardize its speed and direction.
                launch(cart, blocks.sign);
                break;
            case OFF:
            case NA:
                // park it.
                stop(cart);
                // recenter it
                Location l = blocks.rail.getLocation().add(0.5, 0.5, 0.5);
                if (!cart.getLocation().equals(l)) {
                    cart.teleport(l);
                }
                // recentering and parking almost completely prevents more than one cart from getting onto the same
                // station.
                break;
        }
    }

    @Override
    public String getName() {

        return "Station";
    }

    @Override
    public String[] getApplicableSigns() {

        return new String[] {"station"};
    }
}