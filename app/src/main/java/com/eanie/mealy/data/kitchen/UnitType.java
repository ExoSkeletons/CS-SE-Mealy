package com.eanie.mealy.data.kitchen;

import androidx.annotation.NonNull;

public enum UnitType {
	COUNT(""),
	GRAMS("g", 50),

	LITERS("L", 0.5f),
	TABLE_SPOONS("Tsp");

	public final String postfix;
	public final double stepAmountBy;

	UnitType(String postfix, double stepAmountBy) {
		this.postfix = postfix;
		this.stepAmountBy = stepAmountBy;
	}

	UnitType(String postfix) {
		this(postfix, 1);
	}

	@NonNull
	@Override
	public String toString() {
		return postfix;
	}
}
