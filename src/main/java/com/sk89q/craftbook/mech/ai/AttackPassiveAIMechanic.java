package com.sk89q.craftbook.mech.ai;

import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.event.entity.EntityTargetEvent;

import com.sk89q.craftbook.bukkit.CraftBookPlugin;

public class AttackPassiveAIMechanic extends BaseAIMechanic implements TargetAIMechanic {

    public AttackPassiveAIMechanic(EntityType ... entity) {

        super(entity);
    }

    @Override
    public void onEntityTarget (EntityTargetEvent event) {

        if(event.getTarget() != null) return;
        if(!(event.getEntity() instanceof LivingEntity)) return;
        for(Entity ent : event.getEntity().getNearbyEntities(15D, 15D, 15D)) {
            if(ent instanceof Animals && ((LivingEntity) event.getEntity()).hasLineOfSight(ent)) {
                if(event.getEntity() instanceof Monster) {
                    event.setCancelled(true);
                    ((Monster) event.getEntity()).setTarget((Animals) ent);
                } else
                    event.setTarget(ent);
                CraftBookPlugin.logDebugMessage("Setting target to entity: " + ent.getType().name(), "ai-mechanics.entity-target.attack-passive");
                return;
            }
        }
    }
}