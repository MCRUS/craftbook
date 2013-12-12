package com.sk89q.craftbook.mech.arrows;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

import com.sk89q.worldedit.blocks.ItemID;

public class FireArrow extends AbstractArrow {

    ShapelessRecipe recipe;

    public FireArrow() {

        super("Fire Arrow");
    }

    @Override
    public void addRecipe() {

        recipe = new ShapelessRecipe(new ItemStack(ItemID.ARROW, 4));
        recipe.addIngredient(Material.ARROW, 4);
        recipe.addIngredient(Material.FIREBALL, 1);
        Bukkit.addRecipe(recipe);
    }

    @Override
    public boolean onHit(ProjectileHitEvent event) {

        return false;
    }

    @Override
    public boolean onShoot(EntityShootBowEvent event) {

        return false;
    }
}