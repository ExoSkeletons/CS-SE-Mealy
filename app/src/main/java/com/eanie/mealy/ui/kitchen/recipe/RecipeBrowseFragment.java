package com.eanie.mealy.ui.kitchen.recipe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eanie.mealy.Quantity;
import com.eanie.mealy.R;
import com.eanie.mealy.Recipe;
import com.eanie.mealy.UnitType;
import com.eanie.mealy.models.UserRecipesViewModel;
import com.eanie.mealy.ui.kitchen.KitchenItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.eanie.mealy.models.UserDataViewModel.ARG_UUID;

public class RecipeBrowseFragment extends Fragment {

	private UserRecipesViewModel userRecipesVM;

	public RecipeBrowseFragment() {
	}

	public static RecipeBrowseFragment newInstance(String userId) {
		var fragment = new RecipeBrowseFragment();
		Bundle args = new Bundle();
		args.putString(ARG_UUID, userId);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		userRecipesVM = getDefaultViewModelProviderFactory().create(UserRecipesViewModel.class);

		var args = getArguments();
		if (args != null) {
			var userId = args.getString(ARG_UUID, null);
			if (userId != null) userRecipesVM.setUserId(userId);
		}
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater,
	                         @Nullable ViewGroup container,
	                         @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_recipe_browse, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view,
	                          @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		RecyclerView rvRecipes = view.findViewById(R.id.rv_recipes);

		List<Recipe> demoRecipes = createDemoRecipes();

		RecipeAdapter adapter = new RecipeAdapter(recipe -> {
			// navigate to recipe fragment
			var recipeFragment = RecipeFragment.newInstance(userRecipesVM.getUserId(), recipe);
			getParentFragmentManager().beginTransaction()
					.replace(R.id.container, recipeFragment)
					.addToBackStack("recipe-full")
					.commit();
		});
		adapter.submitList(demoRecipes);

		rvRecipes.setAdapter(adapter);
		rvRecipes.setLayoutManager(new LinearLayoutManager(getContext()));
	}

	private List<Recipe> createDemoRecipes() {
		List<Recipe> list = new ArrayList<>();
		String demoChef = "demo-chef";

        list.add(new Recipe(
                "123",
                "Pasta Bolognese",
                "Classic pasta with meat sauce.",
                emptyIngredients,
                demoChef
        ));

        list.add(new Recipe(
                "456",
                "Vegetable Stir-Fry",
                "Quick dinner with mixed vegetables.",
                emptyIngredients,
                demoChef
        ));

        list.add(new Recipe(
                "789",
                "Chocolate Cake",
                "Rich and moist chocolate cake.",
                emptyIngredients,
                demoChef
        ));

        list.add(new Recipe(
                "321",
                "Omelette",
                "Simple omelette with cheese and herbs.",
                emptyIngredients,
                demoChef
        ));

        return list;
    }
}
