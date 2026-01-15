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

	public double unapply(double amount) {
		return amount / modifier;
	}

	@NonNull
	@Override
	public String toString() {
		return symbol;
	}

	public Pair<Quantifier, Double> normalize(double amount) {
		var quantifiers = Quantifier.values();

		if (amount == 0.0) return new Pair<>(this, 0.0);
		if (amount < 1.0) {
			// expand: Look for smaller units (e.g., 0.001 kg -> 1 g)
			for (int i = this.ordinal() + 1; i < quantifiers.length; i++) {
				var q = quantifiers[i];
				var normalized = q.unapply(amount);
				if (normalized >= 1.0)
					return new Pair<>(q, normalized);
			}
			var smallest = quantifiers[quantifiers.length - 1];
			return new Pair<>(smallest, smallest.unapply(amount));
		}
		// compress: Look for larger units (e.g., 25000 g -> 25 kg)
		var lastFit = this;
		for (int i = this.ordinal() - 1; i >= 0; i--) {
			var q = quantifiers[i];
			var normalized = q.unapply(amount);
			if (normalized >= 1.0) {
				lastFit = q;
				amount = normalized;
			} else break; // Stop if the next unit up is too large
		}
		return new Pair<>(lastFit, amount);
	}
}
