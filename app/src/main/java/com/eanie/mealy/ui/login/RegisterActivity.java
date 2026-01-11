package com.eanie.mealy.ui.login;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.eanie.mealy.databinding.ActivityRegisterBinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class RegisterActivity extends AppCompatActivity {
	private LoginViewModel loginViewModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		com.eanie.mealy.databinding.ActivityRegisterBinding binding = ActivityRegisterBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

		// reuse validation logic
		TextWatcher watcher = new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				loginViewModel.loginDataChanged(binding.username.getText().toString(), binding.password.getText().toString());
			}
		};
		binding.username.addTextChangedListener(watcher);
		binding.password.addTextChangedListener(watcher);

		loginViewModel.getLoginFormState().observe(this, state -> {
			binding.register.setEnabled(state.isDataValid());
		});

		binding.register.setOnClickListener(v -> {
			binding.loading.setVisibility(View.VISIBLE);
			loginViewModel.register(binding.username.getText().toString(), binding.password.getText().toString());
		});

		// "Return to Login" Navigation
		binding.loginLink.setOnClickListener(v -> finish());

		loginViewModel.getAuthResult().observe(this, result -> {
			if (result == null) return;
			if (result.getSuccess() != null) {

			}
		});
	}
}
