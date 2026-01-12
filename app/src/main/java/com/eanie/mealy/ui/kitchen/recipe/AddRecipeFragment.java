package com.eanie.mealy.ui.kitchen.recipe;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eanie.mealy.R;
import com.eanie.mealy.models.RecipeAddViewModel;
import com.eanie.mealy.ui.kitchen.KitchenItemAdapter;

import java.util.ArrayList;

public class AddRecipeFragment extends Fragment {
    public AddRecipeFragment() {
    }

    public static AddRecipeFragment newInstance() {
        return new AddRecipeFragment();
    }

    private RecipeAddViewModel recipeAddViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        recipeAddViewModel = new ViewModelProvider(this).get(RecipeAddViewModel.class);

        return inflater.inflate(R.layout.fragment_add_recipe, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText etName = view.findViewById(R.id.et_recipe_name);
        EditText etInstructions = view.findViewById(R.id.et_instructions);
        Button btnSave = view.findViewById(R.id.btn_save_recipe);
        ImageButton btnCancel = view.findViewById(R.id.btn_close);

        RecyclerView rvIngredients = view.findViewById(R.id.rv_ingredients);
        KitchenItemAdapter ingredientAdapter = new KitchenItemAdapter(false);
        ingredientAdapter.submitList(new ArrayList<>());
        rvIngredients.setAdapter(ingredientAdapter);
        rvIngredients.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));

        recipeAddViewModel.ingredients.observe(getViewLifecycleOwner(), ingredientAdapter::submitList);

        Button btnAddIngredient = view.findViewById(R.id.btn_add_ingredient);
        btnAddIngredient.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Add ingredient", Toast.LENGTH_SHORT).show();
            // todo: show dialog to add ingredient
        });

        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                recipeAddViewModel.name.postValue(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });
        etInstructions.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                recipeAddViewModel.instructions.postValue(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        btnSave.setOnClickListener(v -> saveRecipe());

        btnCancel.setOnClickListener(v -> {
            //חזרה לדף
            getParentFragmentManager().popBackStack();
        });
    }

    private void saveRecipe() {
        recipeAddViewModel.saveRecipe();

        Toast.makeText(getContext(), "Recipe saved!", Toast.LENGTH_SHORT).show();

        // חזרה למסך הקודם
        getParentFragmentManager().popBackStack();
    }
}
