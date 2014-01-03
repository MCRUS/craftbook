package com.sk89q.craftbook.circuits.gates.world.blocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.sk89q.craftbook.ChangedSign;
import com.sk89q.craftbook.bukkit.util.BukkitUtil;
import com.sk89q.craftbook.circuits.ic.AbstractICFactory;
import com.sk89q.craftbook.circuits.ic.AbstractSelfTriggeredIC;
import com.sk89q.craftbook.circuits.ic.ChipState;
import com.sk89q.craftbook.circuits.ic.IC;
import com.sk89q.craftbook.circuits.ic.ICFactory;
import com.sk89q.craftbook.circuits.ic.ICVerificationException;
import com.sk89q.craftbook.circuits.pipe.PipeRequestEvent;
import com.sk89q.craftbook.util.BlockUtil;
import com.sk89q.craftbook.util.RegexUtil;
import com.sk89q.craftbook.util.SignUtil;

public class BlockBreaker extends AbstractSelfTriggeredIC {

    boolean above;

    public BlockBreaker(Server server, ChangedSign block, boolean above, ICFactory factory) {

        super(server, block, factory);
        this.above = above;
    }

    @Override
    public String getTitle() {

        return "Block Breaker";
    }

    @Override
    public String getSignTitle() {

        return "BLOCK BREAK";
    }

    @Override
    public void trigger(ChipState chip) {

        if (chip.getInput(0)) {
            chip.setOutput(0, breakBlock());
        }
    }

    @Override
    public void think(ChipState state) {

        state.setOutput(0, breakBlock());
    }

    Block broken, chest;

    int id;
    byte data;

    @Override
    public void load() {

        try {
            String[] split = RegexUtil.COLON_PATTERN.split(getSign().getLine(2));
            id = Integer.parseInt(split[0]);
            try {
                data = Byte.parseByte(split[1]);
            } catch(Exception ignored){}
        } catch (Exception ignored) {
        }
    }

    public boolean breakBlock() {

        if (chest == null || broken == null) {

            Block bl = getBackBlock();

            if (above) {
                chest = bl.getRelative(0, 1, 0);
                broken = bl.getRelative(0, -1, 0);
            } else {
                chest = bl.getRelative(0, -1, 0);
                broken = bl.getRelative(0, 1, 0);
            }
        }

        boolean hasChest = false;
        if (chest != null && chest.getState() instanceof InventoryHolder)
            hasChest = true;

        if (broken == null || broken.getType() == Material.AIR || broken.getType() == Material.BEDROCK || broken.getType() == Material.PISTON_MOVING_PIECE)
            return false;

        if (id > 0 && id != broken.getTypeId()) return false;

        if (data > 0 && data != broken.getData()) return false;

        for (ItemStack stack : BlockUtil.getBlockDrops(broken, null)) {

            BlockFace back = SignUtil.getBack(BukkitUtil.toSign(getSign()).getBlock());
            Block pipe = getBackBlock().getRelative(back);

            PipeRequestEvent event = new PipeRequestEvent(pipe, new ArrayList<ItemStack>(Arrays.asList(stack)), getBackBlock());
            Bukkit.getPluginManager().callEvent(event);

            if(!event.isValid())
                continue;

            for(ItemStack blockstack : event.getItems()) {
                if (hasChest) {
                    InventoryHolder c = (InventoryHolder) chest.getState();
                    HashMap<Integer, ItemStack> overflow = c.getInventory().addItem(blockstack);
                    ((BlockState) c).update();
                    if (overflow.isEmpty()) continue;
                    else {
                        for (Map.Entry<Integer, ItemStack> bit : overflow.entrySet()) {
                            dropItem(bit.getValue());
                        }
                        continue;
                    }
                }

                dropItem(blockstack);
            }
        }
        broken.setType(Material.AIR);

        return true;
    }

    public void dropItem(ItemStack item) {

        BukkitUtil.toSign(getSign()).getWorld().dropItem(BlockUtil.getBlockCentre(BukkitUtil.toSign(getSign()).getBlock()), item);
    }

    public static class Factory extends AbstractICFactory {

        boolean above;

        public Factory(Server server, boolean above) {

            super(server);
            this.above = above;
        }

        @Override
        public IC create(ChangedSign sign) {

            return new BlockBreaker(getServer(), sign, above, this);
        }

        @Override
        public void verify(ChangedSign sign) throws ICVerificationException {

            if(!sign.getLine(2).trim().isEmpty()) {
                try {
                    String[] split = RegexUtil.COLON_PATTERN.split(sign.getLine(2));
                    Integer.parseInt(split[0]);
                    try {
                        if(split.length > 1)
                            Byte.parseByte(split[1]);
                    } catch(Exception e){
                        throw new ICVerificationException("Data must be a number!");
                    }
                } catch (Exception ignored) {
                    throw new ICVerificationException("ID must be a number!");
                }
            }
        }

        @Override
        public String getShortDescription() {

            return "Breaks blocks above/below block sign is on.";
        }

        @Override
        public String[] getLineHelp() {

            return new String[] {"+oBlock ID:Data", null};
        }
    }
}