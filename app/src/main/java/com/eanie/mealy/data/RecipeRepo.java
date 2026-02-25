package com.eanie.mealy.data;

import com.eanie.mealy.data.firestore.FirestoreQueryLiveData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import androidx.lifecycle.LiveData;

public class RecipeRepo {
	private final FirebaseFirestore db = FirebaseFirestore.getInstance();

	public LiveData<List<Recipe>> recipesOf(String userId) {
		return new FirestoreQueryLiveData<>(Recipe.class,
				db
						.collection("recipes")
						.whereEqualTo("chefId", userId)
		);
	}

	public LiveData<List<Recipe>> recipes() {
		return new FirestoreQueryLiveData<>(Recipe.class,
				db.collection("recipes")
		);
	}

	public void insert(Recipe recipe, OnSuccessListener<String> onSuccessListener, OnFailureListener onFailureListener) {
		db
				.collection("recipes")
				.add(recipe)
				.addOnFailureListener(onFailureListener)
				.addOnSuccessListener(documentReference ->
						onSuccessListener.onSuccess(documentReference.getId())
				);
	}

	public void delete(Recipe recipe) {
		db
				.collection("recipes")
				.document(recipe.getId())
				.delete();
	}

	public void update(Recipe recipe, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
		if (recipe.getId() == null) {
			onFailureListener.onFailure(new IllegalArgumentException("Cannot update recipe document, Recipe has no id"));
			return;
		}

		db.collection("recipes")
				.document(recipe.getId())
				.set(recipe)
				.addOnSuccessListener(onSuccessListener)
				.addOnFailureListener(onFailureListener);
	}

}
