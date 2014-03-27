package com.sk89q.craftbook.mech.drops;

import java.util.List;

import com.sk89q.craftbook.util.ItemInfo;

public class BlockCustomDropDefinition extends CustomDropDefinition {

    private ItemInfo blockData;

    /**
     * Instantiate a Block-Type CustomDrop.
     */
    public BlockCustomDropDefinition(List<DropItemStack> drops, ItemInfo blockData) {
        super(drops);

        this.blockData = blockData;
    }

    public ItemInfo getBlockType() {
        return blockData;
    }
}