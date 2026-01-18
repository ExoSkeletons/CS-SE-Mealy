package com.eanie.mealy.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.eanie.mealy.R;

public class Resources {
	public static Drawable getDrawable(Context context, String key, int defaultResId) {
		int resId = context.getResources().getIdentifier("ic_" + key, "drawable", context.getPackageName());
		return context.getDrawable(resId != 0 ? resId : defaultResId);
	}

	public static String getString(Context context, String key, int defaultResId) {
		int resId = context.getResources().getIdentifier(key, "string", context.getPackageName());
		return context.getString(resId != 0 ? resId : defaultResId);
	}

	public static String getString(Context context, String key, String defaultString) {
		int resId = context.getResources().getIdentifier(key, "string", context.getPackageName());
		return resId != 0 ? context.getString(resId) : defaultString;
	}

	public static int getInteger(Context context, String key, int defaultInt) {
		int resId = context.getResources().getIdentifier(key, "int", context.getPackageName());
		return resId != 0 ? context.getResources().getInteger(resId) : defaultInt;
	}

	public static Drawable getItemIcon(Context context, String key) {
		return getDrawable(context, key, R.drawable.ic_ing_butter);
	}
}
