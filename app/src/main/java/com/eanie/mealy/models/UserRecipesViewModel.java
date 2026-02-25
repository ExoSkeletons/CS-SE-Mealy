package com.eanie.mealy.models;

import android.app.Application;
import android.widget.Toast;

import com.eanie.mealy.data.Recipe;
import com.eanie.mealy.data.RecipeRepo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

public class UserRecipesViewModel extends UserViewModel {
	private final RecipeRepo repo = new RecipeRepo();

	private final LiveData<List<Recipe>> myRecipes = Transformations.switchMap(userId, id -> {
		if (id == null || id.isEmpty()) return new MutableLiveData<>(new ArrayList<>());
		return repo.recipesOf(id);
	});

	public UserRecipesViewModel(@NonNull Application application) {
		super(application);
	}

	public LiveData<List<Recipe>> myRecipes() {
		return myRecipes;
	}

	public void add(Recipe recipe, OnSuccessListener<Recipe> onSuccess, OnFailureListener onFailure) {
		if (getUserId() == null) return;
		recipe.setChefId(getUserId());
		if (recipe.getId() != null) repo.insert(recipe,
				id -> {
					recipe.setId(id);
					onSuccess.onSuccess(recipe);
				},
				e -> {
					e.printStackTrace();
					Toast.makeText(getApplication(), "Failed to add recipe", Toast.LENGTH_SHORT).show();
					onFailure.onFailure(e);
				}
		);
		else repo.update(recipe,
				r -> onSuccess.onSuccess(recipe),
				e -> {
					e.printStackTrace();
					Toast.makeText(getApplication(), "Failed to update recipe", Toast.LENGTH_SHORT).show();
					onFailure.onFailure(e);
				}
		);
	}

	public void update(Recipe recipe, OnSuccessListener<Recipe> onSuccess, OnFailureListener onFailure) {
		repo.update(recipe,
				r -> onSuccess.onSuccess(recipe),
				e -> {
					e.printStackTrace();
					Toast.makeText(getApplication(),
							"Failed to update recipe",
							Toast.LENGTH_SHORT).show();
					onFailure.onFailure(e);
				});
	}

	public void delete(Recipe recipe) {
		repo.delete(recipe);
	}
}
