package com.eanie.mealy.data;

import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;

public class FirestoreQueryLiveData<T> extends LiveData<List<T>> {
	private final Query query;
	private final Class<T> tClass;
	private ListenerRegistration registration;

	public FirestoreQueryLiveData(Class<T> tClass, Query query) {
		super(new ArrayList<>());

		this.query = query;
		this.tClass = tClass;
	}

	@Override
	protected void onActive() {
		registration = query.addSnapshotListener((snapshots, e) -> {
			if (e != null) return;
			if (snapshots != null) {
				var values = new ArrayList<T>();
				for (var doc : snapshots)
					values.add(doc.toObject(tClass));
				postValue(values);
			}
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