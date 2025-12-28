package com.eanie.mealy;

import android.content.Context;
import java.io.Serializable;

public class Ingredient implements Serializable {
    private String nameKey; // e.g., "ing_apple"

    // Required for Firestore
    public Ingredient() {
    }

    public Ingredient(String nameKey) {
        this.nameKey = nameKey;
    }

    public String getNameKey() {
        return nameKey;
    }

    public void setNameKey(String nameKey) {
        this.nameKey = nameKey;
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
        return context.getResources().getIdentifier("ic_" + nameKey, "drawable", context.getPackageName());
    }
}
