package com.eanie.mealy;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Quantity implements Serializable {
	private  double amount;
	private  UnitType unitType;

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
    public void normalize() {
        if (unitType == UnitType.GRAMS && amount >= 1000) {
            amount = amount / 1000.0;       // 1000 g -> 1
            unitType = UnitType.KILOGRAMS;  // g -> Kg
        }

        if (unitType == UnitType.KILOGRAMS && amount > 0 && amount < 1) {
            amount = amount * 1000.0;       // 0.5 Kg -> 500
            unitType = UnitType.GRAMS;      // Kg -> g
        }
    }
	@NonNull
	@Override
    public String toString() {
        double a = this.amount;

        String amountStr =
                ((long) a == a)
                        ? String.valueOf((long) a)
                        : String.valueOf(a);

        return amountStr + " " + unitType.postfix;
    }

}
