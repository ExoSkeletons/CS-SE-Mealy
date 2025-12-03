package com.eanie.mealy.ui.kitchen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eanie.mealy.Quantity;
import com.eanie.mealy.R;
import com.eanie.mealy.UnitType;
import com.eanie.mealy.models.ItemsViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class KitchenFragment extends Fragment {

	public static KitchenFragment newInstance() {
		return new KitchenFragment();
	}

	private ItemsViewModel mViewModel;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mViewModel = new ViewModelProvider(this).get(ItemsViewModel.class);
		// TODO: Use the ViewModel
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
	                         @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_kitchen, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		RecyclerView stock_list = view.findViewById(R.id.stock_rv);

		// Create sample data
		List<KitchenItem> kitchenItems = new ArrayList<>();
		kitchenItems.add(new KitchenItem("ing_apple", new Quantity(5))); // Using a default drawable for now
		kitchenItems.add(new KitchenItem("ing_cheese", new Quantity(200, UnitType.GRAMS)));
		kitchenItems.add(new KitchenItem("ing_cucumber", new Quantity(3)));
		kitchenItems.add(new KitchenItem("ing_milk", new Quantity(1.5, UnitType.LITERS)));
		kitchenItems.add(new KitchenItem("ing_bread", new Quantity(2000, UnitType.GRAMS)));

		// This user id is tempory (it is just my fake user) -Aviad
		mViewModel.setUserId("lvwuK3xBNufRynvXdB8XRqirziu2");
		view.findViewById(R.id.imageButton).setOnClickListener(v -> {
			for (KitchenItem item : kitchenItems)
				mViewModel.addIngredient(item);
		});
		// Get items live with -
		mViewModel.myItems().observe(getViewLifecycleOwner(), items -> {

		});

		// TODO: switch to use RecyclerView ListAdapter!!!! for kitchen adapter
		KitchenAdapter adapter = new KitchenAdapter(kitchenItems);
		stock_list.setAdapter(adapter);
		stock_list.setLayoutManager(new GridLayoutManager(getContext(), 2)); // 2 columns in the grid
	}
}
