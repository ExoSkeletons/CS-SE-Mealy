package com.eanie.mealy.models;

import com.eanie.mealy.Recipe;
import com.eanie.mealy.data.RecipeRepo;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

public class RecipeViewModel extends UserDataViewModel {
	private final RecipeRepo repo = new RecipeRepo();

	private final LiveData<List<Recipe>> myRecipes = Transformations.switchMap(userId, id -> {
		if (id == null || id.isEmpty()) return new MutableLiveData<>(new ArrayList<>());
		return repo.recipesOf(id);
	});

	public LiveData<List<Recipe>> myRecipes() {
		return myRecipes;
	}

	public void addRecipe(Recipe recipe) {
		repo.addRecipe(recipe);
	}
}
