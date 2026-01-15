package com.eanie.mealy.models;

import android.app.Application;
import android.content.Context;

import com.eanie.mealy.Quantifier;
import com.eanie.mealy.Quantity;
import com.eanie.mealy.UnitType;
import com.eanie.mealy.data.ItemsRepo;
import com.eanie.mealy.ui.kitchen.KitchenItem;
import com.eanie.mealy.ui.kitchen.Resources;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import static com.eanie.mealy.ui.kitchen.KitchenItem.match;

public class ItemsViewModel extends AndroidViewModel {
	private final ItemsRepo repo = new ItemsRepo();
	private final LiveData<List<KitchenItem>> demoItems = new MutableLiveData<>(List.of(
			new KitchenItem("ing_apple", 3),
			new KitchenItem("ing_bread", new Quantity(2.5, UnitType.GRAMS, Quantifier.KILO)),
			new KitchenItem("ing_butter", new Quantity(100, UnitType.GRAMS)),
			new KitchenItem("ing_cheese", new Quantity(200, UnitType.GRAMS)),
			new KitchenItem("ing_cucumber"),
			new KitchenItem("ing_eggs", 12),
			new KitchenItem("ing_flour", new Quantity(1, UnitType.GRAMS, Quantifier.KILO)),
			new KitchenItem("ing_milk", new Quantity(250, UnitType.LITERS, Quantifier.MILLI)),
			new KitchenItem("ing_mushrooms", new Quantity(200, UnitType.GRAMS)),
			new KitchenItem("ing_onion", 5),
			new KitchenItem("ing_tomato", 2),
			new KitchenItem("ing_yogurt"),
			new KitchenItem("ing_water", new Quantity(1, UnitType.LITERS)),
			new KitchenItem("ing_salt", new Quantity(100, UnitType.GRAMS)),
			new KitchenItem("ing_pepper", new Quantity(1, UnitType.GRAMS)),
			new KitchenItem("ing_garlic", new Quantity(1, UnitType.GRAMS)),
			new KitchenItem("ing_oil", new Quantity(200, UnitType.LITERS, Quantifier.MILLI)),
			new KitchenItem("ing_sugar", new Quantity(400, UnitType.GRAMS))
	));

	private final LiveData<List<KitchenItem>> ingredients = repo.items();

	public ItemsViewModel(@NonNull Application application) {
		super(application);
	}

	public LiveData<List<KitchenItem>> ingredients() {
		// demo ingredients
		// todo: replace with repo.items()
		return demoItems;
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
