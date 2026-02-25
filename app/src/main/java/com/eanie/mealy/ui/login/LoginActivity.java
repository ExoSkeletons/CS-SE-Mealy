package com.eanie.mealy.ui.login;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.eanie.mealy.R;
import com.eanie.mealy.databinding.ActivityLoginBinding;
import com.eanie.mealy.models.LoginViewModel;
import com.eanie.mealy.ui.MainActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class LoginActivity extends AppCompatActivity {
	private LoginViewModel loginVM;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		loginVM = new ViewModelProvider(this).get(LoginViewModel.class);

		final EditText usernameEditText = binding.username;
		final EditText passwordEditText = binding.password;
		final Button actionButton = binding.login;
		final TextView toggleModeButton = binding.registerLink;
		final Button loginGoogle = binding.googleLogin;
		final ProgressBar loadingProgressBar = binding.loading;

		loginVM.loginMode().observe(this, isLoginMode -> {
			actionButton.setText(isLoginMode ? R.string.action_sign_in_short : R.string.action_register);
			toggleModeButton.setText(isLoginMode ? R.string.login_mode_register : R.string.login_mode_login);
		});

		loginVM.getLoginFormState().observe(this, loginFormState -> {
			if (loginFormState == null)
				return;
			actionButton.setEnabled(loginFormState.isDataValid());
			if (loginFormState.getUsernameError() != null)
				usernameEditText.setError(getString(loginFormState.getUsernameError()));
			if (loginFormState.getPasswordError() != null)
				passwordEditText.setError(getString(loginFormState.getPasswordError()));
		});

		loginVM.getAuthResult().observe(this, result -> {
			if (result == null) return;
			loadingProgressBar.setVisibility(View.GONE);
			if (result.getError() != null) {
				showLoginFailed(result.getError(), result.getException());
				return;
			}
			if (result.getSuccess() != null)
				MainActivity.start(this);
		});

		TextWatcher afterTextChangedListener = new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				loginVM.loginDataChanged(usernameEditText.getText().toString(),
						passwordEditText.getText().toString());
			}
		};
		usernameEditText.addTextChangedListener(afterTextChangedListener);
		passwordEditText.addTextChangedListener(afterTextChangedListener);
		passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
			if (actionId == EditorInfo.IME_ACTION_DONE)
				loginVM.signIn(usernameEditText.getText().toString(), passwordEditText.getText().toString());
			return false;
		});

		toggleModeButton.setOnClickListener(v -> loginVM.toggleLoginMode());

		actionButton.setOnClickListener(v -> {
			loadingProgressBar.setVisibility(View.VISIBLE);
			loginVM.signIn(usernameEditText.getText().toString(), passwordEditText.getText().toString());
		});
		loginGoogle.setOnClickListener(v -> {
			loadingProgressBar.setVisibility(View.VISIBLE);
			loginVM.signInWithGoogle(this);
		});

		loginVM.restoreSession();
	}

	private void showLoginFailed(@NonNull @StringRes Integer errorString, @Nullable Exception e) {
		var context = getApplicationContext();
		var errorMessage = context.getString(errorString);
		if (e != null) {
			errorMessage += "\n" + e.getMessage();
			e.printStackTrace();
		}

		Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
	}
}