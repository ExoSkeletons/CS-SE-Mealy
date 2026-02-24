package com.eanie.mealy.notifications;

import android.content.Context;
import android.util.Log;

import com.eanie.mealy.data.notification.Notification;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class NotificationPollWorker extends Worker {
	private static final String TAG = "NOTIF_WORKER";

	public static void enqueue(Context context, String uuid) {
		if (uuid == null || uuid.isEmpty()) return;
		var dataMap = new HashMap<String, Object>();
		dataMap.put("uuid", uuid);

		PeriodicWorkRequest req = new PeriodicWorkRequest.Builder(
				NotificationPollWorker.class,
				15, TimeUnit.MINUTES // Minimum interval allowed by WorkManager
		)
				.setInputData(new Data(dataMap))
				.build();
		WorkManager.getInstance(context).enqueue(req);
	}

	public NotificationPollWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            Log.d(TAG, "doWork STARTED");

	        var data = getInputData();
	        String uuid = data.getString("uuid");
	        if (uuid == null || uuid.isEmpty()) {
		        Log.d(TAG, "Worker is missing uuid input data");
		        return Result.failure();
	        }

	        long lastMs = SystemNotifications.getLastNotifiedMs(getApplicationContext());
	        Timestamp lastTs = new Timestamp(new Date(lastMs));

	        var query = FirebaseFirestore.getInstance()
                    .collection("users")
			        .document(uuid)
                    .collection("notifications")
                    .whereEqualTo("read", false)
			        // .whereGreaterThan("timestamp", lastTs) todo: create fb index for multi-field query
			        ;

	        var snapshot = Tasks.await(
                    query.get(),
                    10,
			        TimeUnit.SECONDS
            );

	        if (snapshot == null || snapshot.isEmpty()) {
                Log.d(TAG, "No new notifications");
                return Result.success();
            }

	        List<Notification> notifications = new ArrayList<>();
	        for (var doc : snapshot.getDocuments()) {
		        Notification n = doc.toObject(Notification.class);
		        if (n != null) {
			        notifications.add(n);
                }
            }

	        SystemNotifications.notify(getApplicationContext(), notifications);

	        Log.d(TAG, "Processed " + notifications.size() + " notifications");
            return Result.success();

        } catch (Exception e) {
	        Log.d(TAG, "Worker error: ", e);
            return Result.retry();
        }
    }
}
