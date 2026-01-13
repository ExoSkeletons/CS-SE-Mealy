package com.eanie.mealy.models;

import android.app.Application;

import com.eanie.mealy.Recipe;
import com.eanie.mealy.data.RecipeRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

public class UserRecipesViewModel extends UserDataViewModel {
	private final RecipeRepo repo = new RecipeRepo();

	private final LiveData<List<Recipe>> myRecipes = Transformations.switchMap(userId, id -> {
		if (id == null || id.isEmpty()) return new MutableLiveData<>(new ArrayList<>());
		return repo.recipesOf(id);
	});

	public UserRecipesViewModel(@NonNull Application application) {
		super(application);
	}

	public LiveData<List<Recipe>> myRecipes() {
		return myRecipes;
	}

	public void add(Recipe recipe) {
		if (getUserId() == null) return;
		recipe.setChefId(getUserId());
		recipe.setId(null);
		repo.insert(recipe);
	}
}
