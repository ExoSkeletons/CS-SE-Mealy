package com.eanie.mealy.models;

import com.eanie.mealy.data.kitchen.IngredientStatus;
import com.eanie.mealy.data.kitchen.KitchenItem;
import com.eanie.mealy.data.kitchen.Recipe;
import com.eanie.mealy.repos.RecipeRepo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DiscoveryViewModel extends ViewModel {
	private final RecipeRepo recipeRepo = new RecipeRepo();
	private final LiveData<List<Recipe>> recipes = recipeRepo.recipes();
	private final MutableLiveData<List<KitchenItem>> items = new MutableLiveData<>();

	private final MediatorLiveData<List<Recipe>> makeableRecipes = new MediatorLiveData<>(new ArrayList<>());
	private final MediatorLiveData<List<Recipe>> partiallyMakeableRecipes = new MediatorLiveData<>(new ArrayList<>());

	private final MediatorLiveData<Map<String, Map<String, IngredientStatus>>> makeabilityStatus = new MediatorLiveData<>(new HashMap<>());

	public DiscoveryViewModel() {
		makeableRecipes.addSource(recipes, r -> {
			if (items.getValue() != null)
				makeableRecipes.postValue(filterMakeable(r, items.getValue()));
		});
		makeableRecipes.addSource(items, i -> {
			if (recipes.getValue() != null)
				makeableRecipes.postValue(filterMakeable(recipes.getValue(), i));
		});
		partiallyMakeableRecipes.addSource(recipes, r -> {
			if (items.getValue() != null)
				partiallyMakeableRecipes.postValue(filterPartiallyMakeable(r, items.getValue()));
		});
		partiallyMakeableRecipes.addSource(items, i -> {
			if (recipes.getValue() != null)
				partiallyMakeableRecipes.postValue(filterPartiallyMakeable(recipes.getValue(), i));
		});

		makeabilityStatus.addSource(recipes, r -> {
			if (items.getValue() != null) {
				var statuses = mapRecipesMakeability(r, items.getValue());
				makeabilityStatus.postValue(statuses);
			}
		});
		makeabilityStatus.addSource(items, i -> {
			if (recipes.getValue() != null) {
				var status = mapRecipesMakeability(recipes.getValue(), i);
				makeabilityStatus.postValue(status);
			}
		});
	}

	@NonNull
	private HashMap<String, Map<String, IngredientStatus>> mapRecipesMakeability(List<Recipe> recipes, List<KitchenItem> i) {
		var status = new HashMap<String, Map<String, IngredientStatus>>();
		for (Recipe r : recipes)
			status.put(r.getId(), r.completionStatusWith(i));
		return status;
	}

	private static List<Recipe> filterMakeable(List<Recipe> recipes, List<KitchenItem> i) {
		return recipes.stream()
				.filter(r -> r.canBeMadeWith(i, true))
				.collect(Collectors.toList());
	}

	private static List<Recipe> filterPartiallyMakeable(List<Recipe> recipes, List<KitchenItem> i) {
		return recipes.stream()
				.filter(r -> r.canBeMadeWith(i, false))
				.collect(Collectors.toList());
	}

	public void updateIngredients(List<KitchenItem> ingredients) {
		items.setValue(ingredients);
	}

	public LiveData<List<Recipe>> makeableRecipes() {
		return makeableRecipes;
	}

	public LiveData<List<Recipe>> partiallyMakeableRecipes() {
		return partiallyMakeableRecipes;
	}

	public LiveData<List<Recipe>> allRecipes() {
		return recipes;
	}

	public LiveData<Map<String, Map<String, IngredientStatus>>> makeStatus() {
		return makeabilityStatus;
	}
}