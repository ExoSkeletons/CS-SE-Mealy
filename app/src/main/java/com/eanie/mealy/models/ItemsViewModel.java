package com.eanie.mealy.models;

import android.app.Application;

import com.eanie.mealy.data.ItemsRepo;
import com.eanie.mealy.ui.kitchen.KitchenItem;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class ItemsViewModel extends AndroidViewModel {
	private final ItemsRepo repo = new ItemsRepo();

	private final LiveData<List<KitchenItem>> ingredients = repo.items();

	public ItemsViewModel(@NonNull Application application) {
		super(application);
	}

	public LiveData<List<KitchenItem>> ingredients() {
		return ingredients;
	}


	public void add(KitchenItem item) {
		if (item == null) return;
		if (item.getIngredientKey() == null) return;
		if (item.getQuantity() == null) return;
		if (item.getQuantity().getUnitType() == null) return;
		if (item.getQuantity().getAmount() <= 0) return;

		var items = ingredients().getValue();
		if (items != null) {
			var itemExists = items.stream()
					.anyMatch(i -> Objects.equals(i.getIngredientKey(), item.getIngredientKey()));
			if (itemExists) return;
		}

		repo.insert(item);
	}
}
