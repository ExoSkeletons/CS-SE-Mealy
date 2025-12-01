package com.eanie.mealy.ui.kitchen;

import com.eanie.mealy.Ingredient;
import com.eanie.mealy.Quantity;

public class KitchenItem {
	// If you want to query deeper properties, embedding the object is fine for Firestore.
	private Ingredient ingredient;
	private Quantity quantity;

	// Required for Firestore
	public KitchenItem() {}

	public KitchenItem(Ingredient ingredient, Quantity quantity) {
		this.ingredient = ingredient;
		this.quantity = quantity;
	}

	public Ingredient getIngredient() { return ingredient; }
	public void setIngredient(Ingredient ingredient) { this.ingredient = ingredient; }

	public Quantity getQuantity() { return quantity; }
	public void setQuantity(Quantity quantity) { this.quantity = quantity; }
}