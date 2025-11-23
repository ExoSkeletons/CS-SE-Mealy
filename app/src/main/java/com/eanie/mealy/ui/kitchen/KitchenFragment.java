package com.eanie.mealy.ui.kitchen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eanie.mealy.R;

import java.util.ArrayList;
import java.util.List;

public class KitchenFragment extends Fragment {

	public static KitchenFragment newInstance() {
		return new KitchenFragment();
	}

	private KitchenViewModel mViewModel;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mViewModel = new ViewModelProvider(this).get(KitchenViewModel.class);
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
        kitchenItems.add(new KitchenItem("Apples", 5, R.mipmap.ic_launcher)); // Using a default drawable for now
        kitchenItems.add(new KitchenItem("Bananas", 3, R.mipmap.ic_launcher));
        kitchenItems.add(new KitchenItem("Milk", 1, R.mipmap.ic_launcher));
        kitchenItems.add(new KitchenItem("Bread", 2, R.mipmap.ic_launcher));


        KitchenAdapter adapter = new KitchenAdapter(kitchenItems);
        stock_list.setAdapter(adapter);
        stock_list.setLayoutManager(new GridLayoutManager(getContext(), 2)); // 2 columns in the grid
    }
}
