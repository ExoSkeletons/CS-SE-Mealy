package com.eanie.mealy.data.login.sources;

import com.eanie.mealy.data.login.FirebaseLoggedInUser;
import com.eanie.mealy.data.login.LoggedInUser;
import com.eanie.mealy.data.login.cred.GAuthCredentials;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

public class FirebaseGoogleLoginDataSource extends FirebaseLoginDataSource<GAuthCredentials> {
	@Override
	public void login(GAuthCredentials cred, OnSuccessListener<LoggedInUser> onSuccess, OnFailureListener onFailure) {
		AuthCredential credential = GoogleAuthProvider.getCredential(cred.idToken(), null);
		auth
				.signInWithCredential(credential)
				.addOnSuccessListener(authResult ->
						onSuccess.onSuccess(new FirebaseLoggedInUser(authResult.getUser()))
				)
				.addOnFailureListener(onFailure);
	}

	@Override
	public void register(GAuthCredentials cred, OnSuccessListener<LoggedInUser> onSuccess, OnFailureListener onFailure) {
		// In Firebase Google Auth, login and register are functionally the same call
		login(cred, onSuccess, onFailure);
	}
}