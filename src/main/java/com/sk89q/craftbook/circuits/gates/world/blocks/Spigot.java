package com.sk89q.craftbook.circuits.gates.world.blocks;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

import com.sk89q.craftbook.ChangedSign;
import com.sk89q.craftbook.circuits.ic.AbstractIC;
import com.sk89q.craftbook.circuits.ic.AbstractICFactory;
import com.sk89q.craftbook.circuits.ic.ChipState;
import com.sk89q.craftbook.circuits.ic.ConfigurableIC;
import com.sk89q.craftbook.circuits.ic.IC;
import com.sk89q.craftbook.circuits.ic.ICFactory;
import com.sk89q.craftbook.circuits.ic.RestrictedIC;
import com.sk89q.craftbook.util.ICUtil;
import com.sk89q.craftbook.util.LocationUtil;
import com.sk89q.util.yaml.YAMLProcessor;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BlockID;
import com.sk89q.worldedit.blocks.ItemID;

public class Spigot extends AbstractIC {

    Vector radius;
    Location offset;

    public Spigot(Server server, ChangedSign block, ICFactory factory) {

        super(server, block, factory);
    }

    @Override
    public void load() {

        radius = ICUtil.parseRadius(getSign());
        offset = ICUtil.parseBlockLocation(getSign()).getLocation();
    }

    @Override
    public String getTitle() {

        return "Spigot";
    }

    @Override
    public String getSignTitle() {

        return "SPIGOT";
    }

    @Override
    public void trigger(ChipState chip) {

        if (chip.getInput(0)) {
            chip.setOutput(0, search());
        }
    }

    public boolean search() {

        Block off = offset.getBlock();
        ArrayList<Location> searched = new ArrayList<Location>();
        return searchAt(searched, off);
    }

    public boolean searchAt(ArrayList<Location> searched, Block off) {

        if (searched.contains(off.getLocation())) return false;
        searched.add(off.getLocation());
        if (LocationUtil.isWithinRadius(off.getLocation(), offset, radius)) return false;
        if (off.getTypeId() == 0) {

            int m = getFromChest();
            if (m == BlockID.AIR) return false;
            off.setTypeId(parse(m));
            return true;
        } else if (off.isLiquid()) {
            if (off.getData() != 0x0) { // Moving

                int m = getFromChest(off.getTypeId());
                if (m == BlockID.AIR) return false;
                off.setTypeId(m);
                return true;
            } else { // Still

                if (searchAt(searched, off.getRelative(1, 0, 0))) return true;
                if (searchAt(searched, off.getRelative(-1, 0, 0))) return true;
                if (searchAt(searched, off.getRelative(0, 0, 1))) return true;
                if (searchAt(searched, off.getRelative(0, 0, -1))) return true;
                if (searchAt(searched, off.getRelative(0, 1, 0))) return true;
            }
        }

        return false;
    }

    public int getFromChest() {

        Block chest = getBackBlock().getRelative(0, -1, 0);

        if (chest.getTypeId() == BlockID.CHEST) {
            Chest c = (Chest) chest.getState();
            if (((Factory) getFactory()).buckets) {
                HashMap<Integer, ItemStack> over = c.getInventory().removeItem(new ItemStack(ItemID.WATER_BUCKET, 1));
                if (over.isEmpty()) return BlockID.WATER;
                over = c.getInventory().removeItem(new ItemStack(ItemID.LAVA_BUCKET, 1));
                if (over.isEmpty()) return BlockID.LAVA;
            } else {
                HashMap<Integer, ItemStack> over = c.getInventory().removeItem(new ItemStack(BlockID.WATER, 1));
                if (over.isEmpty()) return BlockID.WATER;
                over = c.getInventory().removeItem(new ItemStack(BlockID.STATIONARY_WATER, 1));
                if (over.isEmpty()) return BlockID.WATER;
                over = c.getInventory().removeItem(new ItemStack(BlockID.LAVA, 1));
                if (over.isEmpty()) return BlockID.LAVA;
                over = c.getInventory().removeItem(new ItemStack(BlockID.STATIONARY_LAVA, 1));
                if (over.isEmpty()) return BlockID.LAVA;
            }
        }

        return BlockID.AIR;
    }

    public int getFromChest(int m) {

        m = parse(m);
        Block chest = getBackBlock().getRelative(0, -1, 0);

        if (chest.getTypeId() == BlockID.CHEST) {
            Chest c = (Chest) chest.getState();

            if (((Factory) getFactory()).buckets) {

                HashMap<Integer, ItemStack> over = c.getInventory().removeItem(
                        new ItemStack(m == BlockID.LAVA ? ItemID.LAVA_BUCKET : ItemID.WATER_BUCKET, 1));
                if (over.isEmpty()) return m;
            } else {

                HashMap<Integer, ItemStack> over = c.getInventory().removeItem(new ItemStack(m, 1));
                if (over.isEmpty()) return m;

                over = c.getInventory().removeItem(new ItemStack(unparse(m), 1));
                if (over.isEmpty()) return m;
            }
        }

        return BlockID.AIR;
    }

    public int parse(int mat) {

        if (mat == BlockID.STATIONARY_WATER || mat == BlockID.WATER) return BlockID.WATER;
        if (mat == BlockID.STATIONARY_LAVA || mat == BlockID.LAVA) return BlockID.LAVA;
        return BlockID.AIR;
    }

    public int unparse(int mat) {

        if (mat == BlockID.STATIONARY_WATER || mat == BlockID.WATER) return BlockID.STATIONARY_WATER;
        if (mat == BlockID.STATIONARY_LAVA || mat == BlockID.LAVA) return BlockID.STATIONARY_LAVA;
        return BlockID.AIR;
    }

    public static class Factory extends AbstractICFactory implements RestrictedIC, ConfigurableIC {

        public boolean buckets;

        public Factory(Server server) {

            super(server);
        }

        @Override
        public IC create(ChangedSign sign) {

            return new Spigot(getServer(), sign, this);
        }

        @Override
        public String getShortDescription() {

            return "Fills areas with liquid from below chest.";
        }

        @Override
        public String[] getLineHelp() {

            return new String[] {"+oradius=x:y:z offset", null};
        }

        @Override
        public void addConfiguration(YAMLProcessor config, String path) {

            buckets = config.getBoolean(path + "requires-buckets", false);
        }
    }
}