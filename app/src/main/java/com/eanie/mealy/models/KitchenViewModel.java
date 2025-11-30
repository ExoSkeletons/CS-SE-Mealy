package com.eanie.mealy.models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.eanie.mealy.data.ItemsRepo;
import com.eanie.mealy.ui.kitchen.KitchenItem;
import java.util.List;
import java.util.ArrayList;

public class KitchenViewModel extends UserDataViewModel {
	private final ItemsRepo repo = new ItemsRepo();

	private final LiveData<List<KitchenItem>> ingredients = Transformations.switchMap(userId, id -> {
		if (id == null || id.isEmpty()) return new MutableLiveData<>(new ArrayList<>());
		return repo.itemsOf(id);
	});

	public LiveData<List<KitchenItem>> getIngredients() {
		return ingredients;
	}

	public void addIngredient(KitchenItem item) {
		if (getUserId() != null) repo.addIngredient(getUserId(), item);
	}
}