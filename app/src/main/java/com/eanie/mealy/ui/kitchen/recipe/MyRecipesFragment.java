package com.eanie.mealy.ui.kitchen.recipe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eanie.mealy.R;

import java.util.stream.Collectors;

public class MyRecipesFragment extends Fragment {

    private RecipeAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_recipes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rv = view.findViewById(R.id.rv_my_recipes);

        adapter = new RecipeAdapter(recipe -> {
            RecipeFragment recipeFragment = RecipeFragment.newInstance(null, recipe);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.container, recipeFragment)
                    .addToBackStack("recipe-full")
                    .commit();
        });

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);


        var myChefId = "demo-chef";

        adapter.submitList(
                RecipeBrowseDemo.createDemoRecipes()
                        .stream()
                        .filter(r -> myChefId.equals(r.getChefId()))
                        .collect(Collectors.toList())
        );
    }
}
