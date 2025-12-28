package com.eanie.mealy.ui.kitchen;

import com.eanie.mealy.Quantity;
import java.io.Serializable;
import com.google.firebase.firestore.DocumentId;

public class KitchenItem implements Serializable {
	@DocumentId
	private String ingredientKey;
	private Quantity quantity;

	// Required for Firestore
	public KitchenItem() {
	}

	public KitchenItem(String ingredientKey, Quantity quantity) {
		this.ingredientKey = ingredientKey;
		this.quantity = quantity;
	}

	public String getIngredietKey() {
		return ingredientKey;
	}

	public void setIngredient(String ingredientKey) {
		this.ingredientKey = ingredientKey;
	}

	public Quantity getQuantity() {
		return quantity;
	}

	public void setQuantity(Quantity quantity) {
		this.quantity = quantity;
	}
}