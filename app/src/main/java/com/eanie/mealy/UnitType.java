package com.eanie.mealy;

public enum UnitType {
	COUNT(""),
	GRAMS("g"),
	LITERS("L"),
	;

	UnitType(String postfix) {
		this.postfix = postfix;
	}

	public final String postfix;
}
