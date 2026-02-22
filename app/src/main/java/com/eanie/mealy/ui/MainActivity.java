package com.eanie.mealy.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.eanie.mealy.R;
import com.eanie.mealy.models.NotificationViewModel;
import com.eanie.mealy.notifications.NotificationPollWorker;
import com.eanie.mealy.notifications.SystemNotifications;
import com.eanie.mealy.ui.kitchen.KitchenFragment;
import com.eanie.mealy.ui.recipe.RecipeNavFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.function.Supplier;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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

	private static final int PERMISSION_REQUEST_CODE = 1075460;

    private String uuid = null;
    BottomNavigationView bottomNav = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
		            != PackageManager.PERMISSION_GRANTED)
	            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
        }

        var user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;
        uuid = user.getUid();

	    NotificationPollWorker.enqueue(this, uuid);

		var notificationViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
		notificationViewModel.setUserId(uuid);
		notificationViewModel.notifications().observe(this, list -> {
			SystemNotifications.notify(this, list);
		});

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

        selectFragment(NavOption.KITCHEN);
    }

    private void selectFragment(NavOption option) {
        if (bottomNav == null) return;
        bottomNav.setSelectedItemId(option.navId);
    }
}