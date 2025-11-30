package com.eanie.mealy.data;

import com.eanie.mealy.Recipe;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import androidx.lifecycle.LiveData;

public class RecipeRepo {
	FirebaseFirestore db = FirebaseFirestore.getInstance();

	public LiveData<List<Recipe>> recipesOf(String userId) {
		return new FirestoreQueryLiveData<>(Recipe.class,
				db
						.collection("recipes")
						.whereEqualTo("userId", userId)
		);
	}

	public LiveData<List<Recipe>> recipes() {
		return new FirestoreQueryLiveData<>(Recipe.class,
				db.collection("recipes")
		);
	}

	public void addRecipe(Recipe recipe) {
		db.collection("recipes").add(recipe);
	}
}
