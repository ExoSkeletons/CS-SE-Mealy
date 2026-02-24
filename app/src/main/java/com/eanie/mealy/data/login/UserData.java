package com.eanie.mealy.data.login;

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
    @NonNull
    private String displayName = "";




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
    @NonNull
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(@Nullable String displayName) {
        this.displayName = (displayName != null) ? displayName : "";
    }
}
