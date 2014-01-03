package com.sk89q.craftbook.util;

import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.sk89q.craftbook.bukkit.CraftBookPlugin;

public enum PlayerType {

    NAME('p'), GROUP('g'), PERMISSION_NODE('n'), TEAM('t'), ALL('a');

    private PlayerType(char prefix) {

        this.prefix = prefix;
    }

    char prefix;

    public static PlayerType getFromChar(char c) {

        c = Character.toLowerCase(c);
        for (PlayerType t : values()) { if (t.prefix == c) return t; }
        return PlayerType.NAME;
    }

    public boolean doesPlayerPass(Player player, String line) {

        switch(this) {
            case GROUP:
                return CraftBookPlugin.inst().inGroup(player, line);
            case NAME:
                return player.getName().toLowerCase(Locale.ENGLISH).startsWith(line.toLowerCase(Locale.ENGLISH));
            case PERMISSION_NODE:
                return player.hasPermission(line);
            case TEAM:
                try {
                    return Bukkit.getScoreboardManager().getMainScoreboard().getTeam(line).hasPlayer(player);
                } catch(Exception e) {}
                break;
            case ALL:
                return true;
            default:
                return false;
        }

        return false;
    }
}