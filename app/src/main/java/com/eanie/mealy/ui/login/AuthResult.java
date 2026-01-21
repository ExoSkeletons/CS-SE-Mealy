package com.eanie.mealy.ui.login;

import com.eanie.mealy.data.login.LoggedInUser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

/**
 * Authentication result : success (user details) or error message.
 */
public class AuthResult {
	@Nullable
	private LoggedInUser success;
	@Nullable
	@StringRes
	private Integer error;
	@Nullable
	private Exception exception;

	public AuthResult(@NonNull @StringRes Integer error) {
		this.error = error;
	}

	public AuthResult(@NonNull LoggedInUser success) {
		this.success = success;
	}

	public AuthResult(@NonNull @StringRes Integer error, @NonNull Exception exception) {
		this.error = error;
		this.exception = exception;
	}

	@Nullable
	public LoggedInUser getSuccess() {
		return success;
	}

	@Nullable
	public Integer getError() {
		return error;
	}

	@Nullable
	public Exception getException() {
		return exception;
	}
}