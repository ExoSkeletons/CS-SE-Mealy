package com.eanie.mealy;

import android.util.Pair;

import androidx.annotation.NonNull;

public enum Quantifier {
	KILO(1000.0, "K"),
	NONE(1.0, ""),
	// CENTI(0.01, "c"),
	MILLI(0.001, "m"),
	MICRO(0.000001, "µ"),
	// NANO(0.000000001,"n"),
	;

	private final double modifier;
	public final String symbol;

	Quantifier(double modifier, String symbol) {
		this.modifier = modifier;
		this.symbol = symbol;
	}

	public double apply(double amount) {
		return amount * modifier;
	}

	@NonNull
	@Override
	public String toString() {
		return symbol;
	}

	public static Pair<Quantifier, Double> reduce(double amount) {
		for (Quantifier m : values())
			if (amount >= m.modifier) {
				amount /= m.modifier;
				return new Pair<>(m, amount);
			}
		return new Pair<>(NONE, amount);
	}
}
