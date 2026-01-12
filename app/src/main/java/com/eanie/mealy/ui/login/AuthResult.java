package com.eanie.mealy.ui.login;

import com.eanie.mealy.data.login.LoggedInUser;

import androidx.annotation.Nullable;

/**
 * Authentication result : success (user details) or error message.
 */
class AuthResult {
	@Nullable
	private LoggedInUser success;
	@Nullable
	private Integer error;

	AuthResult(@Nullable Integer error) {
		this.error = error;
	}

	AuthResult(@Nullable LoggedInUser success) {
		this.success = success;
	}

	@Nullable
	LoggedInUser getSuccess() {
		return success;
	}

	@Nullable
	Integer getError() {
		return error;
	}
}