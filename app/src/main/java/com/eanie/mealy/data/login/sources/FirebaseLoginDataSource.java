package com.eanie.mealy.data.login.sources;

import com.eanie.mealy.data.login.LoginDataSource;
import com.google.firebase.auth.FirebaseAuth;

public abstract class FirebaseLoginDataSource<Cred> implements LoginDataSource<Cred> {
	FirebaseAuth auth = FirebaseAuth.getInstance();

	@Override
	public void logout() {
		auth.signOut();
	}
}
