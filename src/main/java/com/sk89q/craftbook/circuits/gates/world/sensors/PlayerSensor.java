package com.sk89q.craftbook.circuits.gates.world.sensors;

import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import com.sk89q.craftbook.ChangedSign;
import com.sk89q.craftbook.bukkit.CraftBookPlugin;
import com.sk89q.craftbook.bukkit.util.BukkitUtil;
import com.sk89q.craftbook.circuits.ic.AbstractICFactory;
import com.sk89q.craftbook.circuits.ic.AbstractSelfTriggeredIC;
import com.sk89q.craftbook.circuits.ic.ChipState;
import com.sk89q.craftbook.circuits.ic.IC;
import com.sk89q.craftbook.circuits.ic.ICFactory;
import com.sk89q.craftbook.circuits.ic.RestrictedIC;
import com.sk89q.craftbook.util.ICUtil;
import com.sk89q.craftbook.util.LocationUtil;
import com.sk89q.craftbook.util.RegexUtil;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

/**
 * @author Me4502
 */
public class PlayerSensor extends AbstractSelfTriggeredIC {

    public PlayerSensor(Server server, ChangedSign block, ICFactory factory) {

        super(server, block, factory);
    }

    @Override
    public String getTitle() {

        return "Player Detection";
    }

    @Override
    public String getSignTitle() {

        return "P-DETECTION";
    }

    @Override
    public void trigger(ChipState chip) {

        if (chip.getInput(0))
            chip.setOutput(0, invertOutput ? !isDetected() : isDetected());
    }

    @Override
    public void think(ChipState state) {

        state.setOutput(0, isDetected());
    }

    Vector radius;

    Location location;
    ProtectedRegion reg;
    Type type;
    String nameLine;
    boolean invertOutput = false;

    @Override
    public void load() {

        if (getLine(3).contains(":")) {
            type = Type.getFromChar(getLine(3).replace("!", "").trim().toCharArray()[0]);
        }
        if (type == null) type = Type.PLAYER;

        invertOutput = getLine(3).contains("!");

        nameLine = getLine(3).replace("g:", "").replace("p:", "").replace("n:", "").replace("!", "").trim();

        try {
            String locInfo = getLine(2);
            if (locInfo.startsWith("r:") && CraftBookPlugin.inst().getWorldGuard() != null) {

                locInfo = locInfo.replace("r:", "");
                reg = CraftBookPlugin.inst().getWorldGuard().getRegionManager(BukkitUtil.toSign(getSign()).getWorld
                        ()).getRegion(locInfo);
                if (reg != null) return;
            }
            radius = ICUtil.parseRadius(getSign());
            String radiusString = radius.getBlockX() + "," + radius.getBlockY() + "," + radius.getBlockZ();
            if(radius.getBlockX() == radius.getBlockY() && radius.getBlockY() == radius.getBlockZ())
                radiusString = String.valueOf(radius.getBlockX());
            if (locInfo.contains("=")) {
                getSign().setLine(2, radiusString + "=" + RegexUtil.EQUALS_PATTERN.split(getSign().getLine(2))[1]);
                location = ICUtil.parseBlockLocation(getSign(), 2).getLocation();
            } else {
                getSign().setLine(2, radiusString);
                location = getBackBlock().getLocation();
            }
        } catch (Exception e) {
            location = getBackBlock().getLocation();
            BukkitUtil.printStacktrace(e);
        }
        if(reg == null && location == null)
            location = getBackBlock().getLocation();
    }

    protected boolean isDetected() {

        if (reg != null) {

            for (Player p : BukkitUtil.toSign(getSign()).getWorld().getPlayers()) {
                if (reg.contains(p.getLocation().getBlockX(), p.getLocation().getBlockY(),
                        p.getLocation().getBlockZ())) {
                    return true;
                }
            }
        }

        if (location != null) {
            if (!nameLine.isEmpty() && type == Type.PLAYER) {
                Player p = Bukkit.getPlayer(nameLine);
                if (p != null && LocationUtil.isWithinRadius(location, p.getLocation(), radius)) return true;
            }
            for (Player e : getServer().getOnlinePlayers()) {
                if (e == null || !e.isValid() || !LocationUtil.isWithinRadius(location, e.getLocation(), radius)) {
                    continue;
                }

                if (nameLine.isEmpty()) {
                    return true;
                } else if (type == Type.PLAYER && e.getName().toLowerCase(Locale.ENGLISH).startsWith(nameLine.toLowerCase(Locale.ENGLISH))) {
                    return true;
                } else if (type == Type.GROUP && CraftBookPlugin.inst().inGroup(e, nameLine)) {
                    return true;
                } else if (type == Type.PERMISSION_NODE && e.hasPermission(nameLine)) {
                    return true;
                }
            }
        }

        return false;
    }

    private enum Type {

        PLAYER('p'), GROUP('g'), PERMISSION_NODE('n');

        private Type(char prefix) {

            this.prefix = prefix;
        }

        char prefix;

        public static Type getFromChar(char c) {

            c = Character.toLowerCase(c);
            for (Type t : values()) { if (t.prefix == c) return t; }
            return null;
        }
    }

    public static class Factory extends AbstractICFactory implements RestrictedIC {

        public Factory(Server server) {

            super(server);
        }

        @Override
        public IC create(ChangedSign sign) {

            return new PlayerSensor(getServer(), sign, this);
        }

        @Override
        public String getShortDescription() {

            return "Detects players within a radius.";
        }

        @Override
        public String[] getLineHelp() {

            return new String[] {
                    "radius=x:y:z offset, or r:regionname for WorldGuard regions",
                    "p:playername, g:permgroup or n:permnode"
            };
        }
    }
}