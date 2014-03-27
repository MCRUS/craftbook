package com.sk89q.craftbook.circuits.gates.variables;

import org.bukkit.Server;

import com.sk89q.craftbook.ChangedSign;
import com.sk89q.craftbook.LocalPlayer;
import com.sk89q.craftbook.bukkit.BukkitPlayer;
import com.sk89q.craftbook.bukkit.commands.VariableCommands;
import com.sk89q.craftbook.circuits.ic.AbstractICFactory;
import com.sk89q.craftbook.circuits.ic.AbstractSelfTriggeredIC;
import com.sk89q.craftbook.circuits.ic.ChipState;
import com.sk89q.craftbook.circuits.ic.IC;
import com.sk89q.craftbook.circuits.ic.ICFactory;
import com.sk89q.craftbook.circuits.ic.ICVerificationException;
import com.sk89q.craftbook.common.variables.VariableManager;
import com.sk89q.craftbook.util.RegexUtil;

public class IsAtLeast extends AbstractSelfTriggeredIC {

    public IsAtLeast (Server server, ChangedSign sign, ICFactory factory) {
        super(server, sign, factory);
    }

    @Override
    public String getTitle () {
        return "Is At Least";
    }

    @Override
    public String getSignTitle () {
        return "IS AT LEAST";
    }

    String variable;
    double amount;

    @Override
    public void load() {

        try {
            variable = getLine(2);
            amount = Double.parseDouble(getLine(3));
        } catch(Exception ignored) {}
    }

    @Override
    public void trigger (ChipState chip) {

        if(chip.getInput(0)) {
            chip.setOutput(0, isAtLeast());
        }
    }

    @Override
    public void think(ChipState chip) {
        chip.setOutput(0, isAtLeast());
    }

    public boolean isAtLeast() {
        String var,key;
        var = VariableManager.instance.getVariableName(variable);
        key = VariableManager.instance.getNamespace(variable);

        double existing = Double.parseDouble(VariableManager.instance.getVariable(var, key));

        return existing >= amount;
    }

    public static class Factory extends AbstractICFactory {

        public Factory(Server server) {

            super(server);
        }

        @Override
        public IC create(ChangedSign sign) {

            return new IsAtLeast(getServer(), sign, this);
        }

        @Override
        public String getShortDescription() {

            return "Checks if a variable is at least...";
        }

        @Override
        public String[] getLineHelp() {

            return new String[] {"Variable Name", "Amount"};
        }

        @Override
        public void checkPlayer(ChangedSign sign, LocalPlayer player) throws ICVerificationException {

            String[] parts = RegexUtil.PIPE_PATTERN.split(sign.getLine(2));
            if(parts.length == 1) {
                if(!VariableCommands.hasVariablePermission(((BukkitPlayer) player).getPlayer(), "global", parts[0], "use"))
                    throw new ICVerificationException("You do not have permissions to use the global variable namespace!");
            } else
                if(!VariableCommands.hasVariablePermission(((BukkitPlayer) player).getPlayer(), parts[0], parts[1], "use"))
                    throw new ICVerificationException("You do not have permissions to use the " + parts[0] + " variable namespace!");
        }

        @Override
        public void verify(ChangedSign sign) throws ICVerificationException {
            try {
                String[] parts = RegexUtil.PIPE_PATTERN.split(sign.getLine(2));
                if(parts.length == 1) {
                    if(!VariableManager.instance.hasVariable(sign.getLine(2), "global"))
                        throw new ICVerificationException("Unknown Variable!");
                } else
                    if(!VariableManager.instance.hasVariable(parts[1], parts[0]))
                        throw new ICVerificationException("Unknown Variable!");
                Double.parseDouble(sign.getLine(3));
            } catch(NumberFormatException e) {
                throw new ICVerificationException("Amount must be a number!");
            }
        }
    }
}