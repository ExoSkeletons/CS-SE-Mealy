package com.eanie.mealy.models;

import com.eanie.mealy.Recipe;
import com.eanie.mealy.data.RecipeRepo;
import com.eanie.mealy.ui.kitchen.KitchenItem;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DiscoveryViewModel extends ViewModel {
	private final RecipeRepo recipeRepo = new RecipeRepo();
	private final LiveData<List<Recipe>> recipes = recipeRepo.recipes();
	private final MutableLiveData<List<KitchenItem>> items = new MutableLiveData<>();

	private final MediatorLiveData<List<Recipe>> makeableRecipes = new MediatorLiveData<>(new ArrayList<>());

	public DiscoveryViewModel() {
		makeableRecipes.addSource(recipes, r -> {
			if (items.getValue() != null)
				makeableRecipes.postValue(filterMakeable(r, items.getValue()));
		});
		makeableRecipes.addSource(items, i -> {
			if (recipes.getValue() != null)
				makeableRecipes.postValue(filterMakeable(recipes.getValue(), i));
		});
	}

	private static List<Recipe> filterMakeable(List<Recipe> recipes, List<KitchenItem> i) {
		var makeable = new ArrayList<Recipe>();
		for (var recipe : recipes)
			if (recipe.canBeMadeWith(i))
				makeable.add(recipe);
		return makeable;
	}

	// Calls this whenever the ItemsViewModel updates
	public void updateIngredients(List<KitchenItem> ingredients) {
		items.setValue(ingredients);
	}

	public LiveData<List<Recipe>> makeableRecipes() {
		return makeableRecipes;
	}
}