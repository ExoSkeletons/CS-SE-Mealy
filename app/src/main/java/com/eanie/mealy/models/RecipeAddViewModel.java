package com.eanie.mealy.models;

import android.app.Application;
import android.net.Uri;

import com.eanie.mealy.data.KitchenItem;
import com.eanie.mealy.data.Recipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import static com.eanie.mealy.data.KitchenItem.match;

public class RecipeAddViewModel extends UserRecipesViewModel {
	public RecipeAddViewModel(@NonNull Application application) {
		super(application);
	}

	public MutableLiveData<String> name = new MutableLiveData<>();
	public MutableLiveData<String> owner = new MutableLiveData<>();
	public MutableLiveData<List<KitchenItem>> ingredients = new MutableLiveData<>(new ArrayList<>());

	public MutableLiveData<String> instructions = new MutableLiveData<>();
	public MutableLiveData<Uri> image = new MutableLiveData<>();

	public Recipe buildRecipe() {
		return new Recipe(
				owner.getValue(),
				name.getValue(),
				instructions.getValue(),
				ingredients.getValue(),
				owner.getValue()
		);
	}

	public void saveRecipe() {
		// todo: submit image to firebase storage
		add(buildRecipe()); // save recipe to firebase
	}

	public void addIngredient(KitchenItem kitchenItem) {
		var items = ingredients.getValue();
		List<KitchenItem> newList = items == null ? new ArrayList<>() : new ArrayList<>(items);
		newList.add(kitchenItem);
		ingredients.setValue(newList);
	}

	public void updateIngredient(KitchenItem updatedItem) {
		var items = ingredients.getValue();
		if (items == null) return;

		var oldItem = match(updatedItem.getIngredientKey(), items);
		if (oldItem == null) return;

		var updatedItems = new ArrayList<>(items);
		if (Collections.replaceAll(updatedItems, oldItem, updatedItem))
			ingredients.postValue(updatedItems);
	}

	public void removeIngredient(KitchenItem item) {
		var items = ingredients.getValue();
		if (items == null) return;
		var updated = new ArrayList<>(items);
		if (updated.remove(item))
			ingredients.postValue(updated);
	}

	private void updateAmount(String itemKey, double amount, boolean additive) {
		if (itemKey == null) return;
		if (getUserId() == null) return;

		var items = ingredients.getValue();
		if (items == null) return;

		var item = match(itemKey, items);
		if (item == null) return;

		double newAmount = additive
				? amount + item.getQuantity().getAmount()
				: amount;
		if (newAmount <= 0)
			removeIngredient(item);
		else {
			var updatedItem = item.clone();
			updatedItem.getQuantity().setAmount(newAmount);
			updateIngredient(updatedItem);
		}
	}

	public void setAmount(String itemKey, double amount) {
		updateAmount(itemKey, amount, false);
	}

	public void increaseAmount(String itemKey, double amount) {
		updateAmount(itemKey, amount, true);
	}
}