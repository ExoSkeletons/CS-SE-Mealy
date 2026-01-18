package com.eanie.mealy.ui.login;

import com.eanie.mealy.data.login.LoggedInUser;

import androidx.annotation.Nullable;

/**
 * Authentication result : success (user details) or error message.
 */
public class AuthResult {
	@Nullable
	private LoggedInUser success;
	@Nullable
	private Integer error;

	public AuthResult(@Nullable Integer error) {
		this.error = error;
	}

	public AuthResult(@Nullable LoggedInUser success) {
		this.success = success;
	}

	@Nullable
	public LoggedInUser getSuccess() {
		return success;
	}

	@Nullable
	public Integer getError() {
		return error;
	}
}