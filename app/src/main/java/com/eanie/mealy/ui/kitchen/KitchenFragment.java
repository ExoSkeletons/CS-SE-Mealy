package com.eanie.mealy.ui.kitchen;

import static com.eanie.mealy.models.UserDataViewModel.ARG_UUID;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eanie.mealy.Quantity;
import com.eanie.mealy.R;
import com.eanie.mealy.UnitType;
import com.eanie.mealy.models.UserItemsViewModel;

import java.util.ArrayList;
import java.util.List;

public class KitchenFragment extends Fragment {

	public static KitchenFragment newInstance(String userId) {
		var fragment = new KitchenFragment();
		Bundle args = new Bundle();
		args.putString(ARG_UUID, userId);
		fragment.setArguments(args);
		return fragment;
    }

	private UserItemsViewModel mViewModel;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mViewModel = getDefaultViewModelProviderFactory().create(UserItemsViewModel.class);

		var args = getArguments();
		if (args != null) {
			var userId = args.getString(ARG_UUID, null);
			if (userId != null) mViewModel.setUserId(userId);
		}
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
	    kitchenItems.add(new KitchenItem("ing_apple", new Quantity(5)));
		kitchenItems.add(new KitchenItem("ing_cheese", new Quantity(200, UnitType.GRAMS)));
		kitchenItems.add(new KitchenItem("ing_cucumber", new Quantity(3)));
		kitchenItems.add(new KitchenItem("ing_milk", new Quantity(1.5, UnitType.LITERS)));
		kitchenItems.add(new KitchenItem("ing_bread", new Quantity(2, UnitType.KILOGRAMS)));

        KitchenItemAdapter adapter = new KitchenItemAdapter(true);
        stock_list.setAdapter(adapter);
        stock_list.setLayoutManager(new GridLayoutManager(getContext(), 2)); // 2 columns in the grid

        adapter.submitList(kitchenItems);

		view.findViewById(R.id.imageButton).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Add clicked", Toast.LENGTH_SHORT).show();
		});

		// Get items live with -
		mViewModel.myItems().observe(getViewLifecycleOwner(), items -> {
            if (items == null) return;

            kitchenItems.clear();
            if (!items.isEmpty())
                kitchenItems.addAll(items);
			adapter.notifyDataSetChanged();
		});
	}
}
