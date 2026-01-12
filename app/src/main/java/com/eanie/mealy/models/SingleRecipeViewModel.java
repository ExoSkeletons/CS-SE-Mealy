package com.eanie.mealy.models;

import com.eanie.mealy.Recipe;
import com.eanie.mealy.ui.kitchen.KitchenItem;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SingleRecipeViewModel extends ViewModel {
	public MutableLiveData<String> name = new MutableLiveData<>("");
	public MutableLiveData<String> description = new MutableLiveData<>("");
	public MutableLiveData<String> instructions = new MutableLiveData<>("");
	public MutableLiveData<List<KitchenItem>> ingredients = new MutableLiveData<>(new ArrayList<>());

	public Recipe build() {
		return new Recipe(null,
				name.getValue(), // description.getValue(),
				instructions.getValue(),
				ingredients.getValue(),
				null
		);
	}

	public Recipe buildFor(String chefId) {
		var r = build();
		r.setChefId(chefId);
		return r;
	}

	public void set(Recipe recipe) {
		name.setValue(recipe.getName());
		// description.setValue(recipe.getDescription());
		instructions.setValue(recipe.getInstructions());
		ingredients.setValue(recipe.getIngredients() != null
				? new ArrayList<>(recipe.getIngredients())
				: new ArrayList<>()
		);
	}

	public void addIngredient(KitchenItem item) {
		List<KitchenItem> current = ingredients.getValue();
		if (current != null) {
			current.add(item);
			// Re-set the value to trigger observers
			ingredients.setValue(current);
		}
	}
}
