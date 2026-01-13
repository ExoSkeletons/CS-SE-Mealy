package com.eanie.mealy.ui.login;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.eanie.mealy.MainActivity;
import com.eanie.mealy.databinding.ActivityLoginBinding;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class LoginActivity extends AppCompatActivity {
	private LoginViewModel loginViewModel;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());


		loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

		final EditText usernameEditText = binding.username;
		final EditText passwordEditText = binding.password;
		final Button loginEmail = binding.login;
		final Button loginGoogle = binding.googleLogin;
		final ProgressBar loadingProgressBar = binding.loading;

		loginViewModel.getLoginFormState().observe(this, loginFormState -> {
			if (loginFormState == null)
				return;
			loginEmail.setEnabled(loginFormState.isDataValid());
			if (loginFormState.getUsernameError() != null)
				usernameEditText.setError(getString(loginFormState.getUsernameError()));
			if (loginFormState.getPasswordError() != null)
				passwordEditText.setError(getString(loginFormState.getPasswordError()));
		});

		loginViewModel.getAuthResult().observe(this, result -> {
			if (result == null) return;
			loadingProgressBar.setVisibility(View.GONE);
			if (result.getError() != null) {
				showLoginFailed(result.getError());
				return;
			}
			if (result.getSuccess() != null)
				MainActivity.start(this);
		});

		TextWatcher afterTextChangedListener = new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// ignore
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// ignore
			}

			@Override
			public void afterTextChanged(Editable s) {
				loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
						passwordEditText.getText().toString());
			}
		};
		usernameEditText.addTextChangedListener(afterTextChangedListener);
		passwordEditText.addTextChangedListener(afterTextChangedListener);
		passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				loginViewModel.login(usernameEditText.getText().toString(),
						passwordEditText.getText().toString());
			}
			return false;
		});

		loginEmail.setOnClickListener(v -> {
			loadingProgressBar.setVisibility(View.VISIBLE);
			loginViewModel.login(usernameEditText.getText().toString(),
					passwordEditText.getText().toString());
		});
		loginGoogle.setOnClickListener(v -> {
			loadingProgressBar.setVisibility(View.VISIBLE);
			loginViewModel.signInWithGoogle(this);
		});
	}

	private void showLoginFailed(@StringRes Integer errorString) {
		Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
	}
}