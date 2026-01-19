package com.eanie.mealy.ui.kitchen;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.eanie.mealy.R;
import com.eanie.mealy.data.KitchenItem;
import com.eanie.mealy.data.Quantifier;
import com.eanie.mealy.data.Quantity;
import com.eanie.mealy.data.UnitType;
import com.eanie.mealy.models.DiscoveryViewModel;
import com.eanie.mealy.models.ItemsViewModel;
import com.eanie.mealy.models.NotificationViewModel;
import com.eanie.mealy.models.UserItemsViewModel;
import com.eanie.mealy.ui.Resources;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.eanie.mealy.models.UserViewModel.ARG_UUID;

public class KitchenFragment extends Fragment {
	private ItemsViewModel itemsVM;
	private UserItemsViewModel userItemsVM;
	private NotificationViewModel notificationVM;
	private DiscoveryViewModel discoveryVM;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// ViewModels
		var provider = new ViewModelProvider(requireActivity());
		itemsVM = provider.get(ItemsViewModel.class);
		userItemsVM = provider.get(UserItemsViewModel.class);
		notificationVM = provider.get(NotificationViewModel.class);
		discoveryVM = provider.get(DiscoveryViewModel.class);

		var args = getArguments();
		if (args != null) {
			var userId = args.getString(ARG_UUID, null);
			if (userId != null) {
				userItemsVM.setUserId(userId);
				notificationVM.setUserId(userId);
			}
		}
	}

	@Nullable
	@Override
	public View onCreateView(
			@NonNull LayoutInflater inflater,
			@Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState
	) {
		return inflater.inflate(R.layout.fragment_kitchen, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		var btnAddIngredient = view.findViewById(R.id.btn_add_ingredient);

		var user = FirebaseAuth.getInstance().getCurrentUser(); // todo: remove uuid arg pass and just call getCurrentUser()..
		if (user != null) {
			var name = user.getDisplayName();
			if (name != null) {
				var firstName = name.split(" ")[0];
				var capitalizedName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1);
				((TextView) view.findViewById(R.id.tv_username)).setText(
						requireContext().getString(R.string.kitchen_title, capitalizedName)
				);
			}
		}

		ImageButton btnNotifications = view.findViewById(R.id.btn_notifications);
		View notificationBadge = view.findViewById(R.id.notification_badge);

		notificationVM.notifications().observe(getViewLifecycleOwner(), notifications -> {
			if (notifications == null) return;

			boolean hasUnread = notifications.stream().anyMatch(n -> !n.isRead());
			notificationBadge.setVisibility(hasUnread ? View.VISIBLE : View.GONE);

			Log.d("Notifications", "Count: " + notifications.size() + ", Unread: " + hasUnread);
		});

		btnNotifications.setOnClickListener(v -> showNotificationsDialog());

		Button btnSendNotif = view.findViewById(R.id.btn_send_notif);
		btnSendNotif.setOnClickListener(v -> notificationVM.send("Test Notification ", notificationVM.getUserId()));

		RecyclerView stock_list = view.findViewById(R.id.stock_rv);
		KitchenItemAdapter adapter = new KitchenItemAdapter(
				clicked -> showEditIngredientDialog(requireContext(), clicked, userItemsVM::updateIngredient),
				new KitchenItemAdapter.OnQuantityChangeListener() {
					@Override
					public void onPlus(String ingredientKey) {
						userItemsVM.increaseAmount(ingredientKey, itemsVM.stepSize(ingredientKey));
					}

					@Override
					public void onMinus(String ingredientKey) {
						userItemsVM.increaseAmount(ingredientKey, -itemsVM.stepSize(ingredientKey));
					}
				}
		);
		adapter.setShowQuantity(true);
		adapter.setShowName(true);
		adapter.setShowIcon(true);
		adapter.setMinimalStyle(true);
		stock_list.setAdapter(adapter);
		stock_list.setLayoutManager(new GridLayoutManager(getContext(), 2));

		// open add items dialog
		btnAddIngredient.setOnClickListener(v ->
				showAddIngredientsDialog(requireContext(),
						userItemsVM.myItems().getValue(), itemsVM.ingredients().getValue(),
						userItemsVM::addIngredient, itemsVM::add
				)
		);

		userItemsVM.myItems().observe(getViewLifecycleOwner(), items -> {
			if (items == null) return;
			adapter.submitList(items);
			discoveryVM.updateIngredients(items);
		});
	}

	private void showNotificationsDialog() {
		View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_notifications, null);
		RecyclerView rv = dialogView.findViewById(R.id.rv_notifications);

		NotificationAdapter adapter = new NotificationAdapter();
		rv.setLayoutManager(new LinearLayoutManager(requireContext()));
		rv.setAdapter(adapter);

		notificationVM.notifications().observe(getViewLifecycleOwner(), adapter::submitList);

		new AlertDialog.Builder(requireContext())
				.setTitle(R.string.notifications)
				.setView(dialogView)
				.setPositiveButton(android.R.string.ok, (dialog, which) -> notificationVM.markAllAsRead())
				.setOnDismissListener(dialog -> notificationVM.markAllAsRead())
				.show();
	}

	public static void showEditIngredientDialog(Context context, KitchenItem item, Consumer<KitchenItem> onEdit) {
		ingredientDialog(context, R.string.save, item, null, onEdit).show();
	}

	private static void showCreateIngredientDialog(Context context, @Nullable List<KitchenItem> suggestions, Consumer<KitchenItem> onCreate) {
		ingredientDialog(context, R.string.create, null, suggestions, onCreate)
				// .setTitle("Create New Item")
				.setNegativeButton(android.R.string.cancel, null)
				.show();
	}

	private static AlertDialog.Builder ingredientDialog(
			Context context,
			int positiveButton,
			@Nullable KitchenItem item,
			@Nullable List<KitchenItem> suggestions,
			Consumer<KitchenItem> consumer
	) {
		boolean creatingItem = item == null;

		var layout = LayoutInflater.from(context).inflate(
				creatingItem
						? R.layout.dialog_kitchen_item_create
						: R.layout.dialog_kitchen_item_edit
				, null
		);

		var tvName = (TextView) layout.findViewById(R.id.tv_item_name);
		var tvAmount = (TextView) layout.findViewById(R.id.tv_amount);
		var spQuant = (Spinner) layout.findViewById(R.id.sp_quant);
		var spUnit = (Spinner) layout.findViewById(R.id.sp_unit_type);

		// Setup spinners
		spQuant.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, Quantifier.values()));
		spUnit.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, UnitType.values()));

		if (creatingItem) {
			// Create mode
			var itemNameHint = "";
			if (suggestions != null && !suggestions.isEmpty()) {
				var shuffled = new ArrayList<>(suggestions);
				Collections.shuffle(shuffled);
				itemNameHint = Resources.getString(context, shuffled.get(0).getIngredientKey(), "");
			}
			tvName.setHint(itemNameHint);
			tvAmount.setHint("1.0");
			spQuant.setSelection(Quantifier.NONE.ordinal());
			spUnit.setSelection(UnitType.COUNT.ordinal());
		} else {
			// Edit mode
			var imgIcon = (ImageView) layout.findViewById(R.id.img_icon);
			imgIcon.setImageDrawable(Resources.getItemIcon(context, item.getIngredientKey()));
			tvName.setText(Resources.getString(context, item.getIngredientKey(), item.getIngredientKey()));
			tvAmount.setText(String.valueOf(item.getQuantity().getAmount()));
			spQuant.setSelection(item.getQuantity().getQuantifier().ordinal());
			spUnit.setSelection(item.getQuantity().getUnitType().ordinal());
		}

		return new AlertDialog.Builder(context)
				.setView(layout)
				.setPositiveButton(positiveButton, (dialog, which) -> {
					String nameInput = tvName.getText().toString();
					String amountText = tvAmount.getText().toString();

					double amount;
					try {
						amount = Double.parseDouble(amountText);
					} catch (RuntimeException e) {
						Toast.makeText(context, "Invalid Amount", Toast.LENGTH_SHORT).show();
						return;
					}

					Quantifier quant = Quantifier.values()[spQuant.getSelectedItemPosition()];
					UnitType unitType = UnitType.values()[spUnit.getSelectedItemPosition()];

					String key = (item != null) ? item.getIngredientKey() : KitchenItem.toKey(nameInput);
					consumer.accept(new KitchenItem(key, new Quantity(amount, unitType, quant)));
				});
	}

	public static void showAddIngredientsDialog(
			Context context,
			@Nullable List<KitchenItem> existingItems, @Nullable List<KitchenItem> availableItems,
			Consumer<KitchenItem> addItem, @Nullable Consumer<KitchenItem> createItem
	) {
		if (existingItems == null) existingItems = List.of();
		var existingKeys = existingItems.stream()
				.map(KitchenItem::getIngredientKey)
				.collect(Collectors.toList());
		if (availableItems == null) availableItems = List.of();
		var newItems = availableItems.stream()
				.filter(i -> !existingKeys.contains(i.getIngredientKey()))
				.collect(Collectors.toList());

		var dialogBuilder = new AlertDialog.Builder(context)
				.setNegativeButton(android.R.string.cancel, null);

		if (!newItems.isEmpty()) {
			KitchenItemAdapter dialogAdapter = new KitchenItemAdapter();
			dialogAdapter.setShowQuantity(false);
			dialogAdapter.setShowName(true);
			dialogAdapter.setShowIcon(true);
			dialogAdapter.setMinimalStyle(false);
			dialogAdapter.setSelectionMode(true);

			View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_ingredients, null);
			RecyclerView rv = dialogView.findViewById(R.id.rv_ingredients);

			rv.setLayoutManager(new GridLayoutManager(context, 3));
			rv.setAdapter(dialogAdapter);
			dialogAdapter.submitList(newItems);

			dialogBuilder
					//.setTitle("Select ingredients to Add")
					.setView(dialogView)
					.setPositiveButton(R.string.add, (dialog, which) ->
							newItems.stream()
									.filter(i -> dialogAdapter.getSelectedKeys().contains(i.getIngredientKey()))
									.forEach(addItem)
					);
		} else {
			dialogBuilder.setTitle("No ingredients left to buy")
					.setMessage("You have everything!")
					.setPositiveButton(android.R.string.ok, null);
		}

		if (createItem != null) {
			List<KitchenItem> existing = existingItems;
			dialogBuilder.setNeutralButton(R.string.create,
					(dialog, which) -> showCreateIngredientDialog(
							context, existing,
							item -> {
								createItem.accept(item);
								addItem.accept(item);
							}
					)
			);
		}

		dialogBuilder.show();
	}
}
