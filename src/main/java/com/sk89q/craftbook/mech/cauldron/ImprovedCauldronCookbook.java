package com.sk89q.craftbook.mech.cauldron;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.inventory.ItemStack;

import com.sk89q.craftbook.LocalConfiguration;
import com.sk89q.craftbook.bukkit.CraftBookPlugin;
import com.sk89q.craftbook.bukkit.util.BukkitUtil;
import com.sk89q.craftbook.mech.cauldron.ImprovedCauldron.UnknownRecipeException;
import com.sk89q.craftbook.util.ItemSyntax;
import com.sk89q.craftbook.util.ItemUtil;
import com.sk89q.util.yaml.YAMLProcessor;

/**
 * @author Silthus
 */
public class ImprovedCauldronCookbook extends LocalConfiguration {

    public static ImprovedCauldronCookbook INSTANCE;
    private Collection<Recipe> recipes;
    protected final YAMLProcessor config;
    protected final Logger logger;

    public ImprovedCauldronCookbook(YAMLProcessor config, Logger logger) {

        INSTANCE = this;
        this.config = config;
        this.logger = logger;
        load();
    }

    @Override
    public void load() {

        recipes = new ArrayList<Recipe>();

        if (config == null) return; // If the config is null, it can't continue.

        try {
            config.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> keys = config.getKeys("cauldron-recipes");
        if (keys != null)
            for (String key : keys)
                recipes.add(new Recipe(key, config));
    }

    public Recipe getRecipe(Collection<CauldronItemStack> items) throws UnknownRecipeException {

        for (Recipe recipe : recipes) { if (recipe.checkIngredients(items)) return recipe; }
        throw new UnknownRecipeException("Are you sure you have the right ingredients?");
    }

    public static final class Recipe {

        private final String id;
        private final YAMLProcessor config;

        private String name;
        private String description;
        private Collection<CauldronItemStack> ingredients;
        private Collection<CauldronItemStack> results;
        private double chance;

        private Recipe(String id, YAMLProcessor config) {

            this.id = id;
            this.config = config;
            ingredients = new ArrayList<CauldronItemStack>();
            results = new ArrayList<CauldronItemStack>();
            chance = 60;
            load();
        }

        private void load() {

            name = config.getString("cauldron-recipes." + id + ".name");
            description = config.getString("cauldron-recipes." + id + ".description");
            ingredients = getItems("cauldron-recipes." + id + ".ingredients");
            results = getItems("cauldron-recipes." + id + ".results");
            chance = config.getDouble("cauldron-recipes." + id + ".chance", 60);
        }

        private Collection<CauldronItemStack> getItems(String path) {

            Collection<CauldronItemStack> items = new ArrayList<CauldronItemStack>();
            try {
                for (Object oitem : config.getKeys(path)) {
                    String okey = String.valueOf(oitem);
                    String item = okey.trim();

                    ItemStack stack = ItemUtil.makeItemValid(ItemSyntax.getItem(item));

                    if (stack != null) {

                        stack.setAmount(config.getInt(path + "." + okey, 1));
                        CauldronItemStack itemStack = new CauldronItemStack(stack);
                        items.add(itemStack);
                    }
                }
            } catch (Exception e) {
                CraftBookPlugin.inst().getLogger().severe("An error occured generating ingredients for cauldron recipe: " + id);
                BukkitUtil.printStacktrace(e);
            }
            return items;
        }

        public String getId() {

            return id;
        }

        public String getName() {

            return name;
        }

        public String getDescription() {

            return description;
        }

        public double getChance() {

            return chance;
        }

        /**
         * Checks if the recipe
         *
         * @param items
         *
         * @return
         */
        public boolean checkIngredients(Collection<CauldronItemStack> items) {

            if (items.size() <= 0) return false;
            int count = 0;
            for (CauldronItemStack item : items) {
                if (!ingredients.contains(item)) return false;
                count++;
            }
            return count == ingredients.size();
        }

        public Collection<CauldronItemStack> getResults() {

            return results;
        }
    }
}