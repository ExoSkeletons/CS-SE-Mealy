package com.eanie.mealy;

import android.os.Bundle;
import android.widget.Button;

import com.eanie.mealy.ui.kitchen.KitchenFragment;
import com.eanie.mealy.ui.kitchen.recipe.RecipeBrowseFragment;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnTabKitchen;
    private Button btnTabRecipes;

	private final String demoUUID = "lvwuK3xBNufRynvXdB8XRqirziu2";

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
		        .replace(R.id.container, KitchenFragment.newInstance(demoUUID))
                .commit();
    }

    private void showRecipesFragment() {
        getSupportFragmentManager().beginTransaction()
		        .replace(R.id.container, RecipeBrowseFragment.newInstance(demoUUID))
                .commit();
    }
}
