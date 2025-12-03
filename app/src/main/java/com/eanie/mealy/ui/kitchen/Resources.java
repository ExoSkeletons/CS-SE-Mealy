package com.eanie.mealy.ui.kitchen;

import android.content.Context;
import android.graphics.drawable.Drawable;

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
}
