package com.eanie.mealy.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.eanie.mealy.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class NotificationService extends Service {

    private static final int FOREGROUND_ID = 1001;
    private String uid = null;
	private ListenerRegistration registration;
    private long lastShownAtMs = 0;
    private static final long COOLDOWN_MS = 15_000;



    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("FGS", "NotificationService onCreate()");
        NotificationChannels.ensureCreated(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        uid = intent != null ? intent.getStringExtra("uid") : null;
        if (uid == null || uid.isEmpty()) {
            stopSelf();
            return START_NOT_STICKY;
        }
	    Log.d("FGS", "NotificationService listening for uid=" + uid);
        startListeningForNotifications();


        Log.d("FGS", "NotificationService onStartCommand()");
        Log.d("FGS", "Calling startForeground()");

        Notification notification = new NotificationCompat.Builder(
                this,
                NotificationChannels.CHANNEL_ID
        )
                .setContentTitle("Mealy is running")
                .setContentText("Listening for new notifications")
                .setSmallIcon(R.mipmap.ic_launcher) //CHANGE TO DRAWABLE?
                .setOngoing(true)
                .build();

        startForeground(FOREGROUND_ID, notification);

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private void startListeningForNotifications() {
	    Log.d("FGS", "Firestore listener triggered");

        if (registration != null) return;

	    registration = FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("notifications")
                .whereEqualTo("read", false)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null || snapshots == null) return;

                    for (var change : snapshots.getDocumentChanges()) {
                        if (change.getType() != com.google.firebase.firestore.DocumentChange.Type.ADDED)
                            continue;

                        long now = System.currentTimeMillis();
                        if (now - lastShownAtMs < COOLDOWN_MS) return;

                        lastShownAtMs = now;

                        String text = change.getDocument().getString("text");
	                    Log.d("FGS", "New unread notification: " + text);
                        showSystemNotification(text);
                    }
                });
    }
    private void showSystemNotification(String text) {
	    Log.d("FGS", "showSystemNotification() called");

        if (text == null) return;

        android.app.Notification notification =
		        new NotificationCompat.Builder(this, NotificationChannels.CHANNEL_ID)
		                .setContentTitle(getApplicationContext().getString(R.string.app_name))
                        .setContentText(text)
		                .setSmallIcon(R.drawable.ic_mealy)
                        .setAutoCancel(true)
                        .build();

	    NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        nm.notify((int) System.currentTimeMillis(), notification);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (registration != null) {
            registration.remove();
            registration = null;
        }
    }


}
