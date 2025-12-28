package com.eanie.mealy;

public enum UnitType {
	COUNT(""),
	GRAMS("g", 10),
	LITERS("L", 0.5f),
	TABLE_SPOONS("Tsp")
	;

	public final String postfix;
	public final double stepAmountBy;

	UnitType(String postfix, double stepAmountBy) {
		this.postfix = postfix;
		this.stepAmountBy = stepAmountBy;
	}

	UnitType(String postfix) {
		this.postfix = postfix;
		this.stepAmountBy = 1;
	}
}
