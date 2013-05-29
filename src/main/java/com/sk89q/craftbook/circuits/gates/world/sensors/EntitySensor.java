package com.sk89q.craftbook.circuits.gates.world.sensors;

import java.util.Set;

import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import com.sk89q.craftbook.ChangedSign;
import com.sk89q.craftbook.circuits.ic.AbstractICFactory;
import com.sk89q.craftbook.circuits.ic.AbstractSelfTriggeredIC;
import com.sk89q.craftbook.circuits.ic.ChipState;
import com.sk89q.craftbook.circuits.ic.IC;
import com.sk89q.craftbook.circuits.ic.ICFactory;
import com.sk89q.craftbook.circuits.ic.ICVerificationException;
import com.sk89q.craftbook.util.EntityType;
import com.sk89q.craftbook.util.ICUtil;
import com.sk89q.craftbook.util.LocationUtil;
import com.sk89q.craftbook.util.RegexUtil;
import com.sk89q.worldedit.Vector;

/**
 * @author Silthus
 */
public class EntitySensor extends AbstractSelfTriggeredIC {

    private Set<EntityType> types;

    private Block center;
    private Vector radius;

    private short minimum;

    private short minMode;

    public EntitySensor(Server server, ChangedSign block, ICFactory factory) {

        super(server, block, factory);
    }

    @Override
    public void load() {

        getSign().setLine(3, getLine(3).toUpperCase());

        // lets get the types to detect first
        types = EntityType.getDetected(getLine(3).split("<")[0].trim().split("<=")[0].trim().split(">=")[0].trim().split("==")[0].trim().split(">")[0].trim());

        if(getLine(3).contains(">="))
            minMode = 0;
        else if (getLine(3).contains("=="))
            minMode = 1;
        else if (getLine(3).contains(">"))
            minMode = 2;
        else if (getLine(3).contains("<="))
            minMode = 3;
        else if (getLine(3).contains("<"))
            minMode = 4;
        else
            minMode = 0;

        try {
            if(minMode == 0)
                minimum = Short.parseShort(getLine(3).split(">=")[1].trim());
            else if(minMode == 1)
                minimum = Short.parseShort(getLine(3).split("==")[1].trim());
            else if(minMode == 2)
                minimum = Short.parseShort(getLine(3).split(">")[1].trim());
            else if(minMode == 3)
                minimum = Short.parseShort(getLine(3).split("<=")[1].trim());
            else if(minMode == 4)
                minimum = Short.parseShort(getLine(3).split("<")[1].trim());
        } catch (Exception e) {
            minimum = 1;
        }

        // if the line contains a = the offset is given
        // the given string should look something like that:
        // radius=x:y:z or radius, e.g. 1=-2:5:11
        radius = ICUtil.parseRadius(getSign());
        String radiusString = radius.getBlockX() + "," + radius.getBlockY() + "," + radius.getBlockZ();
        if(radius.getBlockX() == radius.getBlockY() && radius.getBlockY() == radius.getBlockZ())
            radiusString = String.valueOf(radius.getBlockX());
        if (getSign().getLine(2).contains("=")) {
            getSign().setLine(2, radiusString + "=" + RegexUtil.EQUALS_PATTERN.split(getSign().getLine(2))[1]);
            center = ICUtil.parseBlockLocation(getSign());
        } else {
            getSign().setLine(2, radiusString);
            center = getBackBlock();
        }
        getSign().update(false);
    }

    @Override
    public String getTitle() {

        return "Entity Sensor";
    }

    @Override
    public String getSignTitle() {

        return "ENTITY SENSOR";
    }

    @Override
    public void trigger(ChipState chip) {

        if (chip.getInput(0)) {
            chip.setOutput(0, isDetected());
        }
    }

    @Override
    public void think(ChipState state) {

        state.setOutput(0, isDetected());
    }

    protected boolean isDetected() {

        short cur = 0;

        for (Entity entity : LocationUtil.getNearbyEntities(center.getLocation(), radius)) {
            if (entity.isValid()) {
                for (EntityType type : types) { // Check Type
                    if (type.is(entity)) { // Check Radius
                        if (LocationUtil.isWithinRadius(center.getLocation(), entity.getLocation(), radius))
                            cur++;
                    }
                }
            }
        }

        if(minMode == 0 && cur >= minimum)
            return true;
        else if (minMode == 1 && cur == minimum)
            return true;
        else if (minMode == 2 && cur > minimum)
            return true;
        else if (minMode == 3 && cur <= minimum)
            return true;
        else if (minMode == 4 && cur < minimum)
            return true;

        return false;
    }

    public static class Factory extends AbstractICFactory {

        public Factory(Server server) {

            super(server);
        }

        @Override
        public IC create(ChangedSign sign) {

            return new EntitySensor(getServer(), sign, this);
        }

        @Override
        public void verify(ChangedSign sign) throws ICVerificationException {

            ICUtil.verifySignSyntax(sign);
        }

        @Override
        public String getShortDescription() {

            return "Detects specific entity types in a given radius.";
        }

        @Override
        public String[] getLineHelp() {

            return new String[] {"radius=x:y:z offset", "Entity Types{(>=|==|>)minimum}"};
        }
    }
}
