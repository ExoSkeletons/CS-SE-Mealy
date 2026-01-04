package com.eanie.mealy.ui.kitchen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eanie.mealy.Quantity;
import com.eanie.mealy.R;
import com.eanie.mealy.UnitType;
import com.eanie.mealy.models.DiscoveryViewModel;
import com.eanie.mealy.models.UserItemsViewModel;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.eanie.mealy.models.UserDataViewModel.ARG_UUID;

public class KitchenFragment extends Fragment {

	private UserItemsViewModel userItemsVM;
	private DiscoveryViewModel discoveryVM;

	public static KitchenFragment newInstance(String userId) {
		var fragment = new KitchenFragment();
		Bundle args = new Bundle();
		args.putString(ARG_UUID, userId);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// ViewModels
		userItemsVM = getDefaultViewModelProviderFactory().create(UserItemsViewModel.class);
		discoveryVM = getDefaultViewModelProviderFactory().create(DiscoveryViewModel.class);

		var args = getArguments();
		if (args != null) {
			var userId = args.getString(ARG_UUID, null);
			if (userId != null) userItemsVM.setUserId(userId);
		}
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater,
	                         @Nullable ViewGroup container,
	                         @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_kitchen, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		RecyclerView stock_list = view.findViewById(R.id.stock_rv);

		KitchenItemAdapter adapter = new KitchenItemAdapter(
				true,
				new KitchenItemAdapter.OnQuantityChangeListener() {
					@Override
					public void onPlus(String ingredientKey) {
						userItemsVM.plusAmount(ingredientKey);
					}

					@Override
					public void onMinus(String ingredientKey) {
						userItemsVM.minusAmount(ingredientKey);
					}
				}
		);
		stock_list.setAdapter(adapter);
		stock_list.setLayoutManager(new GridLayoutManager(getContext(), 2));

		// demo
		var demoItems = List.of(
				new KitchenItem("ing_apple", new Quantity(5)),
				new KitchenItem("ing_cheese", new Quantity(200, UnitType.GRAMS)),
				new KitchenItem("ing_cucumber", new Quantity(3)),
				new KitchenItem("ing_milk", new Quantity(1.5, UnitType.LITERS)),
				new KitchenItem("ing_bread", new Quantity(2, UnitType.KILOGRAMS))
		);
		adapter.submitList(demoItems);
		discoveryVM.updateIngredients(demoItems);

		// open add items dialog
		view.findViewById(R.id.imageButton).setOnClickListener(v -> showAddIngredientsDialog());

		userItemsVM.myItems().observe(getViewLifecycleOwner(), items -> {
			if (items == null) return;
			adapter.submitList(items);
			discoveryVM.updateIngredients(items);
		});
	}

	private void showAddIngredientsDialog() {
		var mItems = userItemsVM.myItems().getValue();
		var items = Stream.of(
						"ing_apple",
						"ing_bread",
						"ing_butter",
						"ing_cheese",
						"ing_cucumber",
						"ing_eggs",
						"ing_flour",
						"ing_milk",
						"ing_mushrooms",
						"ing_onion",
						"ing_tomato",
						"ing_yogurt"
				).map(k -> new KitchenItem(k, new Quantity(userItemsVM.stepSize(k))))
				.filter(i -> mItems == null || !mItems.contains(i))
				.toList();

		boolean[] checked = new boolean[items.size()];

		new AlertDialog.Builder(requireContext())
				.setTitle("Choose ingredients")
				.setMultiChoiceItems(
						items.stream()
								.map(KitchenItem::getIngredientKey)
								.map(k -> Resources.getString(requireContext(), k, k)) // get name translation
								.toArray(String[]::new),
						checked,
						(dialog, which, isChecked) -> checked[which] = isChecked
				)
				.setPositiveButton("Add", (dialog, which) ->
						IntStream.range(0, items.size())
								.filter(i -> checked[i])
								.forEachOrdered(i -> userItemsVM.addIngredient(items.get(i))))
				.setNegativeButton("Cancel", null)
				.show();
	}
}
