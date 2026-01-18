package com.eanie.mealy.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.eanie.mealy.R;
import com.eanie.mealy.ui.kitchen.KitchenFragment;
import com.eanie.mealy.ui.recipe.RecipeBrowseFragment;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;

import static com.eanie.mealy.models.UserDataViewModel.withUserId;

public class MainActivity extends AppCompatActivity {
	public static void start(Activity activity) {
		Intent intent = new Intent(activity, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		activity.startActivity(intent);
		activity.setResult(RESULT_OK);
		activity.finish();
	}

	private TextView btnTabKitchen;
	private TextView btnTabRecipes;

	private String uuid = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		btnTabKitchen = findViewById(R.id.btn_tab_kitchen);
		btnTabRecipes = findViewById(R.id.btn_tab_recipes);

		var user = FirebaseAuth.getInstance().getCurrentUser();
		if (user == null) return;

		uuid = user.getUid();

		if (savedInstanceState == null) showKitchenFragment();

		btnTabKitchen.setOnClickListener(v -> showKitchenFragment());
		btnTabRecipes.setOnClickListener(v -> showRecipesFragment());
	}

	private void showKitchenFragment() {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, withUserId(uuid, new KitchenFragment()))
				.commit();
	}

	private void showRecipesFragment() {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, withUserId(uuid, new RecipeBrowseFragment()))
				.commit();
	}
}
