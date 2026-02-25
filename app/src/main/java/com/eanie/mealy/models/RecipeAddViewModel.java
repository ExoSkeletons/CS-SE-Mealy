package com.eanie.mealy.models;

import android.app.Application;
import android.net.Uri;
import android.widget.Toast;

import com.eanie.mealy.data.ImageRepo;
import com.eanie.mealy.data.KitchenItem;
import com.eanie.mealy.data.Recipe;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import static com.eanie.mealy.data.KitchenItem.match;

public class RecipeAddViewModel extends UserRecipesViewModel {
	private final ImageRepo imageRepo = new ImageRepo();

	public RecipeAddViewModel(@NonNull Application application) {
		super(application);
	}

	public MutableLiveData<String> id = new MutableLiveData<>();
	public MutableLiveData<String> name = new MutableLiveData<>();
	public MutableLiveData<String> owner = new MutableLiveData<>();
	public MutableLiveData<List<KitchenItem>> ingredients = new MutableLiveData<>(new ArrayList<>());

	public MutableLiveData<String> instructions = new MutableLiveData<>();
	public MutableLiveData<Uri> image = new MutableLiveData<>();

	public void set(Recipe recipe) {
		id.setValue(recipe.getId());
		name.setValue(recipe.getName());
		instructions.setValue(recipe.getInstructions());
		ingredients.setValue(new ArrayList<>(recipe.getIngredients()));
		owner.setValue(recipe.getChefId());
		// todo: load image
	}

	public Recipe buildRecipe() {
		return new Recipe(
				id.getValue(),
				name.getValue(),
				instructions.getValue(),
				ingredients.getValue(),
				owner.getValue()
		);
	}

	public void saveRecipe(OnSuccessListener<Recipe> onComplete) {
		var recipe = buildRecipe();
		// todo: add validation
		OnFailureListener onFailure = e -> {
			e.printStackTrace();
			Toast.makeText(getApplication(), "Failed to save recipe", Toast.LENGTH_SHORT).show();
		};
		var uri = this.image.getValue();
		if (uri != null) {
			imageRepo.upload(recipe, uri,
					path -> {
						recipe.setImagePath(path);
						add(recipe, onComplete, onFailure);
					},
					e -> {
						e.printStackTrace();
						Toast.makeText(getApplication(), "Failed to upload image", Toast.LENGTH_SHORT).show();
					}
			);
			return;
		}
		add(recipe, onComplete, onFailure);
	}

	public void setImage(@Nullable Uri uri) {
		image.postValue(uri);
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