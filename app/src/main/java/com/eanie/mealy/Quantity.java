package com.eanie.mealy;

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
		var r = Quantifier.reduce(amount);
		this.amount = r.second;
		this.quantifier = r.first;
	}

	public UnitType getUnitType() {
		return unitType;
	}

	public void setUnitType(UnitType unitType) {
		this.unitType = unitType;
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
		Quantity quantity = (Quantity) o;
		return Double.compare(amount, quantity.amount) == 0 && unitType == quantity.unitType;
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
}
