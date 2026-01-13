package com.eanie.mealy.data;

import com.eanie.mealy.Recipe;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

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

	public LiveData<List<Recipe>> getFavoriteRecipesOf(String userId) {
		return Transformations.switchMap(getDataOf(userId), data -> {
			if (data == null || data.getFavoriteRecipes().isEmpty())
				return new MutableLiveData<>(List.of());

			List<String> favIds = data.getFavoriteRecipes();
			if (favIds.size() > 30) favIds = favIds.subList(0, 30); // Firestore limit

			return new FirestoreQueryLiveData<>(Recipe.class,
					db
							.collection("recipes")
							.whereIn(FieldPath.documentId(), favIds)
			);
		});
	}
}
