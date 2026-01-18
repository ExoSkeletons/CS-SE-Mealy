package com.eanie.mealy.models;

import android.app.Application;

import com.eanie.mealy.data.Recipe;
import com.eanie.mealy.data.RecipeRepo;
import com.eanie.mealy.data.UserData;
import com.eanie.mealy.data.UserRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

public class UserInfoViewModel extends UserViewModel {
	private final UserRepo userRepo = new UserRepo();
	private final RecipeRepo recipeRepo = new RecipeRepo();

	private final LiveData<UserData> userData = Transformations.switchMap(userId, id -> {
		if (id == null || id.isEmpty())
			return new MutableLiveData<>(null);
		return userRepo.getDataOf(id);
	});

	protected final LiveData<Boolean> isChef = Transformations.map(userData, u -> u != null && u.isChef());

	// Observe all recipes once
	private final LiveData<List<Recipe>> recipes = recipeRepo.recipes();

	public final MediatorLiveData<List<Recipe>> favoriteRecipes = new MediatorLiveData<>();

	public UserInfoViewModel(@NonNull Application application) {
		super(application);

		favoriteRecipes.addSource(recipes, recipes -> updateFavoriteRecipes(recipes, userData.getValue()));
		favoriteRecipes.addSource(userData, data -> updateFavoriteRecipes(recipes.getValue(), data));
	}

	private void updateFavoriteRecipes(List<Recipe> recipes, UserData data) {
		if (recipes == null || data == null) {
			favoriteRecipes.setValue(new ArrayList<>());
			return;
		}

		List<String> favIds = data.getFavoriteRecipes();
		List<Recipe> filtered = recipes.stream()
				.filter(r -> favIds.contains(r.getId()))
				.collect(Collectors.toList());

		favoriteRecipes.setValue(filtered);
	}

	public void setFavorite(String recipeId, boolean favorite) {
		String id = getUserId();
		UserData data = userData.getValue();
		if (id == null || data == null) return;

		var favIds = new ArrayList<>(data.getFavoriteRecipes());

		var isFavorite = favIds.contains(recipeId);
		if (isFavorite == favorite) return;

		if (favorite) favIds.add(recipeId);
		else favIds.remove(recipeId);

		data.setFavoriteRecipes(favIds);
		updateData(data);
	}

	protected void updateData(UserData data) {
		if (data == null || getUserId() == null) return;
		userRepo.insert(getUserId(), data);
	}

	public LiveData<Boolean> isChef() {
		return isChef;
	}

	public void setChef(boolean isChef) {
		UserData data = userData.getValue();
		if (data == null) return;
		data.setIsChef(isChef);
		updateData(data);
	}
}
