package com.eanie.mealy.ui.kitchen.recipe;

import android.os.Bundle;

import com.eanie.mealy.R;
import com.eanie.mealy.models.UserRecipesViewModel;

import androidx.annotation.Nullable;

import static com.eanie.mealy.models.UserDataViewModel.ARG_UUID;

public class MyRecipesFragment extends RecipeListFragment {
	UserRecipesViewModel userRecipesVM;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userRecipesVM = getDefaultViewModelProviderFactory().create(UserRecipesViewModel.class);
		if (getArguments() != null) {
			var userId = getArguments().getString(ARG_UUID);
			userRecipesVM.setUserId(userId);
		}
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
