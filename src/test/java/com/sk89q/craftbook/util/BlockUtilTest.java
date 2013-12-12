package com.sk89q.craftbook.util;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.sk89q.worldedit.blocks.BlockID;

@RunWith(PowerMockRunner.class)
@PrepareForTest(BlockUtil.class)
public class BlockUtilTest {

    @Test
    public void testAreBlocksSimilar() {

        Block mockBlock1 = mock(Block.class);
        when(mockBlock1.getType()).thenReturn(Material.SAND);

        Block mockBlock2 = mock(Block.class);
        when(mockBlock2.getType()).thenReturn(Material.STONE);

        assertTrue(!BlockUtil.areBlocksSimilar(mockBlock1, mockBlock2));

        when(mockBlock2.getType()).thenReturn(Material.SAND);

        assertTrue(BlockUtil.areBlocksSimilar(mockBlock1, mockBlock2));
    }

    @Test
    public void testAreBlocksIdentical() {

        Block mockBlock1 = mock(Block.class);
        when(mockBlock1.getType()).thenReturn(Material.SAND);
        when(mockBlock1.getData()).thenReturn((byte) 1);

        Block mockBlock2 = mock(Block.class);
        when(mockBlock2.getType()).thenReturn(Material.STONE);
        when(mockBlock2.getData()).thenReturn((byte) 1);

        assertTrue(!BlockUtil.areBlocksIdentical(mockBlock1, mockBlock2));

        when(mockBlock2.getType()).thenReturn(Material.SAND);

        assertTrue(BlockUtil.areBlocksIdentical(mockBlock1, mockBlock2));

        when(mockBlock2.getData()).thenReturn((byte) 2);

        assertTrue(!BlockUtil.areBlocksIdentical(mockBlock1, mockBlock2));
    }

    @Test
    public void testIsBlockSimilarTo() {

        Block mockBlock1 = mock(Block.class);
        when(mockBlock1.getType()).thenReturn(Material.WOOD);

        assertTrue(!BlockUtil.isBlockSimilarTo(mockBlock1, Material.COBBLESTONE));

        assertTrue(BlockUtil.isBlockSimilarTo(mockBlock1, Material.WOOD));
    }

    @Test
    public void testIsBlockIdenticalTo() {

        Block mockBlock1 = mock(Block.class);
        when(mockBlock1.getType()).thenReturn(Material.WOOD);
        when(mockBlock1.getData()).thenReturn((byte) 1);

        assertTrue(!BlockUtil.isBlockIdenticalTo(mockBlock1, Material.SAND, (byte) 1));

        assertTrue(BlockUtil.isBlockIdenticalTo(mockBlock1, Material.WOOD, (byte) 1));

        assertTrue(!BlockUtil.isBlockIdenticalTo(mockBlock1, Material.WOOD, (byte) 4));
    }

    @Test
    public void testIsBlockReplacable() {

        assertTrue(!BlockUtil.isBlockReplacable(BlockID.STONE));
        assertTrue(BlockUtil.isBlockReplacable(BlockID.WATER));
        assertTrue(BlockUtil.isBlockReplacable(BlockID.LAVA));
        assertTrue(BlockUtil.isBlockReplacable(BlockID.AIR));
    }
}