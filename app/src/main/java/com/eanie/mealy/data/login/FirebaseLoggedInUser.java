package com.eanie.mealy.data.login;

import com.google.firebase.auth.FirebaseUser;

public record FirebaseLoggedInUser(FirebaseUser user) implements LoggedInUser {
	@Override
	public String userId() {
		return user.getUid();
	}

	@Override
	public String displayName() {
		return user.getDisplayName();
	}
}
