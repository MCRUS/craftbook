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

package com.sk89q.craftbook.circuits.ic;

import java.io.DataOutputStream;
import java.io.FileOutputStream;

import org.bukkit.Server;

import com.sk89q.craftbook.ChangedSign;
import com.sk89q.craftbook.LocalPlayer;
import com.sk89q.craftbook.bukkit.CraftBookPlugin;
import com.sk89q.craftbook.bukkit.util.BukkitUtil;

/**
 * Abstract IC factory.
 *
 * @author sk89q
 */
public abstract class AbstractICFactory implements ICFactory {

    private final Server server;

    public AbstractICFactory(Server server) {

        this.server = server;
    }

    protected Server getServer() {

        return server;
    }

    @Override
    public void verify(ChangedSign sign) throws ICVerificationException {
        // No default check needed; if the sign just has the right ID string,
        // that's good enough in most cases.
        // TODO make some IC's use this to check if its valid.
    }

    @Override
    public void checkPlayer(ChangedSign sign, LocalPlayer player) throws ICVerificationException {
        // No default check needed; if the sign just has the right ID string,
        // that's good enough in most cases.
        // TODO Use this to make some restricted IC's allowed to normal users, but limited.
    }

    @Override
    public String getShortDescription() {

        return "No Description.";
    }

    @Override
    public String getLongDescription() {

        return "Missing Description";
    }

    @Override
    public String[] getLineHelp() {

        return new String[] {null, null};
    }

    @Override
    public void unload() {

        if(this instanceof PersistentDataIC && CraftBookPlugin.inst().getConfiguration().ICSavePersistentData) {
            try {
                ((PersistentDataIC)this).getStorageFile().getParentFile().mkdirs();
                if(!((PersistentDataIC)this).getStorageFile().exists())
                    ((PersistentDataIC)this).getStorageFile().createNewFile();
                ((PersistentDataIC)this).savePersistentData(new DataOutputStream(new FileOutputStream(((PersistentDataIC)this).getStorageFile())));
            } catch(Exception e){
                CraftBookPlugin.logger().severe("Failed to save persistent IC data at " + ((PersistentDataIC)this).getStorageFile().getName());
                BukkitUtil.printStacktrace(e);
            }
        }
    }
}