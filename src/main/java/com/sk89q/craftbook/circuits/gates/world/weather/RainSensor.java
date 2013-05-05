package com.sk89q.craftbook.circuits.gates.world.weather;

import org.bukkit.Server;

import com.sk89q.craftbook.ChangedSign;
import com.sk89q.craftbook.bukkit.util.BukkitUtil;
import com.sk89q.craftbook.circuits.ic.AbstractICFactory;
import com.sk89q.craftbook.circuits.ic.AbstractSelfTriggeredIC;
import com.sk89q.craftbook.circuits.ic.ChipState;
import com.sk89q.craftbook.circuits.ic.IC;
import com.sk89q.craftbook.circuits.ic.ICFactory;

public class RainSensor extends AbstractSelfTriggeredIC {

    public RainSensor(Server server, ChangedSign sign, ICFactory factory) {

        super(server, sign, factory);
    }

    @Override
    public String getTitle() {

        return "Is It Rain";
    }

    @Override
    public String getSignTitle() {

        return "IS IT RAIN";
    }

    @Override
    public void trigger(ChipState chip) {

        if (chip.getInput(0)) {
            chip.setOutput(0, BukkitUtil.toSign(getSign()).getWorld().hasStorm());
        }
    }

    @Override
    public void think(ChipState chip) {

        chip.setOutput(0, BukkitUtil.toSign(getSign()).getWorld().hasStorm());
    }

    public static class Factory extends AbstractICFactory {

        public Factory(Server server) {

            super(server);
        }

        @Override
        public IC create(ChangedSign sign) {

            return new RainSensor(getServer(), sign, this);
        }

        @Override
        public String getShortDescription() {

            return "Outputs high if it is raining.";
        }

        @Override
        public String[] getLineHelp() {

            String[] lines = new String[] {null, null};
            return lines;
        }
    }
}