package com.eanie.mealy.ui.kitchen.recipe;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eanie.mealy.R;
import com.eanie.mealy.Recipe;
import com.eanie.mealy.models.UserItemsViewModel;
import com.eanie.mealy.ui.kitchen.KitchenItemAdapter;

public class RecipeFragment extends Fragment {
    private static final String ARG_RECIPE = "recipe";
    private Recipe recipe;

    private final UserItemsViewModel mViewModel = new ViewModelProvider(this).get(UserItemsViewModel .class);

    public RecipeFragment() {
    }

    public static RecipeFragment newInstance(Recipe recipe) {
        RecipeFragment fragment = new RecipeFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_RECIPE, recipe);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            recipe = (Recipe) getArguments().getSerializable(ARG_RECIPE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recipe, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (recipe == null) return;

        ((TextView) view.findViewById(R.id.tv_recipe_title)).setText(recipe.getName());
        ((TextView) view.findViewById(R.id.tv_recipe_short_description)).setText(recipe.getName()); // todo: get description
        ((TextView) view.findViewById(R.id.tv_preparation)).setText(recipe.getInstructions());
        ((TextView) view.findViewById(R.id.tv_recipe_author)).setText(recipe.getChefId()); // todo: get chef name

        KitchenItemAdapter adapter = new KitchenItemAdapter(false);
        adapter.submitList(recipe.getIngredients());
        RecyclerView rvIngredients = view.findViewById(R.id.rv_ingredients);
        rvIngredients.setAdapter(adapter);
        rvIngredients.setLayoutManager(new LinearLayoutManager(getContext()));

        view.findViewById(R.id.btn_make).setOnClickListener(v -> {
            mViewModel.consumeFrom(recipe);
        });
    }
}
