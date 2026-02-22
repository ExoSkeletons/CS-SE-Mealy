package com.eanie.mealy.ui;

import static com.eanie.mealy.models.UserViewModel.withUserId;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.eanie.mealy.R;
import com.eanie.mealy.notifications.NotificationPollWorker;
import com.eanie.mealy.ui.kitchen.KitchenFragment;
import com.eanie.mealy.ui.recipe.RecipeNavFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

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

        Log.d("NOTIF_WORKER", "Enqueue periodic worker");

        PeriodicWorkRequest req =
                new PeriodicWorkRequest.Builder(NotificationPollWorker.class, 15, TimeUnit.MINUTES)
                        .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "notif_poll",
                ExistingPeriodicWorkPolicy.UPDATE,
                req
        );

        WorkManager.getInstance(this)
                .getWorkInfosForUniqueWorkLiveData("notif_poll")
                .observe(this, infos -> {
                    if (infos == null || infos.isEmpty()) {
                        Log.d("NOTIF_WORKER", "Work state = (no info yet)");
                        return;
                    }
                    WorkInfo info = infos.get(0);
                    Log.d("NOTIF_WORKER", "Work state = " + info.getState());
                });

        if (Build.VERSION.SDK_INT >= 33) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
		            != PackageManager.PERMISSION_GRANTED)
	            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
        }

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

        selectFragment(NavOption.KITCHEN);
    }

    private void selectFragment(NavOption option) {
        if (bottomNav == null) return;
        bottomNav.setSelectedItemId(option.navId);
    }
}