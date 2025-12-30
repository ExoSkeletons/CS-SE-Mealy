package com.eanie.mealy.models;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.eanie.mealy.R;
import com.eanie.mealy.Recipe;
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

		Toast.makeText(
				getApplication(),
				"Added " + Resources.getString(getApplication(), item.getIngredientKey(), R.string.ing_eggs),
				Toast.LENGTH_SHORT
		).show();
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

	private void stepAmount(String itemKey, double scaledBy) {
		if (itemKey == null) return;
		if (getUserId() == null) return;

		double stepSize = stepSize(itemKey);

		increaseAmount(itemKey, stepSize * scaledBy);
	}

	public double stepSize(String itemKey) {
		if (itemKey == null) return 0;

		double stepBy = 1.0;

		Context context = getApplication();
		int stepFromResources = Resources.getInteger(context, itemKey, -1);
		if (stepFromResources >= 0) {
			stepBy = stepFromResources;
		} else {
			var item = Objects.requireNonNull(myItems().getValue()).stream()
					.filter(Objects::nonNull)
					.filter(i -> i.getIngredientKey().equals(itemKey))
					.findFirst()
					.orElse(null);
			if (item != null)
				stepBy = item.getQuantity().getUnitType().stepAmountBy;
		}
		return stepBy;
	}

	public void plusAmount(String itemKey) {
		stepAmount(itemKey, 1.0);
	}

	public void minusAmount(String itemKey) {
		stepAmount(itemKey, -1.0);
	}
}