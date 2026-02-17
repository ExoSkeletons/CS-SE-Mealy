package com.eanie.mealy.data;

import android.util.Log;

import com.google.firebase.firestore.DocumentId;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;

public final class Recipe implements Serializable {
	@DocumentId
	private String id;
	private String name;
	private String instructions;
	private List<KitchenItem> ingredients;
	private String imageUri;
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

	public boolean canBeMadeWith(@NonNull List<KitchenItem> ingredients, boolean matchQuantity) {
		// check each item in this recipe's ingredients list
		for (KitchenItem req : this.ingredients) {
			// find the matching item in the ingredients list
			KitchenItem item = ingredients.stream()
					.filter(i -> i.getIngredientKey().equals(req.getIngredientKey()))
					.findFirst()
					.orElse(null);
			if (item == null)
				return false; // no match, ingredients doesn't contain an item required by this recipe
			// check quantity
			if (matchQuantity)
				try {
					var deficit = req.getQuantity().subtract(item.getQuantity());
					if (deficit > 0)
						return false; // ingredient quantity is not enough to make this recipe
				} catch (IllegalStateException e) {
					Log.w("Recipe.canBeMadeWith", "Comparison failed between " + item + " and " + req + ".");
					e.printStackTrace();
					return false; // comparison failed
				}
		}
		return true; // all ingredients are available
	}

	public List<KitchenItem> calculateMissing(List<KitchenItem> existing) {
		var missing = new ArrayList<KitchenItem>();
		if (getIngredients() == null) return missing;

		for (KitchenItem req : getIngredients()) {
			if (req == null) continue;

			var item = KitchenItem.match(req.getIngredientKey(), existing);
			if (item == null || item.getQuantity() == null) {
				missing.add(req);
				continue;
			}

			try {
				var deficit = req.getQuantity().subtract(item.getQuantity());
				if (deficit > 0) {
					var diff = req.clone();
					diff.getQuantity().setAmount(deficit);
					missing.add(diff);
				}
			} catch (IllegalStateException e) {
				Log.w("Recipe.calculateMissing", "Comparison failed between " + item + " and " + req + ".");
				missing.add(req);
				e.printStackTrace();
			}
		}
		return missing;
	}

	public Map<String, IngredientStatus> completionStatusWith(@NonNull List<KitchenItem> existingItems) {
		Map<String, IngredientStatus> map = new HashMap<>();
		if (this.ingredients == null) return map;

		for (KitchenItem req : this.ingredients) {
			if (req == null) continue;
			String key = req.getIngredientKey();

			KitchenItem item = KitchenItem.match(key, existingItems);
			if (item == null || item.getQuantity() == null) {
				map.put(key, IngredientStatus.MISSING);
				continue;
			}
			if (req.getQuantity() == null) {
				map.put(key, IngredientStatus.ENOUGH);
				continue;
			}

			try {
				var deficit = req.getQuantity().subtract(item.getQuantity());
				if (deficit > 0) {
					map.put(key, IngredientStatus.PARTIAL);
					continue;
				}
			} catch (IllegalStateException e) {
				Log.w("Recipe.completionStatusWith", "Comparison failed between " + item + " and " + req + ".");
				map.put(key, IngredientStatus.MISSING);
				e.printStackTrace();
				continue;
			}

			map.put(key, IngredientStatus.ENOUGH);
		}

		return map;
	}

	public String getImageUri() {
		return imageUri;
	}


	public void setImageUri(String imageUri) {
		this.imageUri = imageUri;
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
