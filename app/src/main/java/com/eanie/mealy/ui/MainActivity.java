package com.eanie.mealy.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.eanie.mealy.R;
import com.eanie.mealy.ui.kitchen.KitchenFragment;
import com.eanie.mealy.ui.recipe.RecipeNavFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;

import static com.eanie.mealy.models.UserViewModel.withUserId;

public class MainActivity extends AppCompatActivity {
	public static void start(Activity activity) {
		Intent intent = new Intent(activity, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		activity.startActivity(intent);
		activity.setResult(RESULT_OK);
		activity.finish();
	}

	private String uuid = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		var user = FirebaseAuth.getInstance().getCurrentUser();
		if (user == null) return;

		uuid = user.getUid();

		if (savedInstanceState == null) showKitchenFragment();

		BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
		bottomNav.setOnItemSelectedListener(item -> {
			int id = item.getItemId();
			if (id == R.id.nav_kitchen) {
				showKitchenFragment();
				return true;
			} else if (id == R.id.nav_recipes) {
				showRecipesFragment();
				return true;
			}
			return false;
		});
	}

	private void showKitchenFragment() {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, withUserId(uuid, new KitchenFragment()))
				.commit();
	}

	private void showRecipesFragment() {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, withUserId(uuid, new RecipeNavFragment()))
				.commit();
	}
}
