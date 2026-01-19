package com.eanie.mealy.data;

import com.eanie.mealy.data.firestore.FirestoreQueryLiveData;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

import androidx.lifecycle.LiveData;

public class NotificationRepo {
	private final FirebaseFirestore db;

	public NotificationRepo() {
		db = FirebaseFirestore.getInstance();
	}

	public void insert(Notification notification) {
		db
				.collection("users")
				.document(notification.receiverUuid)
				.collection("notifications")
				.add(notification);
	}

	public void delete(Notification notification) {
		db
				.collection("users")
				.document(notification.receiverUuid)
				.collection("notifications")
				.document(notification.id)
				.delete();
	}

	public LiveData<List<Notification>> getForUser(String userUuid) {
		return new FirestoreQueryLiveData<>(
				Notification.class,
				db
						.collection("users")
						.document(userUuid)
						.collection("notifications")
						.orderBy("timestamp", Query.Direction.DESCENDING)
		);
	}
}
