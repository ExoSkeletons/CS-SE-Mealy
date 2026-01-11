package com.eanie.mealy.data.login.sources;

import com.eanie.mealy.data.login.FirebaseLoggedInUser;
import com.eanie.mealy.data.login.LoggedInUser;
import com.eanie.mealy.data.login.cred.EmailCredentials;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class FirebaseEmailLoginDataSource extends FirebaseLoginDataSource<EmailCredentials> {
	@Override
	public void login(EmailCredentials credentials, OnSuccessListener<LoggedInUser> onSuccess, OnFailureListener onFailure) {
		auth
				.signInWithEmailAndPassword(credentials.email(), credentials.password())
				.addOnSuccessListener(authResult -> onSuccess.onSuccess(new FirebaseLoggedInUser(authResult.getUser())))
				.addOnFailureListener(onFailure);
	}

	@Override
	public void register(EmailCredentials credentials, OnSuccessListener<LoggedInUser> onSuccess, OnFailureListener onFailure) {
		auth
				.createUserWithEmailAndPassword(credentials.email(), credentials.password())
				.addOnSuccessListener(authResult -> onSuccess.onSuccess(new FirebaseLoggedInUser(authResult.getUser())))
				.addOnFailureListener(onFailure);
	}
}
