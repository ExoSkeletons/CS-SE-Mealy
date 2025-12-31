package com.eanie.mealy.ui.kitchen.recipe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.eanie.mealy.R;
import com.eanie.mealy.Recipe;
import com.eanie.mealy.data.RecipeRepo;
import com.eanie.mealy.ui.kitchen.KitchenItem;

import java.util.ArrayList;

public class AddRecipeFragment extends Fragment {

    private EditText etName;
    private EditText etInstructions;
    private Button btnSave;
    private Button btnCancel;

    private RecipeRepo recipeRepo;

    public AddRecipeFragment() { }

    public static AddRecipeFragment newInstance() {
        return new AddRecipeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_recipe, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etName = view.findViewById(R.id.et_recipe_name);
        etInstructions = view.findViewById(R.id.et_instructions);
        btnSave = view.findViewById(R.id.btn_save_recipe);
        btnCancel = view.findViewById(R.id.btn_cancel);

        recipeRepo = new RecipeRepo();

        btnSave.setOnClickListener(v -> saveRecipe());

        btnCancel.setOnClickListener(v -> {
            //חזרה לדף
            getParentFragmentManager().popBackStack();
        });
    }

    private void saveRecipe() {
        String name = etName.getText().toString().trim();
        String instructions = etInstructions.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError("Please enter a recipe name");
            return;
        }
        if (instructions.isEmpty()) {
            etInstructions.setError("Please enter instructions");
            return;
        }


        String chefId = "demo-user";

        Recipe newRecipe = new Recipe(
                null,
                name,
                instructions,
                new ArrayList<KitchenItem>(),
                chefId
        );

        recipeRepo.insert(newRecipe);

        Toast.makeText(getContext(), "Recipe saved!", Toast.LENGTH_SHORT).show();

        // חזרה למסך הקודם
        getParentFragmentManager().popBackStack();
    }
}
