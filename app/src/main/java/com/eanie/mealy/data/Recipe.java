package com.eanie.mealy.data;

import com.google.firebase.firestore.DocumentId;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;

public final class Recipe implements Serializable {
	@DocumentId
	private String id;
	private String name;
	private String instructions;
	private List<KitchenItem> ingredients;
	private String chefId;

	public Recipe() {
		this.ingredients = new ArrayList<>();
	}

	public Recipe(
			String id,
			String name,
			String instructions,
			List<KitchenItem> ingredients,
			String chefId
	) {
		this.id = id;
		this.name = name;
		this.instructions = instructions;
		this.ingredients = ingredients;
		this.chefId = chefId;
	}

	public boolean canBeMadeWith(@NonNull List<KitchenItem> ingredients) {
		// check each item in this recipe's ingredients list
		for (KitchenItem ri : this.ingredients) {
			// find the matching item in the ingredients list
			KitchenItem mi = ingredients.stream()
					.filter(i -> i.getIngredientKey().equals(ri.getIngredientKey()))
					.findFirst()
					.orElse(null);
			if (mi == null)
				return false; // no match, ingredients doesn't contain an item required by this recipe
			// check quantity
			try {
				var compare = mi.getQuantity().compareTo(ri.getQuantity());
				if (compare < 0)
					return false; // ingredients is not enough to make this recipe
			} catch (IllegalStateException e) {
				e.printStackTrace();
				return false; // comparison failed
			}
		}
		return true; // all ingredients are available
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

	public void setIngredients(List<KitchenItem> ingredients) {
		this.ingredients = ingredients;
	}

	public void setChefId(String chefId) {
		this.chefId = chefId;
	}

	public String getName() {
		return name;
	}

	public String getInstructions() {
		return instructions;
	}

	public List<KitchenItem> getIngredients() {
		return ingredients;
	}

	public String getChefId() {
		return chefId;
	}

	public String getId() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		var that = (Recipe) obj;
		return Objects.equals(this.id, that.id);
	}
}
