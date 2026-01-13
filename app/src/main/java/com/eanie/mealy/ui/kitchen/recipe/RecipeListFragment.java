package com.eanie.mealy.ui.kitchen.recipe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eanie.mealy.Quantity;
import com.eanie.mealy.R;
import com.eanie.mealy.Recipe;
import com.eanie.mealy.UnitType;
import com.eanie.mealy.ui.kitchen.KitchenItem;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.eanie.mealy.models.UserDataViewModel.ARG_UUID;

public abstract class RecipeListFragment extends Fragment {
	private RecipeAdapter adapter;
	private String userId;

	protected String userId() {
		return userId;
	}

	protected RecipeAdapter adapter() {
		return adapter;
	}

	protected abstract int getLayoutId();

	protected int getRecyclerViewId() {
		return R.id.rv_recipes;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) userId = getArguments().getString(ARG_UUID);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(getLayoutId(), container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		RecyclerView rv = view.findViewById(getRecyclerViewId());
		adapter = new RecipeAdapter(recipe -> {
			getParentFragmentManager().beginTransaction()
					.replace(R.id.container, RecipeFragment.newInstance(userId, recipe))
					.addToBackStack("recipe-full")
					.commit();
		});
		rv.setLayoutManager(new LinearLayoutManager(getContext()));
		rv.setAdapter(adapter);

		adapter.submitList(demoRecipes());

		observeData();
	}

	protected abstract void observeData();


	private static List<Recipe> demoRecipes() {
		String demoChef = "demo-chef";
		return List.of(
				new Recipe(
						"123",
						"Pasta Bolognese",
						"Classic pasta with red sauce.",
						List.of(
								new KitchenItem("ing_pasta", new Quantity(200, UnitType.GRAMS)),
								new KitchenItem("ing_tomato", new Quantity(.10, UnitType.LITERS)),
								new KitchenItem("ing_onion", new Quantity(3))
						),
						demoChef
				),
				new Recipe(
						"456",
						"Vegetable Stir-Fry",
						"Quick dinner with mixed vegetables.",
						List.of(),
						demoChef
				),
				new Recipe(
						"789",
						"Chocolate Cake",
						"Rich and moist chocolate cake.",
						List.of(
								new KitchenItem("ing_flour", new Quantity(500, UnitType.GRAMS)),
								new KitchenItem("ing_sugar", new Quantity(50, UnitType.GRAMS)),
								new KitchenItem("ing_eggs", new Quantity(3)),
								new KitchenItem("ing_butter", new Quantity(50, UnitType.GRAMS)),
								new KitchenItem("ing_chocolate", new Quantity(200, UnitType.GRAMS)),
								new KitchenItem("ing_vanilla", new Quantity(0.5, UnitType.TABLE_SPOONS)),
								new KitchenItem("ing_baking_soda", new Quantity(2, UnitType.TABLE_SPOONS)),
								new KitchenItem("ing_salt", new Quantity(UnitType.TABLE_SPOONS)),
								new KitchenItem("ing_milk", new Quantity(.10, UnitType.LITERS))
						),
						demoChef
				),
				new Recipe(
						"321",
						"Omelette",
						"Simple omelette with cheese and herbs.",
						List.of(
								new KitchenItem("ing_eggs", new Quantity(3)),
								new KitchenItem("ing_cheese", new Quantity(200, UnitType.GRAMS)),
								new KitchenItem("ing_salt", new Quantity(2, UnitType.TABLE_SPOONS)),
								new KitchenItem("ing_pepper", new Quantity(UnitType.TABLE_SPOONS)),
								new KitchenItem("ing_oil", new Quantity(10, UnitType.GRAMS)),
								new KitchenItem("ing_onion", new Quantity(1)),
								new KitchenItem("ing_garlic", new Quantity(1)),
								new KitchenItem("ing_mushroom", new Quantity(200, UnitType.GRAMS))
						),
						demoChef
				));
	}
}