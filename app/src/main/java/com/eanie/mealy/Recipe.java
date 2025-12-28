package com.eanie.mealy;

import com.eanie.mealy.ui.kitchen.KitchenItem;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;

public record Recipe(
		String name,
		String instructions,
		List<KitchenItem> ingredients,
		String chefId
) implements Serializable {
	public boolean canBeMadeWith(List<KitchenItem> ingredients) {
		return new HashSet<>(ingredients).containsAll(this.ingredients);
	}
}
