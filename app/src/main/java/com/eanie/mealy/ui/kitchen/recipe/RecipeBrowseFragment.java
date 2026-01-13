package com.eanie.mealy.ui.kitchen.recipe;

import android.os.Bundle;
import android.view.View;

import com.eanie.mealy.R;
import com.eanie.mealy.models.DiscoveryViewModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import static com.eanie.mealy.models.UserDataViewModel.withUserId;

public class RecipeBrowseFragment extends RecipeListFragment {
	private DiscoveryViewModel discoveryVM;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		discoveryVM = getDefaultViewModelProviderFactory().create(DiscoveryViewModel.class);

		var provider = new ViewModelProvider(requireActivity());
		discoveryVM = provider.get(DiscoveryViewModel.class);
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
		discoveryVM.makeableRecipes().observe(getViewLifecycleOwner(), recipes -> {
			if (recipes == null) adapter().submitList(List.of());
			else adapter().submitList(recipes);
		});
	}
}
