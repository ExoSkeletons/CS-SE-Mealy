package com.eanie.mealy.ui.kitchen;

import static com.eanie.mealy.models.UserDataViewModel.ARG_UUID;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eanie.mealy.Quantity;
import com.eanie.mealy.R;
import com.eanie.mealy.UnitType;
import com.eanie.mealy.models.DiscoveryViewModel;
import com.eanie.mealy.models.UserItemsViewModel;

import java.util.ArrayList;
import java.util.List;

public class KitchenFragment extends Fragment {

    private UserItemsViewModel mViewModel;
    private DiscoveryViewModel discoveryVM;

    private KitchenItemAdapter adapter;
    private final List<KitchenItem> kitchenItems = new ArrayList<>();

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
        mViewModel = new ViewModelProvider(this).get(UserItemsViewModel.class);
        discoveryVM = new ViewModelProvider(requireActivity()).get(DiscoveryViewModel.class);

        // קבלת userId מה-Arguments
        var args = getArguments();
        if (args != null) {
            var userId = args.getString(ARG_UUID, null);
            if (userId != null) mViewModel.setUserId(userId);
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

        // 🔹 יוצרים Adapter עם listener ל-+ ו- -
        if (adapter == null) {
            adapter = new KitchenItemAdapter(
                    true,
                    new KitchenItemAdapter.OnQuantityChangeListener() {
                        @Override
                        public void onPlus(String ingredientKey) {
                            mViewModel.plusAmount(ingredientKey);
                        }

                        @Override
                        public void onMinus(String ingredientKey) {
                            mViewModel.minusAmount(ingredientKey);
                        }
                    }
            );
        }

        stock_list.setAdapter(adapter);
        stock_list.setLayoutManager(new GridLayoutManager(getContext(), 2)); // 2 עמודות

        // 🔹 נתוני דמו התחלתיים — רק אם אין כלום (עד שיגיעו נתונים מה-ViewModel)
        if (kitchenItems.isEmpty()) {
            kitchenItems.add(new KitchenItem("ing_apple",    new Quantity(5)));
            kitchenItems.add(new KitchenItem("ing_cheese",   new Quantity(200, UnitType.GRAMS)));
            kitchenItems.add(new KitchenItem("ing_cucumber", new Quantity(3)));
            kitchenItems.add(new KitchenItem("ing_milk",     new Quantity(1.5, UnitType.LITERS)));
            kitchenItems.add(new KitchenItem("ing_bread",    new Quantity(2, UnitType.KILOGRAMS)));
        }

        adapter.submitList(new ArrayList<>(kitchenItems));
        discoveryVM.updateIngredients(new ArrayList<>(kitchenItems));

        // 🔹 כפתור הפלוס – פותח דיאלוג בחירת מצרכים
        view.findViewById(R.id.imageButton).setOnClickListener(v -> {
            showAddIngredientsDialog();
        });

        // 🔹 מאזינים לשינויים במלאי מתוך ה-ViewModel
        mViewModel.myItems().observe(getViewLifecycleOwner(), items -> {
            if (items == null) return;

            kitchenItems.clear();
            if (!items.isEmpty()) {
                kitchenItems.addAll(items);
            }

            // מעדכן את הרשימה על המסך
            adapter.submitList(new ArrayList<>(kitchenItems));

            // מעדכן גם את Discovery (כדי שהמתכונים האפשריים יתאימו למה שיש במטבח)
            discoveryVM.updateIngredients(new ArrayList<>(kitchenItems));
        });
    }

    // 🔹 דיאלוג בחירת מצרכים להוספה
    private void showAddIngredientsDialog() {
        String[] ingredientNames = {
                "Apple",
                "Bread",
                "Butter",
                "Cheese",
                "Cucumber",
                "Eggs",
                "Flour",
                "Milk",
                "Mushrooms",
                "Onion",
                "Tomato",
                "Yogurt"
        };

        String[] ingredientKeys = {
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
        };

        boolean[] checked = new boolean[ingredientNames.length];

        new AlertDialog.Builder(requireContext())
                .setTitle("Choose ingredients")
                .setMultiChoiceItems(ingredientNames, checked, (dialog, which, isChecked) -> {
                    checked[which] = isChecked;
                })
                .setPositiveButton("Add", (dialog, which) -> {
                    for (int i = 0; i < ingredientKeys.length; i++) {
                        if (!checked[i]) continue;   // רק מה שסומן

                        String key = ingredientKeys[i];

                        // בודקים אם כבר קיים כזה פריט ברשימה
                        boolean exists = false;
                        for (KitchenItem item : kitchenItems) {
                            if (item.getIngredientKey().equals(key)) {
                                exists = true;
                                break;
                            }
                        }
                        if (exists) continue;

                        Quantity defaultQty = new Quantity(1);
                        KitchenItem newItem = new KitchenItem(key, defaultQty);

                        // 🔹 מוסיפים דרך ה-ViewModel (שישמור ויעדכן LiveData)
                        mViewModel.addIngredient(newItem);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
