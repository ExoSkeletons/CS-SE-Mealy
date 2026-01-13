package com.eanie.mealy.data;

import com.google.firebase.firestore.DocumentId;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class UserData {
	@DocumentId
	private String userId = null;
	private boolean isChef = false;
	@NonNull
	private List<String> favoriteRecipes = List.of();

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

	@NonNull
	public List<String> getFavoriteRecipes() {
		return favoriteRecipes;
	}

	public void setFavoriteRecipes(@Nullable List<String> favoriteRecipes) {
		this.favoriteRecipes = favoriteRecipes != null ? favoriteRecipes : List.of();
	}
}
