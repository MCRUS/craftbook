package com.sk89q.craftbook.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.sk89q.craftbook.bukkit.CraftBookPlugin;
import com.sk89q.worldguard.protection.GlobalRegionManager;
import com.sk89q.worldguard.protection.flags.DefaultFlag;

public class ProtectionUtil {

    /**
     * Checks to see if a player can build at a location. This will return
     * true if region protection is disabled.
     *
     * @param player The player to check.
     * @param loc    The location to check at.
     * @param build True for build, false for break
     *
     * @return whether {@code player} can build at {@code loc}
     *
     * @see GlobalRegionManager#canBuild(org.bukkit.entity.Player, org.bukkit.Location)
     */
    public static boolean canBuild(Player player, Location loc, boolean build) {

        return canBuild(player,loc.getBlock(), build);
    }

    /**
     * Checks to see if a player can build at a location. This will return
     * true if region protection is disabled or WorldGuard is not found.
     *
     * @param player The player to check
     * @param block  The block to check at.
     * @param build True for build, false for break
     *
     * @return whether {@code player} can build at {@code block}'s location
     *
     * @see GlobalRegionManager#canBuild(org.bukkit.entity.Player, org.bukkit.block.Block)
     */
    public static boolean canBuild(Player player, Block block, boolean build) {

        if (!shouldUseProtection()) return true;
        if (CraftBookPlugin.inst().getConfiguration().advancedBlockChecks) {

            CompatabilityUtil.disableInterferences(player);
            BlockEvent event;
            if(build)
                event = new BlockPlaceEvent(block, block.getState(), block.getRelative(0, -1, 0), player.getItemInHand(), player, true);
            else
                event = new BlockBreakEvent(block, player);
            EventUtil.ignoreEvent(event);
            CraftBookPlugin.inst().getServer().getPluginManager().callEvent(event);
            CompatabilityUtil.enableInterferences(player);
            return !(((Cancellable) event).isCancelled() || event instanceof BlockPlaceEvent && !((BlockPlaceEvent) event).canBuild());
        }
        if (!CraftBookPlugin.inst().getConfiguration().obeyWorldguard) return true;
        return CraftBookPlugin.plugins.getWorldGuard() == null || CraftBookPlugin.plugins.getWorldGuard().canBuild(player, block);
    }

    /**
     * Checks to see if a player can use at a location. This will return
     * true if region protection is disabled or WorldGuard is not found.
     *
     * @param player The player to check.
     * @param loc    The location to check at.
     *
     * @return whether {@code player} can build at {@code loc}
     *
     * @see GlobalRegionManager#canBuild(org.bukkit.entity.Player, org.bukkit.Location)
     */
    public static boolean canUse(Player player, Location loc, BlockFace face, Action action) {

        if (!shouldUseProtection()) return true;
        if (CraftBookPlugin.inst().getConfiguration().advancedBlockChecks) {

            PlayerInteractEvent event = new PlayerInteractEvent(player, action == null ? Action.RIGHT_CLICK_BLOCK : action, player.getItemInHand(), loc.getBlock(), face == null ? BlockFace.SELF : face);
            EventUtil.ignoreEvent(event);
            CraftBookPlugin.inst().getServer().getPluginManager().callEvent(event);
            return !event.isCancelled();
        }
        if (!CraftBookPlugin.inst().getConfiguration().obeyWorldguard) return true;
        return CraftBookPlugin.plugins.getWorldGuard() == null || CraftBookPlugin.plugins.getWorldGuard().getGlobalRegionManager().allows(DefaultFlag.USE, loc, CraftBookPlugin.plugins.getWorldGuard().wrapPlayer(player));
    }

    /**
     * Checks to see if a player can use at a location. This will return
     * true if region protection is disabled or WorldGuard is not found.
     *
     * @param player The player to check.
     * @param loc    The location to check at.
     *
     * @return whether {@code player} can build at {@code loc}
     *
     * @see GlobalRegionManager#canBuild(org.bukkit.entity.Player, org.bukkit.Location)
     */
    public static boolean canAccessInventory(Player player, Block block) {

        if (!shouldUseProtection()) return true;
        if (CraftBookPlugin.inst().getConfiguration().advancedBlockChecks) {

            if(!canUse(player, block.getLocation(), null, Action.RIGHT_CLICK_BLOCK))
                return false;
        }
        if (!CraftBookPlugin.inst().getConfiguration().obeyWorldguard) return true;
        return CraftBookPlugin.plugins.getWorldGuard() == null || CraftBookPlugin.plugins.getWorldGuard().getGlobalRegionManager().allows(DefaultFlag.CHEST_ACCESS, block.getLocation(), CraftBookPlugin.plugins.getWorldGuard().wrapPlayer(player));
    }

    /**
     * Checks to see if a block can form at a specific location. This will
     * return true if region protection is disabled or WorldGuard is not found.
     * 
     * @param block The block that is changing.
     * @param newState The new state of the block.
     * 
     * @return Whether the block can form.
     */
    public static boolean canBlockForm(Block block, BlockState newState) {

        if (!shouldUseProtection()) return true;
        if (CraftBookPlugin.inst().getConfiguration().advancedBlockChecks) {

            BlockFormEvent event = new BlockFormEvent(block, newState);
            EventUtil.ignoreEvent(event);
            CraftBookPlugin.inst().getServer().getPluginManager().callEvent(event);
            return !event.isCancelled();
        }
        if (!CraftBookPlugin.inst().getConfiguration().obeyWorldguard || CraftBookPlugin.plugins.getWorldGuard() == null) return true;

        if(newState.getType() == Material.SNOW)
            return CraftBookPlugin.plugins.getWorldGuard().getGlobalRegionManager().allows(DefaultFlag.SNOW_FALL, block.getLocation());
        if(newState.getType() == Material.ICE)
            return CraftBookPlugin.plugins.getWorldGuard().getGlobalRegionManager().allows(DefaultFlag.ICE_FORM, block.getLocation());

        return true;
    }

    /**
     * Checks whether or not protection related code should even be tested.
     * 
     * @return should check or not.
     */
    public static boolean shouldUseProtection() {

        return CraftBookPlugin.inst().getConfiguration().advancedBlockChecks || CraftBookPlugin.inst().getConfiguration().obeyWorldguard;
    }
}