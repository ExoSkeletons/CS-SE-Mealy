package com.eanie.mealy.ui.recipe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eanie.mealy.R;
import com.eanie.mealy.models.UserDataViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.function.Supplier;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import static com.eanie.mealy.models.UserViewModel.ARG_UUID;
import static com.eanie.mealy.models.UserViewModel.withUserId;

public class RecipeNavFragment extends Fragment {
	private String uuid;
	private UserDataViewModel userInfoVM;

	private enum RecipeTab {
		BROWSE(R.string.available_recipes, RecipeBrowseFragment::new),
		MY_RECIPES(R.string.my_recipes, MyRecipesFragment::new),
		FAVORITES(R.string.favorites, FavoriteRecipesFragment::new);

		@StringRes
		final int titleRes;
		final Supplier<Fragment> fragmentSupplier;

		RecipeTab(@StringRes int titleRes, Supplier<Fragment> fragmentSupplier) {
			this.titleRes = titleRes;
			this.fragmentSupplier = fragmentSupplier;
		}
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userInfoVM = new ViewModelProvider(requireActivity()).get(UserDataViewModel.class);
		if (getArguments() != null) {
			uuid = getArguments().getString(ARG_UUID);
			if (uuid != null) {
				userInfoVM.setUserId(uuid);
			}
		}
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_recipes_nav, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		ViewPager2 viewPager = view.findViewById(R.id.view_pager);
		TabLayout tabLayout = view.findViewById(R.id.tab_layout);
		View btnAddRecipe = view.findViewById(R.id.btn_add_recipe);

		viewPager.setAdapter(new RecipePagerAdapter(this, uuid));

		new TabLayoutMediator(tabLayout, viewPager, (tab, position) ->
				tab.setText(RecipeTab.values()[position].titleRes)
		).attach();

		userInfoVM.isChef().observe(getViewLifecycleOwner(), isChef -> {
			btnAddRecipe.setVisibility(isChef ? View.VISIBLE : View.GONE);
		});

		btnAddRecipe.setOnClickListener(v ->
				getParentFragmentManager().beginTransaction()
						.replace(R.id.container, AddRecipeFragment.newInstance())
						.addToBackStack("add-recipe")
						.commit());
	}

	private static class RecipePagerAdapter extends FragmentStateAdapter {
		private final String uuid;

		public RecipePagerAdapter(@NonNull Fragment fragment, String uuid) {
			super(fragment);
			this.uuid = uuid;
		}

		@NonNull
		@Override
		public Fragment createFragment(int position) {
			RecipeTab tab = RecipeTab.values()[position];
			return withUserId(uuid, tab.fragmentSupplier.get());
		}

		@Override
		public int getItemCount() {
			return RecipeTab.values().length;
		}
	}
}
