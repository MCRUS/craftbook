package com.sk89q.craftbook.mech;

// $Id$
/*
 * CraftBook Copyright (C) 2010 sk89q <http://www.sk89q.com>
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not,
 * see <http://www.gnu.org/licenses/>.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.sk89q.craftbook.AbstractMechanic;
import com.sk89q.craftbook.AbstractMechanicFactory;
import com.sk89q.craftbook.LocalPlayer;
import com.sk89q.craftbook.bukkit.BukkitPlayer;
import com.sk89q.craftbook.bukkit.CraftBookPlugin;
import com.sk89q.craftbook.util.ItemInfo;
import com.sk89q.worldedit.BlockWorldVector;
import com.sk89q.worldedit.blocks.BlockID;
import com.sk89q.worldedit.bukkit.BukkitUtil;

/**
 * Handler for cauldrons.
 *
 * @author sk89q
 * @deprecated Use {@link com.sk89q.craftbook.mech.cauldron.ImprovedCauldron} instead
 */
@Deprecated
public class Cauldron extends AbstractMechanic {

    public static boolean isACauldron(BlockWorldVector pt) {
        World world = BukkitUtil.toBlock(pt).getWorld();

        int ix = pt.getBlockX();
        int iy = pt.getBlockY();
        int iz = pt.getBlockZ();

        Material below = world.getBlockAt(ix, iy - 1, iz).getType();
        Material below2 = world.getBlockAt(ix, iy - 2, iz).getType();
        Block s1 = world.getBlockAt(ix + 1, iy, iz);
        Block s3 = world.getBlockAt(ix - 1, iy, iz);
        Block s2 = world.getBlockAt(ix, iy, iz + 1);
        Block s4 = world.getBlockAt(ix, iy, iz - 1);

        ItemInfo blockItem = CraftBookPlugin.inst().getConfiguration().legacyCauldronBlock;

        // stop strange lava ids
        if (below == Material.STATIONARY_LAVA) {
            below = Material.LAVA;
        }
        if (below2 == Material.STATIONARY_LAVA) {
            below2 = Material.LAVA;
        }
        // Preliminary check so we don't waste CPU cycles
        if ((below == Material.LAVA || below2 == Material.LAVA) && (blockItem.isSame(s1) || blockItem.isSame(s2) || blockItem.isSame(s3) || blockItem.isSame(s4))) {
            return true;
        }

        return false;
    }

    public static class Factory extends AbstractMechanicFactory<Cauldron> {

        protected final CauldronCookbook recipes;

        public Factory() {

            recipes = new CauldronCookbook();
        }

        @Override
        public Cauldron detect(BlockWorldVector pt) {

            Block block = BukkitUtil.toBlock(pt);
            // check if this looks at all like something we're interested in
            // first
            if (block.getTypeId() == BlockID.AIR) return null;
            return isACauldron(pt) ? new Cauldron(recipes, pt) : null;
        }
    }

    /**
     * Stores the recipes.
     */
    private final CauldronCookbook recipes;
    private final BlockWorldVector pt;

    /**
     * Construct the handler.
     *
     * @param recipes
     * @param pt
     * @param plugin
     */
    public Cauldron(CauldronCookbook recipes, BlockWorldVector pt) {

        super();
        this.recipes = recipes;
        this.pt = pt;
    }

    @Override
    public void onRightClick(PlayerInteractEvent event) {

        LocalPlayer localPlayer = CraftBookPlugin.inst().wrapPlayer(event.getPlayer());

        if (!CraftBookPlugin.inst().getConfiguration().legacyCauldronEnabled) return;

        if (!localPlayer.hasPermission("craftbook.mech.cauldron")) return;

        if (!BukkitUtil.toWorldVector(event.getClickedBlock()).equals(pt)) return;
        if (event.getPlayer().getItemInHand().getTypeId() >= 255 || event.getPlayer().getItemInHand().getTypeId() ==
                BlockID.AIR)
            if (preCauldron(localPlayer, event.getPlayer().getWorld(), pt)) {
                event.setUseInteractedBlock(Result.DENY);
                event.setUseItemInHand(Result.DENY);
                event.setCancelled(true);
            }
    }

    /**
     * Thrown when a suspected formation is not actually a valid cauldron.
     */
    private static class NotACauldronException extends Exception {

        private static final long serialVersionUID = 3091428924893050849L;

        /**
         * Construct the exception with a message.
         *
         * @param msg
         */
        public NotACauldronException(String msg) {

            super(msg);
        }
    }

    /**
     * Do cauldron.
     *
     * @param pt
     * @param player
     * @param world
     */
    public boolean preCauldron(LocalPlayer player, World world, BlockWorldVector pt) {

        double x = pt.getX();
        double y = pt.getY();
        double z = pt.getZ();

        int ix = pt.getBlockX();
        int iy = pt.getBlockY();
        int iz = pt.getBlockZ();

        double rootY = y;
        Material below = world.getBlockAt(ix, iy - 1, iz).getType();
        Material below2 = world.getBlockAt(ix, iy - 2, iz).getType();
        Block s1 = world.getBlockAt(ix + 1, iy, iz);
        Block s3 = world.getBlockAt(ix - 1, iy, iz);
        Block s2 = world.getBlockAt(ix, iy, iz + 1);
        Block s4 = world.getBlockAt(ix, iy, iz - 1);

        ItemInfo blockItem = CraftBookPlugin.inst().getConfiguration().legacyCauldronBlock;

        // stop strange lava ids
        if (below == Material.STATIONARY_LAVA) {
            below = Material.LAVA;
        }
        if (below2 == Material.STATIONARY_LAVA) {
            below2 = Material.LAVA;
        }
        // Preliminary check so we don't waste CPU cycles
        if ((below == Material.LAVA || below2 == Material.LAVA) && (blockItem.isSame(s1) || blockItem.isSame(s2) || blockItem.isSame(s3) || blockItem.isSame(s4))) {
            // Cauldron is 2 units deep
            if (below == Material.LAVA) {
                rootY++;
            }
            performCauldron(player, world, new BlockWorldVector(pt.getWorld(), x, rootY, z));
            return true;
        }
        return false;
    }

    /**
     * Attempt to perform a cauldron recipe.
     *
     * @param player
     * @param world
     * @param pt
     */
    private void performCauldron(LocalPlayer player, World world, BlockWorldVector pt) {
        // Gotta start at a root Y then find our orientation
        int rootY = pt.getBlockY();

        Player p = ((BukkitPlayer)player).getPlayer();

        ItemInfo blockItem = CraftBookPlugin.inst().getConfiguration().legacyCauldronBlock;

        // Used to store cauldron blocks -- walls are counted
        Map<BlockWorldVector, ItemInfo> visited = new HashMap<BlockWorldVector, ItemInfo>();

        try {
            // The following attempts to recursively find adjacent blocks so
            // that it can find all the blocks used within the cauldron
            findCauldronContents(world, pt, rootY - 1, rootY, visited);

            // We want cauldrons of a specific shape and size, and 24 is just
            // the right number of blocks that the cauldron we want takes up --
            // nice and cheap check
            if (visited.size() != 24) throw new NotACauldronException("mech.cauldron.too-small");

            // Key is the block ID and the value is the amount
            Map<ItemInfo, Integer> contents = new HashMap<ItemInfo, Integer>();

            // Now we have to ignore cauldron blocks so that we get the real
            // contents of the cauldron
            for (Map.Entry<BlockWorldVector, ItemInfo> entry : visited.entrySet()) {
                if (entry.getValue().equals(blockItem)) if (!contents.containsKey(entry.getValue())) {
                    contents.put(entry.getValue(), 1);
                } else {
                    contents.put(entry.getValue(), contents.get(entry.getValue()) + 1);
                }
            }

            // Find the recipe
            CauldronCookbook.Recipe recipe = recipes.find(contents);

            if (recipe != null) {

                String[] groups = recipe.getGroups();

                if (groups != null) {
                    boolean found = false;

                    for (String group : groups) {

                        if (CraftBookPlugin.inst().inGroup(p, group)) {
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        player.printError("mech.cauldron.legacy-not-in-group");
                        return;
                    }
                }

                player.print(player.translate("mech.cauldron.legacy-create") + " " + recipe.getName() + ".");

                List<ItemInfo> ingredients = new ArrayList<ItemInfo>(recipe.getIngredients());

                //List<BlockWorldVector> removeQueue = new ArrayList<BlockWorldVector>();

                // Get rid of the blocks in world
                for (Map.Entry<BlockWorldVector, ItemInfo> entry : visited.entrySet())
                    // This is not a fast operation, but we should not have
                    // too many ingredients
                {
                    if (ingredients.contains(entry.getValue())) {
                        // Some blocks need to removed first otherwise they will
                        // drop an item, so let's remove those first
                        // if
                        // (!BlockID.isBottomDependentBlock(entry.getValue())) {
                        // removeQueue.add(entry.getKey());
                        // } else {
                        world.getBlockAt(entry.getKey().getBlockX(), entry.getKey().getBlockY(),
                                entry.getKey().getBlockZ()).setTypeId(BlockID.AIR);
                        // }
                        ingredients.remove(entry.getValue());
                    }
                }

                /*
                for (BlockWorldVector v : removeQueue) {
                    world.getBlockAt(v.getBlockX(), v.getBlockY(), v.getBlockZ()).setTypeId(BlockID.AIR);
                }
                 */

                // Give results
                for (ItemInfo id : recipe.getResults()) {
                    HashMap<Integer, ItemStack> map = p.getInventory().addItem(new ItemStack(id.getType(), id.getData()));
                    for (Entry<Integer, ItemStack> i : map.entrySet()) {
                        world.dropItem(p.getLocation(), i.getValue());
                    }
                }
                // Didn't find a recipe
            } else {
                player.printError("mech.cauldron.legacy-not-a-recipe");
            }
        } catch (NotACauldronException ignored) {
        }
    }

    /**
     * Recursively expand the search area so we can define the number of blocks that are in the cauldron. The search
     * will not exceed 24 blocks as no
     * pot will ever use up that many blocks. The Y are bounded both directions so we don't ever search the lava or
     * anything above, although in the
     * case of non-wall blocks, we also make sure that there is standing lava underneath.
     *
     * @param world
     * @param pt
     * @param minY
     * @param maxY
     * @param visited
     *
     * @throws Cauldron.NotACauldronException
     */
    public void findCauldronContents(World world, BlockWorldVector pt, int minY, int maxY, Map<BlockWorldVector, ItemInfo> visited) throws NotACauldronException {

        ItemInfo blockID = CraftBookPlugin.inst().getConfiguration().legacyCauldronBlock;

        // Don't want to go too low or high
        if (pt.getBlockY() < minY) return;
        if (pt.getBlockY() > maxY) return;

        // There is likely a leak in the cauldron (or this isn't a cauldron)
        if (visited.size() > 24) throw new NotACauldronException("mech.cauldron.leaky");

        // Prevent infinite looping
        if (visited.containsKey(pt)) return;

        Material type = world.getBlockAt(pt.getBlockX(), pt.getBlockY(), pt.getBlockZ()).getType();

        // Make water work reliably
        if (type == Material.STATIONARY_WATER) {
            type = Material.WATER;
        }

        // Make lava work reliably
        if (type == Material.STATIONARY_LAVA) {
            type = Material.LAVA;
        }

        visited.put(pt, new ItemInfo(type, world.getBlockAt(pt.getBlockX(), pt.getBlockY(), pt.getBlockZ()).getData()));

        // It's a wall -- we only needed to remember that we visited it but
        // we don't need to recurse
        if (type == blockID.getType()) return;

        // Must have a lava floor
        BlockWorldVector lavaPos = recurse(0, pt.getBlockY() - minY + 1, 0, pt);
        if (world.getBlockTypeIdAt(lavaPos.getBlockX(), lavaPos.getBlockY(), lavaPos.getBlockZ()) == BlockID.LAVA)
            throw new NotACauldronException("mech.cauldron.no-lava");

        // Now we recurse!
        findCauldronContents(world, recurse(1, 0, 0, pt), minY, maxY, visited);
        findCauldronContents(world, recurse(-1, 0, 0, pt), minY, maxY, visited);
        findCauldronContents(world, recurse(0, 0, 1, pt), minY, maxY, visited);
        findCauldronContents(world, recurse(0, 0, -1, pt), minY, maxY, visited);
        findCauldronContents(world, recurse(0, 1, 0, pt), minY, maxY, visited);
        findCauldronContents(world, recurse(0, -1, 0, pt), minY, maxY, visited);
    }

    /**
     * Returns a new BlockWorldVector with i, j, and k added to pt's x, y and z.
     *
     * @param i
     * @param j
     * @param k
     * @param pt
     */
    private BlockWorldVector recurse(int i, int j, int k, BlockWorldVector pt) {

        return new BlockWorldVector(pt.getWorld(), pt.getX() + i, pt.getY() + j, pt.getZ() + k);
    }
}