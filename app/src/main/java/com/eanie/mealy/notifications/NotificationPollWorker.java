package com.eanie.mealy.notifications;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class NotificationPollWorker extends Worker {
    public NotificationPollWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }
    @NonNull
    @Override
    public Result doWork() {
        final String TAG = "NOTIF_WORKER";

        try {
            Log.d(TAG, "doWork STARTED");

            var user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                Log.d(TAG, "No user logged in");
                return Result.success();
            }
            String uid = user.getUid();

            android.content.SharedPreferences sp =
                    getApplicationContext().getSharedPreferences("notif_prefs", Context.MODE_PRIVATE);

            long lastMs = sp.getLong("last_notified_ms", 0L);
            com.google.firebase.Timestamp lastTs =
                    new com.google.firebase.Timestamp(new java.util.Date(lastMs));
Log.d(TAG, "Before Firestore query");
            var query = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .collection("notifications")
                    .whereEqualTo("read", false)
                    .whereGreaterThan("timestamp", lastTs)
                    .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.ASCENDING);
            var snap = com.google.android.gms.tasks.Tasks.await(
                    query.get(),
                    10,
                    java.util.concurrent.TimeUnit.SECONDS
            );
            Log.d(TAG, "After Firestore query");

            if (snap == null || snap.isEmpty()) {
                Log.d(TAG, "No new notifications");
                return Result.success();
            }

            com.eanie.mealy.notifications.NotificationChannels.ensureCreated(getApplicationContext());

            android.app.NotificationManager nm =
                    (android.app.NotificationManager) getApplicationContext()
                            .getSystemService(Context.NOTIFICATION_SERVICE);

            long maxSeenMs = lastMs;

            for (var doc : snap.getDocuments()) {
                String text = doc.getString("text");
                if (text == null || text.isEmpty()) text = "New notification";

                com.google.firebase.Timestamp ts = doc.getTimestamp("timestamp");
                if (ts != null) {
                    long t = ts.toDate().getTime();
                    if (t > maxSeenMs) maxSeenMs = t;
                }

                android.app.Notification n =
                        new androidx.core.app.NotificationCompat.Builder(
                                getApplicationContext(),
                                com.eanie.mealy.notifications.NotificationChannels.CHANNEL_ID
                        )
                                .setContentTitle("Mealy")
                                .setContentText(text)
                                .setSmallIcon(com.eanie.mealy.R.drawable.ic_notification)
                                .setAutoCancel(true)
                                .build();

                if (nm != null) nm.notify((int) System.currentTimeMillis(), n);
            }

            sp.edit().putLong("last_notified_ms", maxSeenMs).apply();

            Log.d(TAG, "Pushed " + snap.size() + " notifications. last_notified_ms=" + maxSeenMs);
            return Result.success();

        } catch (Exception e) {
            Log.d("NOTIF_WORKER", "Worker error: " , e);
            return Result.retry();
        }
    }



}
