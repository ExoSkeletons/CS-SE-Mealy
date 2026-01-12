package com.eanie.mealy.data.login;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public interface LoggedInUser {

	String userId();

	String displayName();
}