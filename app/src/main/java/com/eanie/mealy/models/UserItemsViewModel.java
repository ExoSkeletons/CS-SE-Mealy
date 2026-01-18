package com.eanie.mealy.models;

import android.app.Application;

import com.eanie.mealy.data.ItemsRepo;
import com.eanie.mealy.data.KitchenItem;
import com.eanie.mealy.data.Recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import static com.eanie.mealy.data.KitchenItem.match;

public class UserItemsViewModel extends UserViewModel {
	private final ItemsRepo repo = new ItemsRepo();

	private final LiveData<List<KitchenItem>> ingredients = Transformations.switchMap(userId, id -> {
		if (id == null || id.isEmpty()) return new MutableLiveData<>(new ArrayList<>());
		return repo.itemsOf(id);
	});

	public UserItemsViewModel(@NonNull Application application) {
		super(application);
	}

	public LiveData<List<KitchenItem>> myItems() {
		return ingredients;
	}

	public void addIngredient(KitchenItem item) {
		if (getUserId() == null) return;
		repo.insert(getUserId(), item);
	}

	public void updateIngredient(KitchenItem item) {
		if (getUserId() == null) return;
		repo.insert(getUserId(), item);
	}

	public void consumeFrom(Recipe recipe) {
		if (getUserId() == null) throw new RuntimeException("No user id");

		var items = myItems().getValue();
		if (items == null) throw new RuntimeException("Items is null");

		if (!recipe.canBeMadeWith(items))
			throw new RuntimeException("Recipe cannot be made with ingredients");

		for (KitchenItem item : recipe.getIngredients())
			increaseAmount(item.getIngredientKey(), -item.getQuantity().getAmount());
	}

	private void updateAmount(String itemKey, double amount, boolean additive) {
		if (itemKey == null) return;
		if (getUserId() == null) return;

		var item = match(itemKey, Objects.requireNonNull(myItems().getValue()));
		if (item == null) return;
		item = item.clone();

		double newAmount = additive
				? amount + item.getQuantity().getAmount()
				: amount;
		if (newAmount <= 0)
			repo.delete(getUserId(), item);
		else {
			item.getQuantity().setAmount(newAmount);
			repo.insert(getUserId(), item);
		}
	}

	public void setAmount(String itemKey, double amount) {
		updateAmount(itemKey, amount, false);
	}

	public void increaseAmount(String itemKey, double amount) {
		updateAmount(itemKey, amount, true);
	}
}