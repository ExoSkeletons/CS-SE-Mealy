package com.eanie.mealy.data.login;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public interface LoginDataSource<Cred> {
	void login(
			Cred credentials,
			OnSuccessListener<LoggedInUser> onSuccess,
			OnFailureListener onFailure
	);

	void register(
			Cred credentials,
			OnSuccessListener<LoggedInUser> onSuccess,
			OnFailureListener onFailure
	);

	void logout();
}