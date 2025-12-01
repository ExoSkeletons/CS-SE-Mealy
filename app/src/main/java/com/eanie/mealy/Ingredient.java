package com.eanie.mealy;

import android.content.Context;

public class Ingredient {
	private String nameKey; // e.g., "ing_apple"
	private String iconName; // e.g., "ic_ingredient_apple"

	// Required for Firestore
	public Ingredient() {
	}

	public Ingredient(String nameKey) {
		this(nameKey, "ic_launcher_foreground");
	}

	public Ingredient(String nameKey, String iconName) {
		this.nameKey = nameKey;
		this.iconName = iconName;
	}

	public String getNameKey() {
		return nameKey;
	}

	public void setNameKey(String nameKey) {
		this.nameKey = nameKey;
	}

	public String getIconName() {
		return iconName;
	}

	public void setIconName(String iconName) {
		this.iconName = iconName;
	}


	// Resolves the translation key to the actual string based on current phone language
	public String getDisplayName(Context context) {
		int resId = context.getResources().getIdentifier(nameKey, "string", context.getPackageName());
		if (resId != 0) {
			return context.getString(resId);
		}
		return nameKey; // Fallback
	}

	// Resolves the icon string to a drawable resource ID
	public int getIconResId(Context context) {
		return context.getResources().getIdentifier(iconName, "drawable", context.getPackageName());
	}
}