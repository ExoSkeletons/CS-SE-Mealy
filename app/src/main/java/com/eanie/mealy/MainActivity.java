package com.eanie.mealy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.eanie.mealy.ui.kitchen.KitchenFragment;

public class MainActivity extends AppCompatActivity {
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.container, KitchenFragment.newInstance())
					.commitNow();
		}
	}
}