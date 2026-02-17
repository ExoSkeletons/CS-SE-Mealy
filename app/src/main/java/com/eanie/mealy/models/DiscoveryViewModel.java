package com.eanie.mealy.models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.eanie.mealy.data.KitchenItem;
import com.eanie.mealy.data.Recipe;
import com.eanie.mealy.data.RecipeRepo;

import java.util.ArrayList;
import java.util.List;

public class DiscoveryViewModel extends ViewModel {
	private final RecipeRepo recipeRepo = new RecipeRepo();
	private final LiveData<List<Recipe>> recipes = recipeRepo.recipes();
	private final MutableLiveData<List<KitchenItem>> items = new MutableLiveData<>();

	private final MediatorLiveData<List<Recipe>> makeableRecipes = new MediatorLiveData<>(new ArrayList<>());

	private final MediatorLiveData<Map<Recipe, Map<String, IngredientStatus>>> makeabilityStatus = new MediatorLiveData<>(new HashMap<>());

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

	@NonNull
	private HashMap<Recipe, Map<String, IngredientStatus>> mapRecipesMakeability(List<Recipe> recipes, List<KitchenItem> i) {
		var status = new HashMap<Recipe, Map<String, IngredientStatus>>();
		for (Recipe r : recipes)
			status.put(r, r.completionStatusWith(i));
		return status;
	}

	private static List<Recipe> filterMakeable(List<Recipe> recipes, List<KitchenItem> i) {
		var makeable = new ArrayList<Recipe>();
		for (var recipe : recipes)
			if (recipe.canBeMadeWith(i))
				makeable.add(recipe);
		return makeable;
	}

	public void updateIngredients(List<KitchenItem> ingredients) {
		items.setValue(ingredients);
	}

	public LiveData<List<Recipe>> makeableRecipes() {
		return makeableRecipes;
	}
    public LiveData<List<Recipe>> allRecipes() {
        return recipes;
    }

}