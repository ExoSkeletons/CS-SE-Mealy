package com.eanie.mealy;

import com.eanie.mealy.ui.kitchen.KitchenItem;
import com.google.firebase.firestore.DocumentId;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;

public final class Recipe implements Serializable {
    @DocumentId
    private String id;
    private String name;
    private String instructions;
    private final List<KitchenItem> ingredients;
    private String chefId;

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
        return new HashSet<>(ingredients).containsAll(this.ingredients);
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
