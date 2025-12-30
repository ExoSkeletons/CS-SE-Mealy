package com.eanie.mealy;

import java.io.Serializable;

import androidx.annotation.NonNull;

public class Quantity implements Serializable {
	private double amount;
	private UnitType unitType;

	public Quantity() {
	}

	public Quantity(double amount) {
		this(amount, UnitType.COUNT);
	}

	public Quantity(UnitType unitType) {
		this(1, unitType);
	}

	public Quantity(double amount, UnitType unitType) {
		this.amount = amount;
		this.unitType = unitType;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
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
		var quant = "";

		if (amount < 1) {
			quant = "m";
			amount *= 100;
		} else if (amount > 1000) {
			quant = "K";
			amount /= 1000;
		}

		return
				((long) amount == amount
						? (long) amount
						: String.format("%s", amount)
				) + " " + quant + unitType.postfix;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		Quantity quantity = (Quantity) o;
		return Double.compare(amount, quantity.amount) == 0 && unitType == quantity.unitType;
	}
}
