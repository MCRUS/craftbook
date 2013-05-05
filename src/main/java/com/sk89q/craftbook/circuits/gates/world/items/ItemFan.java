package com.sk89q.craftbook.circuits.gates.world.items;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import com.sk89q.craftbook.ChangedSign;
import com.sk89q.craftbook.circuits.ic.AbstractICFactory;
import com.sk89q.craftbook.circuits.ic.AbstractSelfTriggeredIC;
import com.sk89q.craftbook.circuits.ic.ChipState;
import com.sk89q.craftbook.circuits.ic.IC;
import com.sk89q.craftbook.circuits.ic.ICFactory;
import com.sk89q.craftbook.util.ItemUtil;

public class ItemFan extends AbstractSelfTriggeredIC {

    public ItemFan(Server server, ChangedSign sign, ICFactory factory) {

        super(server, sign, factory);
    }

    double force;

    @Override
    public void load() {

        try {
            force = Double.parseDouble(getSign().getLine(2));
        } catch (Exception ignored) {
            force = 1;
        }
    }

    @Override
    public String getTitle() {

        return "Item Fan";
    }

    @Override
    public String getSignTitle() {

        return "ITEM FAN";
    }

    @Override
    public void trigger(ChipState chip) {

        if (chip.getInput(0)) chip.setOutput(0, push());
    }

    @Override
    public void think(ChipState state) {

        state.setOutput(0, push());
    }

    public boolean push() {

        boolean returnValue = false;

        Block aboveBlock = getBackBlock().getRelative(0, 1, 0);

        for (Entity en : aboveBlock.getChunk().getEntities()) {
            if (!(en instanceof Item)) {
                continue;
            }
            Item item = (Item) en;
            ItemStack stack = item.getItemStack();
            if (!ItemUtil.isStackValid(stack) || item.isDead() || !item.isValid()) {
                continue;
            }
            Location location = item.getLocation();
            int ix = location.getBlockX();
            int iy = location.getBlockY();
            int iz = location.getBlockZ();
            if (ix == aboveBlock.getX() && iy == aboveBlock.getY() && iz == aboveBlock.getZ()) {

                item.teleport(item.getLocation().add(0, force, 0));

                returnValue = true;
            }
        }

        return returnValue;
    }

    public static class Factory extends AbstractICFactory {

        public Factory(Server server) {

            super(server);
        }

        @Override
        public IC create(ChangedSign sign) {

            return new ItemFan(getServer(), sign, this);
        }

        @Override
        public String getShortDescription() {

            return "Gently pushes items upwards.";
        }

        @Override
        public String[] getLineHelp() {

            String[] lines = new String[] {"force (default 1)", null};
            return lines;
        }
    }

    @Override
    public boolean isActive () {
        return true;
    }
}