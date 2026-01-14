package com.eanie.mealy.ui.kitchen;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.eanie.mealy.Quantifier;
import com.eanie.mealy.Quantity;
import com.eanie.mealy.R;
import com.eanie.mealy.UnitType;
import com.eanie.mealy.models.DiscoveryViewModel;
import com.eanie.mealy.models.UserInfoViewModel;
import com.eanie.mealy.models.UserItemsViewModel;
import com.eanie.mealy.ui.kitchen.recipe.MyRecipesFragment;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.eanie.mealy.models.UserDataViewModel.ARG_UUID;
import static com.eanie.mealy.models.UserDataViewModel.withUserId;

public class KitchenFragment extends Fragment {
	private UserItemsViewModel userItemsVM;
	private UserInfoViewModel userInfoVM;
	private DiscoveryViewModel discoveryVM;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// ViewModels
		var provider = new ViewModelProvider(requireActivity());
		userItemsVM = provider.get(UserItemsViewModel.class);
		discoveryVM = provider.get(DiscoveryViewModel.class);
		userInfoVM = provider.get(UserInfoViewModel.class);

		var args = getArguments();
		if (args != null) {
			var userId = args.getString(ARG_UUID, null);
			if (userId != null) {
				userItemsVM.setUserId(userId);
				userInfoVM.setUserId(userId);
			}
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
		View myRecipesBtn = view.findViewById(R.id.btn_my_recipes);
		userInfoVM.isChef().observe(getViewLifecycleOwner(), isChef ->
				myRecipesBtn.setVisibility(isChef ? View.VISIBLE : View.GONE)
		);
		myRecipesBtn.setOnClickListener(v ->
				getParentFragmentManager().beginTransaction()
						.replace(R.id.container, withUserId(userItemsVM.getUserId(), new MyRecipesFragment()))
						.addToBackStack("my-recipes")
						.commit());

		var user = FirebaseAuth.getInstance().getCurrentUser(); // todo: remove uuid arg pass and just call getCurrentUser()..
		if (user != null) {
			var name = user.getDisplayName();
			if (name != null) {
				var firstName = name.split(" ")[0];
				var capitalizedName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1);
				((TextView) view.findViewById(R.id.tv_username)).setText(
						requireContext().getString(R.string.s_kitchen_title, capitalizedName)
				);
			}
		}
		KitchenItemAdapter adapter = new KitchenItemAdapter(
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
		adapter.setShowQuantity(true);
		adapter.setShowName(true);
		adapter.setShowIcon(true);
		adapter.setMinimalStyle(true);
		RecyclerView stock_list = view.findViewById(R.id.stock_rv);
		stock_list.setAdapter(adapter);
		stock_list.setLayoutManager(new GridLayoutManager(getContext(), 2));

		// open add items dialog
		view.findViewById(R.id.imageButton).setOnClickListener(v -> showAddIngredientsDialog());

		userItemsVM.myItems().observe(getViewLifecycleOwner(), items -> {
			if (items == null) return;
			adapter.submitList(items);
			discoveryVM.updateIngredients(items);
		});
	}

	private static void showAddNewIngredientDialog(Context context, @Nullable List<KitchenItem> mItems, Consumer<KitchenItem> consumer) {
		var iv = LayoutInflater.from(context).inflate(R.layout.dialog_kitchen_item_create, null);

		var tvName = (TextView) iv.findViewById(R.id.tv_item_name);
		var tvAmount = (TextView) iv.findViewById(R.id.tv_amount);
		var spQuant = (Spinner) iv.findViewById(R.id.sp_quant);
		var spUnit = (Spinner) iv.findViewById(R.id.sp_unit_type);

		var itemName = "";
		if (mItems != null && !mItems.isEmpty()) {
			var shuffled = new ArrayList<>(mItems);
			Collections.shuffle(shuffled);
			itemName = Resources.getString(context, shuffled.get(0).getIngredientKey(), itemName);
		}
		tvName.setHint(itemName);

		tvAmount.setHint("" + 1.0);

		var quants = Quantifier.values();
		var qAdapter = new ArrayAdapter<>(
				context,
				android.R.layout.simple_spinner_dropdown_item,
				quants
		);
		spQuant.setAdapter(qAdapter);
		spQuant.setSelection(Quantifier.NONE.ordinal());

		var units = UnitType.values();
		var uAdapter = new ArrayAdapter<>(
				context,
				android.R.layout.simple_spinner_dropdown_item,
				units
		);
		spUnit.setAdapter(uAdapter);
		spUnit.setSelection(UnitType.COUNT.ordinal());

		new AlertDialog.Builder(context)
				.setTitle("Add new ingredient")
				.setView(iv)
				.setPositiveButton("Add", (dialog, which) -> {
					String name = tvName.getText().toString();
					String amountText = tvAmount.getText().toString();
					int quantPosition = spQuant.getSelectedItemPosition();
					int unitPosition = spUnit.getSelectedItemPosition();

					String key = KitchenItem.toKey(name);
					double amount;
					Quantifier quant;
					UnitType unitType;
					try {
						amount = Double.parseDouble(amountText);
					} catch (RuntimeException e) {
						Toast.makeText(context, "Invalid Amount", Toast.LENGTH_SHORT).show();
						e.printStackTrace();
						return;
					}
					try {
						unitType = UnitType.values()[unitPosition];
					} catch (RuntimeException e) {
						Toast.makeText(context, "Invalid Unit Type", Toast.LENGTH_SHORT).show();
						unitType = UnitType.COUNT;
					}
					try {
						quant = Quantifier.values()[quantPosition];
					} catch (RuntimeException e) {
						Toast.makeText(context, "Invalid Quantifier", Toast.LENGTH_SHORT).show();
						quant = Quantifier.NONE;
					}

					consumer.accept(new KitchenItem(key, new Quantity(amount, unitType, quant)));
				})
				.setNegativeButton(android.R.string.cancel, null)
				.show();
	}

	private void showAddIngredientsDialog() {
		var mItems = userItemsVM.myItems().getValue();
		var mIds = mItems == null ? null
				: mItems.stream()
				.map(KitchenItem::getIngredientKey)
				.collect(Collectors.toList());
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
				).map(k -> userItemsVM.buy(k))
				.filter(newItem -> mIds == null || !mIds.contains(newItem.getIngredientKey()))
				.collect(Collectors.toList());

		if (items.isEmpty()) {
			new AlertDialog.Builder(requireContext())
					.setTitle("No ingredients left to buy")
					.setMessage("You have everything!")
					.setPositiveButton("Ok", null)
					.show();
			return;
		}

		KitchenItemAdapter dialogAdapter = new KitchenItemAdapter();
		dialogAdapter.setShowQuantity(false);
		dialogAdapter.setShowName(true);
		dialogAdapter.setShowIcon(true);
		dialogAdapter.setMinimalStyle(false);
		dialogAdapter.setSelectionMode(true);
		RecyclerView rv = new RecyclerView(requireContext());
		rv.setLayoutManager(new GridLayoutManager(requireContext(), 3));
		rv.setAdapter(dialogAdapter);
		dialogAdapter.submitList(items);

		new AlertDialog.Builder(requireContext())
				.setTitle("Select ingredients to Add")
				.setView(rv)
				.setPositiveButton("Add Selected", (dialog, which) ->
						items.stream()
								.filter(i -> dialogAdapter.getSelectedKeys().contains(i.getIngredientKey()))
								.forEach(userItemsVM::addIngredient))
				.setNeutralButton("Create New Item", (dialog, which) ->
						showAddNewIngredientDialog(requireContext(), mItems, userItemsVM::addIngredient)
				)
				.setNegativeButton(android.R.string.cancel, null)
				.show();
	}
}
