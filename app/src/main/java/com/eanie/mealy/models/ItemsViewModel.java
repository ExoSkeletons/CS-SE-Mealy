package com.eanie.mealy.models;

import android.app.Application;
import android.content.Context;

import com.eanie.mealy.data.ItemsRepo;
import com.eanie.mealy.data.KitchenItem;
import com.eanie.mealy.data.UnitType;
import com.eanie.mealy.ui.Resources;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import static com.eanie.mealy.data.KitchenItem.match;

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

	public double stepSize(String itemKey) {
		if (itemKey == null) return 0;

		// try get step size from resources
		int stepFromResources = Resources.getInteger(getApplication(), "step_" + itemKey, -1);
		if (stepFromResources >= 0) return stepFromResources;

		// try get step size from matching existing ingredient
		var item = match(itemKey, Objects.requireNonNull(ingredients().getValue()));
		if (item != null)
			return item.getQuantity().getUnitType().stepAmountBy;

		return 1.0;
	}

	public UnitType unitType(String itemKey) {
		if (itemKey == null) return UnitType.COUNT;

		// try get unit type from resources
		Context context = getApplication();
		String stepFromResources = Resources.getString(context, "unit_" + itemKey, null);
		if (stepFromResources != null) {
			try {
				return UnitType.valueOf(stepFromResources);
			} catch (IllegalArgumentException ignored) {
			}
		}

		// try get unit type from existing matching ingredient
		var item = match(itemKey, Objects.requireNonNull(ingredients().getValue()));
		if (item != null)
			return item.getQuantity().getUnitType();

		return UnitType.COUNT;
	}
}
