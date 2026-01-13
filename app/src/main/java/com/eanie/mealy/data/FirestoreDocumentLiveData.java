package com.eanie.mealy.data;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.ListenerRegistration;

import androidx.lifecycle.LiveData;

public class FirestoreDocumentLiveData<T> extends LiveData<T> {
	private final DocumentReference documentReference;
	private final Class<T> tClass;
	private ListenerRegistration registration;

	public FirestoreDocumentLiveData(Class<T> tClass, DocumentReference documentReference) {
		this.documentReference = documentReference;
		this.tClass = tClass;
	}

	@Override
	protected void onActive() {
		registration = documentReference.addSnapshotListener((snapshot, e) -> {
			if (e != null) return;
			if (snapshot != null && snapshot.exists()) postValue(snapshot.toObject(tClass));
			else postValue(null);
		});
	}

	@Override
	protected void onInactive() {
		if (registration != null) {
			registration.remove();
			registration = null;
		}
	}
}
