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

package com.sk89q.craftbook.circuits.gates.world.miscellaneous;

import org.bukkit.Server;
import org.bukkit.entity.Player;

import com.sk89q.craftbook.ChangedSign;
import com.sk89q.craftbook.LocalPlayer;
import com.sk89q.craftbook.circuits.ic.AbstractIC;
import com.sk89q.craftbook.circuits.ic.AbstractICFactory;
import com.sk89q.craftbook.circuits.ic.ChipState;
import com.sk89q.craftbook.circuits.ic.IC;
import com.sk89q.craftbook.circuits.ic.ICFactory;
import com.sk89q.craftbook.circuits.ic.ICMechanicFactory;
import com.sk89q.craftbook.circuits.ic.ICVerificationException;

public class MessageSender extends AbstractIC {

    public MessageSender(Server server, ChangedSign sign, ICFactory factory) {

        super(server, sign, factory);
    }

    @Override
    public String getTitle() {

        return "Message Sender";
    }

    @Override
    public String getSignTitle() {

        return "MESSAGE SENDER";
    }

    @Override
    public void trigger(ChipState chip) {

        if (chip.getInput(0)) {
            chip.setOutput(0, sendMessage());
        }
    }

    @Override
    public void load() {

    }

    /**
     * Returns true if a message was sent.
     *
     * @return
     */
    private boolean sendMessage() {

        boolean sent = false;
        String name = getLine(2);
        String message = getLine(3);
        Player player = getServer().getPlayer(name);

        if (player != null) {
            player.sendMessage(message.replace("&", "\u00A7"));
            sent = true;
        } else if (name.equalsIgnoreCase("BROADCAST") || name.isEmpty()) {
            getServer().broadcastMessage(message);
        }
        return sent;
    }

    public static class Factory extends AbstractICFactory {

        public Factory(Server server) {

            super(server);
        }

        @Override
        public IC create(ChangedSign sign) {

            return new MessageSender(getServer(), sign, this);
        }

        @Override
        public void checkPlayer(ChangedSign sign, LocalPlayer player) throws ICVerificationException {

            if (!sign.getLine(2).equalsIgnoreCase(player.getName()))
                if (!ICMechanicFactory.hasRestrictedPermissions(player, this, "mc1510"))
                    throw new ICVerificationException("You don't have permission to use other players!");
        }

        @Override
        public String getShortDescription() {

            return "Sends a pre-written message on high.";
        }

        @Override
        public String[] getLineHelp() {

            return new String[] {"name of player, or BROADCAST for whole server", "Message to send."};
        }
    }
}