package com.eanie.mealy.ui.kitchen.recipe;

import com.eanie.mealy.Recipe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavoritesStore {
    private static final Set<String> favoriteIds = new HashSet<>();
    private static final List<Recipe> favoriteRecipes = new ArrayList<>();

    public static boolean isFavorite(Recipe r) {
        return r != null && r.getId() != null && favoriteIds.contains(r.getId());
    }

    public static void setFavorite(Recipe r, boolean fav) {
        if (r == null || r.getId() == null) return;

        if (fav) {
            if (favoriteIds.add(r.getId())) {
                favoriteRecipes.add(r);
            }
        } else {
            if (favoriteIds.remove(r.getId())) {
                favoriteRecipes.removeIf(x -> r.getId().equals(x.getId()));
            }
        }
    }

    public static List<Recipe> getFavorites() {
        return new ArrayList<>(favoriteRecipes);
    }
}
