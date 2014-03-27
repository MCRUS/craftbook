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

package com.sk89q.craftbook.circuits;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.sk89q.craftbook.AbstractCraftBookMechanic;
import com.sk89q.craftbook.util.EventUtil;
import com.sk89q.craftbook.util.events.SourcedBlockRedstoneEvent;

/**
 * This mechanism allow players to toggle the fire on top of Netherrack.
 *
 * @author sk89q
 */
public class Netherrack extends AbstractCraftBookMechanic {

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockRedstoneChange(SourcedBlockRedstoneEvent event) {

        if(!EventUtil.passesFilter(event)) return;

        if(event.isMinor())
            return;

        if(event.getBlock().getType() != Material.NETHERRACK) return;

        Block above = event.getBlock().getRelative(0, 1, 0);

        if (event.isOn() && canReplaceWithFire(above.getType())) {
            above.setType(Material.FIRE);
            for(Player p : Bukkit.getOnlinePlayers())
                p.sendBlockChange(above.getLocation(), Material.FIRE, (byte) 0);
        } else if (!event.isOn() && above != null && above.getType() == Material.FIRE) {
            above.setType(Material.AIR);
            for(Player p : Bukkit.getOnlinePlayers())
                p.sendBlockChange(above.getLocation(), Material.AIR, (byte) 0);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onLeftClick(PlayerInteractEvent event) {

        if(!EventUtil.passesFilter(event)) return;

        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;
        if(event.getClickedBlock().getType() != Material.NETHERRACK) return;
        if (event.getBlockFace() == BlockFace.UP) {
            Block fire = event.getClickedBlock().getRelative(event.getBlockFace());
            if (fire.getType() == Material.FIRE && fire.getRelative(BlockFace.DOWN).isBlockPowered()) {
                event.setCancelled(true);
            }
        }
    }

    private boolean canReplaceWithFire(Material t) {

        switch (t) {
            case SNOW:
            case LONG_GRASS:
            case VINE:
            case DEAD_BUSH:
            case AIR:
                return true;
            default:
                return false;
        }
    }
}