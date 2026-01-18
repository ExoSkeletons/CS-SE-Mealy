package com.eanie.mealy.ui.recipe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.eanie.mealy.R;
import com.eanie.mealy.data.Recipe;
import com.eanie.mealy.models.SingleRecipeViewModel;
import com.eanie.mealy.models.UserItemsViewModel;
import com.eanie.mealy.ui.kitchen.KitchenItemAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.eanie.mealy.models.UserViewModel.ARG_UUID;


public class RecipeFragment extends Fragment {
	private static final String ARG_RECIPE = "recipe";

	private UserItemsViewModel userItemsVM;
	private SingleRecipeViewModel recipeVM;

	public static RecipeFragment newInstance(Recipe recipe) {
		RecipeFragment fragment = new RecipeFragment();
		Bundle args = new Bundle();
		args.putSerializable(ARG_RECIPE, recipe);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		var provider = new ViewModelProvider(requireActivity());
		userItemsVM = provider.get(UserItemsViewModel.class);
		recipeVM = provider.get(SingleRecipeViewModel.class);

		var args = getArguments();
		if (args != null) {
			var recipe = (Recipe) args.getSerializable(ARG_RECIPE);
			if (recipe != null) recipeVM.set(recipe);

			var chefId = args.getString(ARG_UUID, null);
			if (chefId != null) userItemsVM.setUserId(chefId);
		}
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater,
	                         @Nullable ViewGroup container,
	                         @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_recipe, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view,
	                          @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		userItemsVM.myItems().observe(getViewLifecycleOwner(), items -> {
		});

		recipeVM.name.observe(getViewLifecycleOwner(), name -> ((TextView) view.findViewById(R.id.tv_recipe_title)).setText(name));
		recipeVM.description.observe(getViewLifecycleOwner(), description -> ((TextView) view.findViewById(R.id.tv_recipe_short_description)).setText(description));
		recipeVM.instructions.observe(getViewLifecycleOwner(), instructions -> ((TextView) view.findViewById(R.id.tv_preparation)).setText(instructions));

		RecyclerView rvIngredients = view.findViewById(R.id.rv_ingredients);
		KitchenItemAdapter adapter = new KitchenItemAdapter();
		rvIngredients.setAdapter(adapter);
		rvIngredients.setLayoutManager(new LinearLayoutManager(getContext()));
		recipeVM.ingredients.observe(getViewLifecycleOwner(), adapter::submitList);

		view.findViewById(R.id.btn_make).setOnClickListener(v -> {
			try {
				userItemsVM.consumeFrom(recipeVM.build());
				Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
				Toast.makeText(getContext(), "Failed to make recipe\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
			}
		});
	}
}
