package com.eanie.mealy.data;

import com.eanie.mealy.ui.kitchen.KitchenItem;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import androidx.lifecycle.LiveData;

public class ItemsRepo {
	FirebaseFirestore db = FirebaseFirestore.getInstance();


	public LiveData<List<KitchenItem>> items() {
		return new FirestoreQueryLiveData<>(KitchenItem.class,
				db.collection("ingredients")
		);
	}

	public void insert(KitchenItem ingredient) {
		db
				.collection("ingredients")
				.document(ingredient.getIngredientKey())
				.set(ingredient);
	}

	public void delete(KitchenItem ingredient) {
		db
				.collection("ingredients")
				.document(ingredient.getIngredientKey())
				.delete();
	}

	public LiveData<List<KitchenItem>> itemsOf(String userId) {
		return new FirestoreQueryLiveData<>(KitchenItem.class,
				db
						.collection("users")
						.document(userId)
						.collection("ingredients")
		);
	}

	public void insert(String userId, KitchenItem ingredient) {
		db
				.collection("users")
				.document(userId)
				.collection("ingredients")
				.document(ingredient.getIngredientKey())
				.set(ingredient);
	}

	public void delete(String userId, KitchenItem ingredient) {
		db
				.collection("users")
				.document(userId)
				.collection("ingredients")
				.document(ingredient.getIngredientKey())
				.delete();
	}
}
