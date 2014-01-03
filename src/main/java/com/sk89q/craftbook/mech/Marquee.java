package com.sk89q.craftbook.mech;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;

import com.sk89q.craftbook.AbstractCraftBookMechanic;
import com.sk89q.craftbook.ChangedSign;
import com.sk89q.craftbook.LocalPlayer;
import com.sk89q.craftbook.bukkit.CraftBookPlugin;
import com.sk89q.craftbook.bukkit.commands.VariableCommands;
import com.sk89q.craftbook.common.VariableManager;
import com.sk89q.craftbook.util.SignUtil;
import com.sk89q.craftbook.util.events.SignClickEvent;

public class Marquee extends AbstractCraftBookMechanic {

    @Override
    public boolean enable() {
        return VariableManager.instance != null;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onSignClick(SignClickEvent event) {

        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        ChangedSign sign = event.getSign();
        if(!sign.getLine(1).equals("[Marquee]")) return;
        LocalPlayer lplayer = CraftBookPlugin.inst().wrapPlayer(event.getPlayer());
        if(!lplayer.hasPermission("craftbook.mech.marquee.use")) {
            if(CraftBookPlugin.inst().getConfiguration().showPermissionMessages)
                lplayer.printError("mech.use-permission");
            return;
        }

        String var = VariableManager.instance.getVariable(sign.getLine(2), sign.getLine(3).isEmpty() ? "global" : sign.getLine(3));
        if(var == null || var.isEmpty()) var = "variable.missing";
        lplayer.print(var);

        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onSignChange(SignChangeEvent event) {

        if(!event.getLine(1).equalsIgnoreCase("[marquee]")) return;
        LocalPlayer lplayer = CraftBookPlugin.inst().wrapPlayer(event.getPlayer());
        if(!lplayer.hasPermission("craftbook.mech.marquee")) {
            if(CraftBookPlugin.inst().getConfiguration().showPermissionMessages)
                lplayer.printError("mech.create-permission");
            SignUtil.cancelSign(event);
            return;
        }

        String namespace = event.getLine(3).isEmpty() ? "global" : event.getLine(3);
        String variable = event.getLine(2);

        if(!VariableCommands.hasVariablePermission(event.getPlayer(), namespace, variable, "get")) {
            lplayer.printError("variable.use-permissions");
            SignUtil.cancelSign(event);
        }

        String var = VariableManager.instance.getVariable(variable, namespace);
        if(var == null || var.isEmpty()) {
            lplayer.printError("variable.missing");
            SignUtil.cancelSign(event);
        }

        event.setLine(1, "[Marquee]");
    }
}