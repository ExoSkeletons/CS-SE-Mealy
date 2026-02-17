package com.eanie.mealy.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

public final class NotificationChannels {

    public static final String CHANNEL_ID = "mealy_notifications";

    private NotificationChannels() { }

    public static void ensureCreated(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;

        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Mealy notifications",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        channel.setDescription("Notifications about likes and recipe usage");

        NotificationManager nm =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (nm != null) nm.createNotificationChannel(channel);
    }
}
