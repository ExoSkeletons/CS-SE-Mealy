package com.eanie.mealy.data;

import com.google.firebase.firestore.DocumentId;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class KitchenItem implements Cloneable, Serializable {
	@NonNull
	public static String toKey(String name) {
		return "ing_" + name.toLowerCase().replace(" ", "_");
	}

	@DocumentId
	private String documentId;
	private String ingredientKey;
	private Quantity quantity;

	public KitchenItem() {
	}

	public KitchenItem(String ingredientKey) {
		this(ingredientKey, new Quantity(1.0));
	}

	public KitchenItem(String ingredientKey, int amount) {
		this(ingredientKey, new Quantity(amount, UnitType.COUNT));
	}

	public KitchenItem(String ingredientKey, Quantity quantity) {
		this.ingredientKey = ingredientKey;
		this.quantity = quantity;
	}

	public String getIngredientKey() {
		return ingredientKey != null ? ingredientKey : documentId;
	}

	public String getDocumentId() {
		return documentId != null ? documentId : ingredientKey;
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
		return Objects.equals(ingredientKey, ((KitchenItem) o).ingredientKey) &&
				Objects.equals(quantity, ((KitchenItem) o).quantity);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(ingredientKey);
	}

	@NonNull
	@Override
	public KitchenItem clone() {
		try {
			var clone = (KitchenItem) super.clone();
			clone.ingredientKey = ingredientKey;
			clone.quantity = quantity.clone();
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}

	@NonNull
	@Override
	public String toString() {
		return ingredientKey + " " + quantity + " {" + documentId + "}";
	}

	@Nullable
	public static KitchenItem match(@NonNull String itemKey, @Nullable List<KitchenItem> items) {
		if (items == null) return null;
		return items.stream()
				.filter(Objects::nonNull)
				.filter(i -> i.getIngredientKey().equals(itemKey))
				.findFirst()
				.orElse(null);
	}
}
