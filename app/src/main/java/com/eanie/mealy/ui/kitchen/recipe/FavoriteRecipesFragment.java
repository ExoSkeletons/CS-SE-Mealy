package com.eanie.mealy.ui.kitchen.recipe;

import com.eanie.mealy.R;

public class FavoriteRecipesFragment extends RecipeListFragment {
	@Override
	protected int getLayoutId() {
		return R.layout.fragment_favorites;
	}

	@Override
	protected int getRecyclerViewId() {
		return R.id.rv_favorites;
	}

	@Override
	protected void observeData() {
		adapter().submitList(FavoritesStore.getFavorites());
	}
}
