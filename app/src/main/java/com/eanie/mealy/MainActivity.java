package com.eanie.mealy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.eanie.mealy.ui.kitchen.KitchenFragment;
import com.eanie.mealy.ui.kitchen.recipe.RecipeBrowseFragment;

public class MainActivity extends AppCompatActivity {

    private Button btnTabKitchen;
    private Button btnTabRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnTabKitchen = findViewById(R.id.btn_tab_kitchen);
        btnTabRecipes = findViewById(R.id.btn_tab_recipes);

        if (savedInstanceState == null) {
            showKitchenFragment();
        }

        btnTabKitchen.setOnClickListener(v -> showKitchenFragment());
        btnTabRecipes.setOnClickListener(v -> showRecipesFragment());
    }

    private void showKitchenFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, KitchenFragment.newInstance())
                .commit();
    }

    private void showRecipesFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, RecipeBrowseFragment.newInstance())
                .commit();
    }
}
