// $Id$
/*
 * Copyright (C) 2010, 2011 sk89q <http://www.sk89q.com>
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

package com.sk89q.craftbook.circuits.gates.world.blocks;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;

import com.sk89q.craftbook.ChangedSign;
import com.sk89q.craftbook.circuits.ic.AbstractICFactory;
import com.sk89q.craftbook.circuits.ic.IC;
import com.sk89q.craftbook.circuits.ic.ICFactory;
import com.sk89q.craftbook.circuits.ic.RestrictedIC;
import com.sk89q.craftbook.util.ItemInfo;

public class SetBlockAbove extends SetBlock {

    public SetBlockAbove(Server server, ChangedSign sign, ICFactory factory) {

        super(server, sign, factory);
    }

    @Override
    public String getTitle() {

        return "Set Block Above";
    }

    @Override
    public String getSignTitle() {

        return "SET BLOCK ABOVE";
    }

    @Override
    protected void doSet(Block body, ItemInfo item, boolean force) {

        int x = body.getX();
        int y = body.getY();
        int z = body.getZ();

        if (force || body.getWorld().getBlockAt(x, y + 1, z).getType() == Material.AIR) {
            body.getWorld().getBlockAt(x, y + 1, z).setType(item.getType());
            if (item.getData() != -1) {
                body.getWorld().getBlockAt(x, y + 1, z).setData((byte) item.getData());
            }
        }
    }

    public static class Factory extends AbstractICFactory implements RestrictedIC {

        public Factory(Server server) {

            super(server);
        }

        @Override
        public IC create(ChangedSign sign) {

            return new SetBlockAbove(getServer(), sign, this);
        }

        @Override
        public String getShortDescription() {

            return "Sets block above IC block.";
        }

        @Override
        public String[] getLineHelp() {

            return new String[] {"id{:data}", "+oFORCE if should force setting the block"};
        }
    }
}