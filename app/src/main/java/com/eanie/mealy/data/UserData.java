package com.eanie.mealy.data;

import com.google.firebase.firestore.DocumentId;

public class UserData {
	@DocumentId
	private String userId = null;
	private boolean isChef = false;

	public UserData() {
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setIsChef(boolean isChef) {
		this.isChef = isChef;
	}

	public boolean getIsChef() {
		return isChef;
	}

	public boolean isChef() {
		return getIsChef();
	}
}
