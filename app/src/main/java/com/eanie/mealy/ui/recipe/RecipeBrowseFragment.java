package com.eanie.mealy.ui.recipe;

import static com.eanie.mealy.models.UserViewModel.ARG_UUID;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.eanie.mealy.R;
import com.eanie.mealy.models.DiscoveryViewModel;
import com.eanie.mealy.models.UserItemsViewModel;

import java.util.List;

public class RecipeBrowseFragment extends RecipeListFragment {
	private DiscoveryViewModel discoveryVM;
	private UserItemsViewModel userItemsVM;
    private List<com.eanie.mealy.data.KitchenItem> lastItems = List.of();
    private List<com.eanie.mealy.data.Recipe> lastRecipes = List.of();


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
	}

	@Override
    protected void observeData() {
        userItemsVM.myItems().observe(getViewLifecycleOwner(), items -> {
            lastItems = (items != null) ? items : List.of();
            discoveryVM.updateIngredients(lastItems);

            adapter().submitUserItems(lastItems);

            adapter().submitList(lastRecipes);
        });

        discoveryVM.allRecipes().observe(getViewLifecycleOwner(), recipes -> {
            if (recipes == null) adapter().submitList(List.of());
            else adapter().submitList(recipes);
        });

    }

}
