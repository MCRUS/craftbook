package com.sk89q.craftbook.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import com.sk89q.craftbook.bukkit.CraftBookPlugin;
import com.sk89q.worldedit.blocks.BlockID;

public class BlockUtil {

    public static boolean areBlocksSimilar(Block block, Block block2) {

        return block.getType() == block2.getType();
    }

    public static boolean areBlocksIdentical(Block block, Block block2) {

        if (block.getType() == block2.getType()) if (block.getData() == block2.getData()) return true;
        return false;
    }

    public static boolean isBlockSimilarTo(Block block, Material type) {

        return block.getType() == type;
    }

    public static boolean isBlockIdenticalTo(Block block, Material type, byte data) {

        if (block.getType() == type) if (block.getData() == data) return true;
        return false;
    }

    public static boolean isBlockReplacable(int id) {

        switch (id) {

            case BlockID.AIR:
            case BlockID.CROPS:
            case BlockID.DEAD_BUSH:
            case BlockID.END_PORTAL:
            case BlockID.FIRE:
            case BlockID.LONG_GRASS:
            case BlockID.LAVA:
            case BlockID.STATIONARY_LAVA:
            case BlockID.WATER:
            case BlockID.STATIONARY_WATER:
            case BlockID.VINE:
            case BlockID.SNOW:
            case BlockID.PISTON_MOVING_PIECE:
                return true;
            default:
                return false;
        }
    }

    public static boolean hasTileData(Material material) {

        switch(material) {

            case CHEST:
            case FURNACE:
            case BURNING_FURNACE:
            case BREWING_STAND:
            case DISPENSER:
            case DROPPER:
            case HOPPER:
            case SIGN:
            case TRAPPED_CHEST:
                return true;
            default:
                return false;
        }
    }

    public static Location getBlockCentre(Block block) {

        return block.getLocation().add(0.5, 0.5, 0.5);
    }

    /**
     * Gets a list of all drops for a particular block.
     * 
     * @param block The block to check drops for.
     * @param tool The tool. If null, it'll allow for all drops.
     * @return The list of drops
     */
    public static ItemStack[] getBlockDrops(Block block, ItemStack tool) {

        List<ItemStack> drops = new ArrayList<ItemStack>();

        switch(block.getType()) {
            case SNOW:
                if(tool == null || tool.getType() == Material.WOOD_SPADE || tool.getType() == Material.STONE_SPADE || tool.getType() == Material.IRON_SPADE || tool.getType() == Material.GOLD_SPADE || tool.getType() == Material.DIAMOND_SPADE)
                    drops.add(new ItemStack(Material.SNOW_BALL));
                break;
            case CROPS:
                drops.add(new ItemStack(Material.WHEAT, 1));
                int amount = CraftBookPlugin.inst().getRandom().nextInt(4);
                if(amount > 0)
                    drops.add(new ItemStack(Material.SEEDS, amount));
                break;
            case CARROT:
                drops.add(new ItemStack(Material.CARROT_ITEM, 1 + CraftBookPlugin.inst().getRandom().nextInt(4)));
                break;
            case POTATO:
                drops.add(new ItemStack(Material.POTATO_ITEM, 1 + CraftBookPlugin.inst().getRandom().nextInt(4)));
                if(CraftBookPlugin.inst().getRandom().nextInt(50) == 0)
                    drops.add(new ItemStack(Material.POISONOUS_POTATO, 1));
                break;
            case NETHER_STALK:
                drops.add(new ItemStack(Material.NETHER_WARTS, 2 + CraftBookPlugin.inst().getRandom().nextInt(3)));
                break;
            case SUGAR_CANE_BLOCK:
                drops.add(new ItemStack(Material.SUGAR_CANE, 1));
                break;
            case MELON_BLOCK:
                drops.add(new ItemStack(Material.MELON, 3 + CraftBookPlugin.inst().getRandom().nextInt(5)));
                break;
            case COCOA:
                drops.add(new ItemStack(Material.INK_SACK, 3, (short) 3));
                break;
            default:
                if(tool == null)
                    drops.addAll(block.getDrops());
                else
                    drops.addAll(block.getDrops(tool));
                break;
        }

        return drops.toArray(new ItemStack[drops.size()]);
    }
}