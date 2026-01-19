package com.eanie.mealy.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.eanie.mealy.R;
import com.eanie.mealy.ui.kitchen.KitchenFragment;
import com.eanie.mealy.ui.recipe.RecipeNavFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.function.Supplier;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import static com.eanie.mealy.models.UserViewModel.withUserId;

public class MainActivity extends AppCompatActivity {
	public static void start(Activity caller) {
		Intent intent = new Intent(caller, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		caller.startActivity(intent);
		caller.setResult(RESULT_OK);
		caller.finish();
	}

	private enum NavOption {
		RECIPES(R.id.nav_recipes, RecipeNavFragment::new),
		KITCHEN(R.id.nav_kitchen, KitchenFragment::new);

		@IdRes
		final int navId;
		final Supplier<Fragment> supplier;

		NavOption(@IdRes int navId, Supplier<Fragment> supplier) {
			this.navId = navId;
			this.supplier = supplier;
		}
	}

	private String uuid = null;
	BottomNavigationView bottomNav = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		var user = FirebaseAuth.getInstance().getCurrentUser();
		if (user == null) return;
		uuid = user.getUid();

		bottomNav = findViewById(R.id.bottom_navigation);
		bottomNav.setOnItemSelectedListener(item -> {
			int id = item.getItemId();
			for (NavOption option : NavOption.values())
				if (option.navId == id) {
					getSupportFragmentManager().beginTransaction()
							.replace(R.id.container, withUserId(uuid, option.supplier.get()))
							.commit();
					return true;
				}
			return false;
		});

		if (savedInstanceState == null)
			bottomNav.setSelectedItemId(R.id.nav_kitchen);
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
