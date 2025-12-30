package com.eanie.mealy.ui.kitchen;

import com.eanie.mealy.Quantity;
import com.google.firebase.firestore.DocumentId;

import java.io.Serializable;
import java.util.Objects;

public class KitchenItem implements Serializable {
	@DocumentId
	private String ingredientKey;
	private Quantity quantity;

	public KitchenItem() {
	}

	public KitchenItem(String ingredientKey, Quantity quantity) {
		this.ingredientKey = ingredientKey;
		this.quantity = quantity;
	}

	public String getIngredientKey() {
		return ingredientKey;
	}

	public void setIngredientKey(String ingredientKey) {
		this.ingredientKey = ingredientKey;
	}

	public Quantity getQuantity() {
		return quantity;
	}

	public void setQuantity(Quantity quantity) {
		this.quantity = quantity;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		return Objects.equals(ingredientKey, ((KitchenItem) o).ingredientKey);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(ingredientKey);
	}
}