package com.eanie.mealy.data.kitchen;

import java.io.Serializable;

import androidx.annotation.NonNull;

public class Quantity implements Cloneable, Serializable {
	private double amount = 1.0;
	private UnitType unitType = UnitType.COUNT;
	private Quantifier quantifier = Quantifier.NONE;

	public Quantity() {
	}

	public Quantity(double amount) {
		this(amount, UnitType.COUNT, Quantifier.NONE);
	}

	public Quantity(UnitType unitType) {
		this(1, unitType, Quantifier.NONE);
	}

	public Quantity(double amount, UnitType unitType) {
		this(amount, unitType, Quantifier.NONE);
	}

	public Quantity(double amount, UnitType unitType, Quantifier quantifier) {
		this.unitType = unitType;
		setAmount(quantifier.apply(amount));
	}


	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		var r = quantifier.normalize(amount);
		this.amount = r.second;
		this.quantifier = r.first;
	}

	public UnitType getUnitType() {
		return unitType;
	}

	public void setUnitType(UnitType unitType) {
		this.unitType = unitType;
	}

	public Quantifier getQuantifier() {
		return quantifier;
	}

	public void setQuantifier(Quantifier quantifier) {
		this.quantifier = quantifier;
	}

	@NonNull
	@Override
	public String toString() {
		var amount = this.amount;
		var unitType = this.unitType;
		var modifier = this.quantifier;

		return
				((long) amount == amount
						? (long) amount
						: String.format("%s", amount)
				) + " " + modifier.symbol + unitType.postfix;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		Quantity oq = (Quantity) o;
		return Double.compare(amount, oq.amount) == 0 && quantifier == oq.quantifier && unitType == oq.unitType;
	}

	@NonNull
	@Override
	public Quantity clone() {
		try {
			var clone = (Quantity) super.clone();
			clone.amount = amount;
			clone.unitType = unitType;
			clone.quantifier = quantifier;
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}

	public double subtract(Quantity other) {
		if (!other.getUnitType().equals(getUnitType()))
			throw new IllegalStateException("Cannot compare differing unit types " + getUnitType() + "," + other.getUnitType()); // todo: add conversion

		var a1 = quantifier.apply(amount);
		var a2 = other.quantifier.apply(other.amount);
		return a1 - a2;
	}
}
