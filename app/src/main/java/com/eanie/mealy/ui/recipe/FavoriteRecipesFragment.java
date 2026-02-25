package com.eanie.mealy.ui.recipe;

import com.eanie.mealy.R;

public class FavoriteRecipesFragment extends RecipeListFragment {
	@Override
	protected int getLayoutId() {
		return R.layout.fragment_recipes_favourites;
	}

	@Override
	protected int getRecyclerViewId() {
		return R.id.rv_favorites;
	}

	@Override
	protected void observeData() {
		favoriteRecipes().observe(getViewLifecycleOwner(), recipes -> {
			if (recipes == null) return;
			adapter().submitList(recipes);
		});
	}
}
