package com.eanie.mealy.notifications;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;

import com.eanie.mealy.R;
import com.eanie.mealy.data.notification.Notification;

import java.util.List;

import androidx.core.app.NotificationCompat;

public class SystemNotifications {
	private static final String PREFS_NAME = "notif_prefs";
	private static final String KEY_LAST_MS = "last_notified_ms";

	public static long getLastNotifiedMs(Context context) {
		SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		return sp.getLong(KEY_LAST_MS, 0L);
	}

	public static void notify(Context context, List<Notification> notifications) {
		if (notifications == null || notifications.isEmpty()) return;

		SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		long lastMs = sp.getLong(KEY_LAST_MS, 0L);
		long maxMs = lastMs;

		boolean changed = false;
		for (Notification n : notifications) {
			// Only notify if unread and newer than our last alert
			if (n.isRead() || n.getTimestamp() == null) continue;

			long currentMs = n.getTimestamp().toDate().getTime();
			if (currentMs > lastMs) {
				showNotification(context, n);
				if (currentMs > maxMs) maxMs = currentMs;
				changed = true;
			}
		}

		if (changed) sp.edit().putLong(KEY_LAST_MS, maxMs).apply();
	}

	private static void showNotification(Context context, Notification notification) {
		NotificationChannels.ensureCreated(context);
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		if (nm == null) return;

		// todo: add notification type and use it for icon, priority, category, etc.

		String text = notification.getText();
		if (text == null || text.isEmpty()) return;

		var n = new NotificationCompat.Builder(context, NotificationChannels.CHANNEL_ID)
				.setContentTitle(context.getString(R.string.app_name))
				.setContentText(text)
				.setSmallIcon(R.drawable.ic_mealy)
				.setAutoCancel(true)
				.setCategory(NotificationCompat.CATEGORY_SOCIAL)
				.setPriority(NotificationCompat.PRIORITY_DEFAULT)
				.build();

		// Use ID hash to update existing notification instead of creating duplicates in the tray
		var id = notification.getId() != null ? notification.getId().hashCode() : (int) System.currentTimeMillis();

		nm.notify(id, n);
	}
}
