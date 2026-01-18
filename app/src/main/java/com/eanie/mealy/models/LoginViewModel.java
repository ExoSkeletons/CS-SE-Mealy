package com.eanie.mealy.models;

import android.app.Activity;
import android.app.Application;
import android.util.Patterns;

import com.eanie.mealy.R;
import com.eanie.mealy.data.login.LoginRepo;
import com.eanie.mealy.data.login.sources.FirebaseEmailLoginDataSource;
import com.eanie.mealy.data.login.sources.FirebaseGoogleLoginDataSource;
import com.eanie.mealy.ui.login.AuthResult;
import com.eanie.mealy.ui.login.LoginFormState;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;

import androidx.annotation.NonNull;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class LoginViewModel extends AndroidViewModel {

	private final MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
	private final MutableLiveData<AuthResult> authResult = new MutableLiveData<>();
	private final LoginRepo loginRepo = new LoginRepo(
			new FirebaseEmailLoginDataSource(),
			new FirebaseGoogleLoginDataSource()
	);

	public LoginViewModel(@NonNull Application application) {
		super(application);
	}

	public LiveData<LoginFormState> getLoginFormState() {
		return loginFormState;
	}

	public LiveData<AuthResult> getAuthResult() {
		return authResult;
	}

	public void login(String username, String password) {
		loginRepo.login(
				username, password,
				result -> authResult.setValue(new AuthResult(result)),
				e -> authResult.setValue(new AuthResult(R.string.login_failed))
		);
	}

	public void register(String username, String password) {
		loginRepo.register(
				username, password,
				registeredUser -> loginRepo.login(
						username, password,
						loggedInUser ->
								authResult.setValue(new AuthResult(loggedInUser)),
						e -> authResult.setValue(new AuthResult(R.string.login_failed))
				),
				e -> authResult.setValue(new AuthResult(R.string.registration_failed))
		);
	}

	public void signInWithGoogle(Activity activity) {
		CredentialManager credentialManager = CredentialManager.create(activity);
		GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
				.setFilterByAuthorizedAccounts(false)
				.setServerClientId(getApplication().getString(R.string.web_client_id))
				.build();
		GetCredentialRequest request = new GetCredentialRequest.Builder()
				.addCredentialOption(googleIdOption)
				.build();
		credentialManager.getCredentialAsync(activity, request, null, Runnable::run,
				new CredentialManagerCallback<>() {
					@Override
					public void onResult(GetCredentialResponse result) {
						handleGoogleResult(result);
					}

					@Override
					public void onError(@NonNull GetCredentialException e) {
						e.printStackTrace();
						authResult.setValue(new AuthResult(R.string.login_failed));
					}
				});
	}

	private void handleGoogleResult(GetCredentialResponse result) {
		try {
			if (result.getCredential() instanceof CustomCredential &&
					result.getCredential().getType().equals(GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)
			) {
				GoogleIdTokenCredential credential = GoogleIdTokenCredential.createFrom(result.getCredential().getData());
				// Pass the token to the Repository
				String idToken = credential.getIdToken();
				loginRepo.signInWithGoogle(idToken,
						user -> authResult.setValue(new AuthResult(user)),
						e -> authResult.setValue(new AuthResult(R.string.login_failed))
				);
			}
		} catch (Exception e) {
			e.printStackTrace();
			authResult.setValue(new AuthResult(R.string.login_failed));
		}
	}

	public void loginDataChanged(String username, String password) {
		if (!isUserNameValid(username))
			loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
		else if (!isPasswordValid(password))
			loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
		else
			loginFormState.setValue(new LoginFormState(true));
	}

	// username validation check
	private boolean isUserNameValid(String username) {
		if (username == null) return false;
		if (username.contains("@")) return Patterns.EMAIL_ADDRESS.matcher(username).matches();
		else return !username.trim().isEmpty();
	}

	// password validation check
	private boolean isPasswordValid(String password) {
		return password != null && !password.isBlank() && password.trim().length() > 5;
	}
}