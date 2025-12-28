package com.eanie.mealy.models;

import com.eanie.mealy.Recipe;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class SingleRecipeViewModel extends ViewModel {
	private LiveData<Recipe> recipe;
}
