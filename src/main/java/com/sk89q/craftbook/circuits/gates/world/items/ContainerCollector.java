package com.sk89q.craftbook.circuits.gates.world.items;

import java.util.List;

import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.sk89q.craftbook.ChangedSign;
import com.sk89q.craftbook.bukkit.util.BukkitUtil;
import com.sk89q.craftbook.circuits.ic.AbstractICFactory;
import com.sk89q.craftbook.circuits.ic.AbstractSelfTriggeredIC;
import com.sk89q.craftbook.circuits.ic.ChipState;
import com.sk89q.craftbook.circuits.ic.IC;
import com.sk89q.craftbook.circuits.ic.ICFactory;
import com.sk89q.craftbook.util.EntityUtil;
import com.sk89q.craftbook.util.ICUtil;
import com.sk89q.craftbook.util.InventoryUtil;
import com.sk89q.craftbook.util.ItemUtil;
import com.sk89q.craftbook.util.SignUtil;

/**
 * @author Me4502
 */
public class ContainerCollector extends AbstractSelfTriggeredIC {

    public ContainerCollector(Server server, ChangedSign sign, ICFactory factory) {

        super(server, sign, factory);
    }

    @Override
    public String getTitle() {

        return "Container Collector";
    }

    @Override
    public String getSignTitle() {

        return "CONTAINER COLLECT";
    }

    @Override
    public void trigger(ChipState chip) {

        if (chip.getInput(0)) {
            chip.setOutput(0, collect());
        }
    }

    @Override
    public void think(ChipState chip) {

        chip.setOutput(0, collect());
    }

    ItemStack doWant, doNotWant;

    @Override
    public void load() {

        doWant = ICUtil.getItem(getLine(2));
        doNotWant = ICUtil.getItem(getLine(3));
    }

    protected boolean collect() {

        Block b = SignUtil.getBackBlock(BukkitUtil.toSign(getSign()).getBlock()).getRelative(0, 1, 0);

        if(!(b.getState() instanceof InventoryHolder))
            return false;

        boolean collected = false;
        for (Entity en : BukkitUtil.toSign(getSign()).getChunk().getEntities()) {
            if (!(en instanceof Item)) {
                continue;
            }
            Item item = (Item) en;
            ItemStack stack = item.getItemStack();
            if (!ItemUtil.isStackValid(stack) || item.isDead() || !item.isValid())
                continue;

            if (EntityUtil.isEntityInBlock(en, BukkitUtil.toSign(getSign()).getBlock())) {

                // Check to see if it matches either test stack, if not stop
                if (doWant != null && !ItemUtil.areItemsIdentical(doWant, stack))
                    continue;

                if (doNotWant != null && ItemUtil.areItemsIdentical(doNotWant, stack))
                    continue;

                // Add the items to a container, and destroy them.
                List<ItemStack> leftovers = InventoryUtil.addItemsToInventory((InventoryHolder)b.getState(), stack);
                if(leftovers.isEmpty()) {
                    item.remove();
                    collected = true;
                } else {
                    if(leftovers.get(0).getAmount() != stack.getAmount()) {
                        item.getItemStack().setAmount(leftovers.get(0).getAmount());
                        collected = true;
                    }
                }
            }
        }
        return collected;
    }

    public static class Factory extends AbstractICFactory {

        public Factory(Server server) {

            super(server);
        }

        @Override
        public IC create(ChangedSign sign) {

            return new ContainerCollector(getServer(), sign, this);
        }

        @Override
        public String getShortDescription() {

            return "Collects items into above chest.";
        }

        @Override
        public String[] getLineHelp() {

            String[] lines = new String[] {"included id:data", "excluded id:data"};
            return lines;
        }
    }
}