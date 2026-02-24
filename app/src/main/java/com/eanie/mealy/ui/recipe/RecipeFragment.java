package com.eanie.mealy.ui.recipe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eanie.mealy.R;
import com.eanie.mealy.data.Recipe;
import com.eanie.mealy.models.NotificationViewModel;
import com.eanie.mealy.models.SingleRecipeViewModel;
import com.eanie.mealy.models.UserItemsViewModel;
import com.eanie.mealy.models.UserRecipesViewModel;
import com.eanie.mealy.ui.kitchen.KitchenItemAdapter;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.eanie.mealy.models.UserViewModel.ARG_UUID;

public class RecipeFragment extends Fragment {
	private static final String ARG_RECIPE = "recipe";

	private UserItemsViewModel userItemsVM;
	private SingleRecipeViewModel recipeVM;
	private UserRecipesViewModel userRecipeVM;
	private NotificationViewModel notificationVM;
	private Recipe currentRecipe;

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
		userRecipeVM = provider.get(UserRecipesViewModel.class);
		notificationVM = provider.get(NotificationViewModel.class);

		var args = getArguments();
		if (args != null) {
			var recipe = (Recipe) args.getSerializable(ARG_RECIPE);
			if (recipe != null) {
				currentRecipe = recipe;
				recipeVM.set(recipe);
			}

			var chefId = args.getString(ARG_UUID, null);
			if (chefId != null) {
				userItemsVM.setUserId(chefId);
				notificationVM.setUserId(chefId);
				userRecipeVM.setUserId(chefId);
			}
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

		recipeVM.name.observe(getViewLifecycleOwner(), name ->
				((TextView) view.findViewById(R.id.tv_recipe_title)).setText(name));

		recipeVM.description.observe(getViewLifecycleOwner(), description ->
				((TextView) view.findViewById(R.id.tv_recipe_short_description)).setText(description));

		recipeVM.instructions.observe(getViewLifecycleOwner(), instructions ->
				((TextView) view.findViewById(R.id.tv_preparation)).setText(instructions));

		ImageView ivRecipe = view.findViewById(R.id.iv_recipe_photo);
		recipeVM.imagePath.observe(getViewLifecycleOwner(),
				path -> recipeVM.loadImage(path, ivRecipe));

		RecyclerView rvIngredients = view.findViewById(R.id.rv_ingredients);
		KitchenItemAdapter adapter = new KitchenItemAdapter(true);
		rvIngredients.setLayoutManager(new LinearLayoutManager(getContext()));
		rvIngredients.setAdapter(adapter);
		recipeVM.ingredients.observe(getViewLifecycleOwner(),
				adapter::submitList);

		view.findViewById(R.id.btn_make).setOnClickListener(v -> {
			try {
				Recipe recipe = recipeVM.build();
				userItemsVM.consumeFrom(recipe);
				notificationVM.sendRecipeUsed(recipe);
				Toast.makeText(getContext(),
						"Success",
						Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
				Toast.makeText(getContext(),
						"Failed to make recipe\n" + e.getMessage(),
						Toast.LENGTH_SHORT).show();
			}
		});

		var currentUserId = userRecipeVM.getUserId();
		var recipeChef = currentRecipe != null ? currentRecipe.getChefId() : null;
		if (Objects.equals(currentUserId, recipeChef)) {
			View ownerLayout = view.findViewById(R.id.layout_owner_actions);
			View btnDelete = view.findViewById(R.id.btn_delete_recipe);

			ownerLayout.setVisibility(View.VISIBLE);

			btnDelete.setOnClickListener(v -> new AlertDialog.Builder(requireContext())
					.setTitle(R.string.delete_recipe)
					.setMessage("Are you sure you want to delete this recipe?")
					.setPositiveButton(android.R.string.yes, (dialog, which) -> {
						userRecipeVM.delete(currentRecipe);
						Toast.makeText(getContext(), "Recipe deleted", Toast.LENGTH_SHORT).show();
						requireActivity().onBackPressed();
					})
					.setNegativeButton(android.R.string.cancel, null)
					.show()
			);
		View btnEdit =
				view.findViewById(R.id.btn_update_recipe);

		if (currentRecipe != null &&
				userItemsVM.getUserId() != null &&
				userItemsVM.getUserId()
						.equals(currentRecipe.getChefId())) {

			ownerLayout.setVisibility(View.VISIBLE);
			btnDelete.setOnClickListener(v ->
					new AlertDialog.Builder(requireContext())
							.setTitle("Delete Recipe")
							.setMessage("Are you sure?")
							.setPositiveButton("Delete",
									(dialog, which) -> {
										userRecipeVM.delete(currentRecipe);
										Toast.makeText(getContext(),
												"Recipe deleted",
												Toast.LENGTH_SHORT).show();
										requireActivity()
												.getSupportFragmentManager()
												.popBackStack();
									})
							.setNegativeButton("Cancel", null)
							.show()
			);

			btnEdit.setOnClickListener(v ->
					requireActivity()
							.getSupportFragmentManager()
							.beginTransaction()
							.replace(R.id.container,
									AddRecipeFragment
											.newInstance(currentRecipe))
							.addToBackStack("edit-recipe")
							.commit()
			);
		}
	}
	}
}
