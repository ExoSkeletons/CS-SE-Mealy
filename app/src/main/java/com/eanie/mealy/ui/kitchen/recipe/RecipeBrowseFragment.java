package com.eanie.mealy.ui.kitchen.recipe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eanie.mealy.R;
import com.eanie.mealy.Recipe;
import com.eanie.mealy.ui.kitchen.KitchenItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecipeBrowseFragment extends Fragment {

    public RecipeBrowseFragment() {
    }

    public static RecipeBrowseFragment newInstance() {
        return new RecipeBrowseFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recipe_browse, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rvRecipes = view.findViewById(R.id.rv_recipes);

        List<Recipe> demoRecipes = createDemoRecipes();

        RecipeAdapter adapter = new RecipeAdapter(recipe -> {
            // navigate to recipe fragment
            var recipeFragment = RecipeFragment.newInstance(recipe);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.container, recipeFragment)
                    .addToBackStack("recipe-full")
                    .commit();
        });
        adapter.submitList(demoRecipes);

        rvRecipes.setAdapter(adapter);
        rvRecipes.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private List<Recipe> createDemoRecipes() {
        List<Recipe> list = new ArrayList<>();
        List<KitchenItem> emptyIngredients = Collections.emptyList();

        list.add(new Recipe(
                "Pasta Bolognese",
                "Classic pasta with meat sauce.",
                emptyIngredients,
                "demo-chef"
        ));

        list.add(new Recipe(
                "Vegetable Stir-Fry",
                "Quick dinner with mixed vegetables.",
                emptyIngredients,
                "demo-chef"
        ));

        list.add(new Recipe(
                "Chocolate Cake",
                "Rich and moist chocolate cake.",
                emptyIngredients,
                "demo-chef"
        ));

        list.add(new Recipe(
                "Omelette",
                "Simple omelette with cheese and herbs.",
                emptyIngredients,
                "demo-chef"
        ));

        return list;
    }
}
