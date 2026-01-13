package com.eanie.mealy.data;

import com.google.firebase.firestore.FirebaseFirestore;

import androidx.lifecycle.LiveData;

public class UserRepo {
	private final FirebaseFirestore db = FirebaseFirestore.getInstance();

	public LiveData<UserData> getDataOf(String userId) {
		return new FirestoreDocumentLiveData<>(UserData.class,
				db
						.collection("users")
						.document(userId)
		);
	}

	public void insert(String userId, UserData data) {
		db
				.collection("users")
				.document(userId)
				.set(data);
	}
}
