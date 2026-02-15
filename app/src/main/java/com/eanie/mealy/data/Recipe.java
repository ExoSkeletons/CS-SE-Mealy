package com.eanie.mealy.data;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentId;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
		for (KitchenItem ri : this.ingredients)
			for (KitchenItem mi : ingredients)
				if (Objects.equals(ri.getIngredientKey(), mi.getIngredientKey())) {
					if (!(mi.getQuantity().getAmount() >= ri.getQuantity().getAmount()))
						return false;
					break;
				}
		return true;
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
    public java.util.List<KitchenItem> missingIngredients(java.util.List<KitchenItem> items) {
        var missing = new java.util.ArrayList<KitchenItem>();
        if (getIngredients() == null) return missing;

        for (KitchenItem req : getIngredients()) {
            if (req == null) continue;

            var have = KitchenItem.match(req.getIngredientKey(), items);

            double needAmount = (req.getQuantity() != null) ? req.getQuantity().getAmount() : 0.0;
            double haveAmount = (have != null && have.getQuantity() != null)
                    ? have.getQuantity().getAmount()
                    : 0.0;

            if (haveAmount < needAmount) {
                var clone = req.clone();
                clone.getQuantity().setAmount(needAmount - haveAmount);
                missing.add(clone);
            }
        }
        return missing;
    }

}
