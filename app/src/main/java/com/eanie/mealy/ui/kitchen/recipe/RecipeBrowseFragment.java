package com.eanie.mealy.ui.kitchen.recipe;

import android.os.Bundle;
import android.view.View;

import com.eanie.mealy.R;
import com.eanie.mealy.models.DiscoveryViewModel;
import com.eanie.mealy.models.UserItemsViewModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import static com.eanie.mealy.models.UserDataViewModel.ARG_UUID;
import static com.eanie.mealy.models.UserDataViewModel.withUserId;

public class RecipeBrowseFragment extends RecipeListFragment {
	private DiscoveryViewModel discoveryVM;
	private UserItemsViewModel userItemsVM;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		var provider = new ViewModelProvider(requireActivity());
		discoveryVM = provider.get(DiscoveryViewModel.class);
		userItemsVM = provider.get(UserItemsViewModel.class);

		if (getArguments() != null) {
			var userId = getArguments().getString(ARG_UUID, null);
			if (userId != null) userItemsVM.setUserId(userId);
		}
	}

	@Override
	protected int getLayoutId() {
		return R.layout.fragment_recipe_browse;
	}

	@Override
	public void onViewCreated(@NonNull View view,
	                          @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		View btnFavorites = view.findViewById(R.id.btn_open_favorites);
		btnFavorites.setOnClickListener(v ->
				getParentFragmentManager().beginTransaction()
						.replace(R.id.container, withUserId(userId(), new FavoriteRecipesFragment()))
						.addToBackStack("favorites")
						.commit());
	}

	@Override
	protected void observeData() {
		userItemsVM.myItems().observe(getViewLifecycleOwner(), items -> {
			if (items != null) discoveryVM.updateIngredients(items);
		});
		discoveryVM.makeableRecipes().observe(getViewLifecycleOwner(), recipes -> {
			if (recipes == null) adapter().submitList(List.of());
			else adapter().submitList(recipes);
		});
	}
}
