package com.eanie.mealy.ui.kitchen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eanie.mealy.Ingredient;
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
        kitchenItems.add(new KitchenItem(new Ingredient("ing_carrot"), new Quantity(5))); // Using a default drawable for now
        kitchenItems.add(new KitchenItem(new Ingredient("ing_butter"), new Quantity(200, UnitType.GRAMS)));
        kitchenItems.add(new KitchenItem(new Ingredient("ing_eggs"), new Quantity(3)));
        kitchenItems.add(new KitchenItem(new Ingredient("ing_flour"), new Quantity(1.5, UnitType.LITERS)));
        kitchenItems.add(new KitchenItem(new Ingredient("ing_bread"), new Quantity(2000, UnitType.GRAMS)));

        KitchenItemAdapter adapter = new KitchenItemAdapter(true);
        stock_list.setAdapter(adapter);
        stock_list.setLayoutManager(new GridLayoutManager(getContext(), 2)); // 2 columns in the grid

        adapter.submitList(kitchenItems);
    }
}
