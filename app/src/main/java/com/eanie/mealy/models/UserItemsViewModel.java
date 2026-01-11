package com.eanie.mealy.models;

import android.app.Application;
import android.content.Context;

import com.eanie.mealy.Quantity;
import com.eanie.mealy.Recipe;
import com.eanie.mealy.UnitType;
import com.eanie.mealy.data.ItemsRepo;
import com.eanie.mealy.ui.kitchen.KitchenItem;
import com.eanie.mealy.ui.kitchen.Resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

public class UserItemsViewModel extends UserDataViewModel {
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

		var item = Objects.requireNonNull(myItems().getValue()).stream()
				.filter(Objects::nonNull)
				.filter(i -> i.getIngredientKey().equals(itemKey))
				.findFirst()
				.orElse(null);
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

	public double stepSize(String itemKey) {
		if (itemKey == null) return 0;

		// try get step size from resources
		Context context = getApplication();
		int stepFromResources = Resources.getInteger(context, "step_" + itemKey, -1);
		if (stepFromResources >= 0) return stepFromResources;

		// try get step size from matching existing ingredient
		var item = Objects.requireNonNull(myItems().getValue()).stream()
				.filter(Objects::nonNull)
				.filter(i -> i.getIngredientKey().equals(itemKey))
				.findFirst()
				.orElse(null);
		if (item != null)
			return item.getQuantity().getUnitType().stepAmountBy;

		return 1.0;
	}

	public UnitType unitType(String itemKey) {
		if (itemKey == null) return UnitType.COUNT;

		// try get unit type from existing matching ingredient
		var item = Objects.requireNonNull(myItems().getValue()).stream()
				.filter(Objects::nonNull)
				.filter(i -> i.getIngredientKey().equals(itemKey))
				.findFirst()
				.orElse(null);
		if (item != null)
			return item.getQuantity().getUnitType();

		// todo: try get unit type from repo

		// try get unit type from resources
		Context context = getApplication();
		String stepFromResources = Resources.getString(context, "unit_" + itemKey, null);
		if (stepFromResources != null) {
			try {
				return UnitType.valueOf(stepFromResources);
			} catch (IllegalArgumentException ignored) {
			}
		}

		return UnitType.COUNT;
	}


	public void plusAmount(String itemKey) {
		updateAmount(itemKey, stepSize(itemKey), true);
	}

	public void minusAmount(String itemKey) {
		updateAmount(itemKey, -stepSize(itemKey), true);
	}

	public KitchenItem buy(String itemKey) {
		return new KitchenItem(
				itemKey,
				new Quantity(
						stepSize(itemKey),
						unitType(itemKey)
				)
		);
	}
}