package com.eanie.mealy.ui.kitchen.recipe;

import android.os.Bundle;

import com.eanie.mealy.R;
import com.eanie.mealy.models.UserRecipesViewModel;

import androidx.annotation.Nullable;

public class MyRecipesFragment extends RecipeListFragment {
	UserRecipesViewModel userRecipesVM;

	public MyRecipesFragment() {
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userRecipesVM = getDefaultViewModelProviderFactory().create(UserRecipesViewModel.class);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.fragment_my_recipes;
	}

	@Override
	protected int getRecyclerViewId() {
		return R.id.rv_my_recipes;
	}

	@Override
	protected void observeData() {
		userRecipesVM.myRecipes().observe(getViewLifecycleOwner(), adapter()::submitList);
	}
}
