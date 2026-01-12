package com.eanie.mealy.data.login;

import com.eanie.mealy.data.login.cred.EmailCredentials;
import com.eanie.mealy.data.login.cred.GAuthCredentials;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {

	private final LoginDataSource<EmailCredentials> emailDataSource;
	private final LoginDataSource<GAuthCredentials> googleDataSource;


	// If user credentials will be cached in local storage, it is recommended it be encrypted
	// @see https://developer.android.com/training/articles/keystore
	private LoggedInUser user = null;

	public LoginRepository(LoginDataSource<EmailCredentials> emailDataSource, LoginDataSource<GAuthCredentials> googleDataSource) {
		this.emailDataSource = emailDataSource;
		this.googleDataSource = googleDataSource;
	}

	public boolean isLoggedIn() {
		return user != null;
	}

	public void logout() {
		user = null;
		emailDataSource.logout();
	}

	private void setLoggedInUser(LoggedInUser user) {
		this.user = user;
		// If user credentials will be cached in local storage, it is recommended it be encrypted
		// @see https://developer.android.com/training/articles/keystore
	}

	public void login(String email, String password, OnSuccessListener<LoggedInUser> onSuccess, OnFailureListener onFailure) {
		emailDataSource.login(
				new EmailCredentials(email, password),
				result -> {
					setLoggedInUser(result);
					onSuccess.onSuccess(result);
				},
				onFailure
		);
	}

	public void register(String email, String password, OnSuccessListener<LoggedInUser> onSuccess, OnFailureListener onFailure) {
		emailDataSource.register(
				new EmailCredentials(email, password),
				result -> {
					setLoggedInUser(result);
					onSuccess.onSuccess(result);
				},
				onFailure
		);
	}

	public void signInWithGoogle(String idToken, OnSuccessListener<LoggedInUser> onSuccess, OnFailureListener onFailure) {
		googleDataSource.login(
				new GAuthCredentials(idToken),
				result -> {
					setLoggedInUser(result);
					onSuccess.onSuccess(result);
				},
				onFailure
		);
	}
}