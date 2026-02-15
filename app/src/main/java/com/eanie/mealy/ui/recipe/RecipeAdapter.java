package com.eanie.mealy.ui.recipe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.eanie.mealy.R;
import com.eanie.mealy.data.IngredientStatus;
import com.eanie.mealy.data.ItemKeyCallback;
import com.eanie.mealy.data.KitchenItem;
import com.eanie.mealy.data.Recipe;
import com.eanie.mealy.ui.kitchen.KitchenItemAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RecipeAdapter extends ListAdapter<Recipe, RecipeAdapter.RecipeItemViewHolder> {
	public interface OnRecipeClickListener {
		void onRecipeClick(Recipe recipe);
	}

	public interface OnFavoriteClickListener {
		void onFavorited(Recipe recipe, boolean isFavorite);
	}

	private final OnRecipeClickListener clickListener;
	private final OnFavoriteClickListener favoriteListener;

	private final List<String> favorites = new ArrayList<>();
	private boolean showFavored = false;
    private List<KitchenItem> userItems = List.of();


    public RecipeAdapter(OnRecipeClickListener clickListener, OnFavoriteClickListener favoriteListener) {
		super(new ItemKeyCallback<>(Recipe::getId));
		this.clickListener = clickListener;
		this.favoriteListener = favoriteListener;
	}

	public void submitFavourites(List<String> favorites) {
		this.favorites.clear();
		this.favorites.addAll(favorites);
		notifyDataSetChanged();
	}

	public void favoritesEnabled(boolean enable) {
		this.showFavored = enable;
		notifyDataSetChanged();
	}
    public void submitUserItems(List<KitchenItem> items) {
        this.userItems = (items != null) ? items : List.of();
        notifyDataSetChanged();
    }

	@NonNull
	@Override
	public RecipeItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.item_recipe, parent, false);
		return new RecipeItemViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull RecipeItemViewHolder holder, int position) {
		Recipe recipe = getItem(position);
		if (recipe == null) return;

		holder.titleTextView.setText(recipe.getName());
		holder.descriptionTextView.setText(recipe.getInstructions()); // todo: get description

		holder.favoriteCheckBox.setVisibility(showFavored ? View.VISIBLE : View.GONE);
		holder.favoriteCheckBox.setChecked(showFavored && favorites.contains(recipe.getId()));
		holder.favoriteCheckBox.setEnabled(favoriteListener != null);
		holder.favoriteCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
			if (favoriteListener != null)
				favoriteListener.onFavorited(recipe, isChecked);
		});

		List<KitchenItem> ingredients = recipe.getIngredients();
		if (ingredients == null || ingredients.isEmpty()) {
			holder.ingredientsRv.setVisibility(View.GONE);
        } else {
            holder.ingredientsRv.setVisibility(View.VISIBLE);

            var preview = limit(ingredients, 8);
            holder.ingredientsAdapter.submitList(preview);
            holder.ingredientsAdapter.setStatusMap(calcStatusMap(preview, recipe));


        }


        holder.itemView.setOnClickListener(v -> {
			if (clickListener != null) clickListener.onRecipeClick(recipe);
		});

	}

	private static <T> List<T> limit(List<T> list, int max) {
		if (list == null) return List.of();
		return list.size() <= max ? list : list.subList(0, max);
	}
    private Map<String, IngredientStatus> calcStatusMap(List<KitchenItem> recipeIngredients, Recipe recipe) {
        var missing = recipe.missingIngredients(userItems);

        Map<String, IngredientStatus> map = new HashMap<>();
        if (recipeIngredients == null) return map;

        for (KitchenItem req : recipeIngredients) {
            if (req == null) continue;

            String key = req.getIngredientKey();
            if (key == null) continue;

            double need = (req.getQuantity() != null) ? req.getQuantity().getAmount() : 0.0;
            var needUnit = (req.getQuantity() != null) ? req.getQuantity().getUnitType() : null;

            KitchenItem haveItem = KitchenItem.match(key, userItems);
            if (haveItem == null || haveItem.getQuantity() == null) {
                map.put(key, IngredientStatus.MISSING);
                continue;
            }

            double have = haveItem.getQuantity().getAmount();
            var haveUnit = haveItem.getQuantity().getUnitType();

            if (need <= 0) {
                map.put(key, IngredientStatus.ENOUGH);
                continue;
            }

            if (needUnit != null && haveUnit != null && needUnit != haveUnit) {
                map.put(key, IngredientStatus.PARTIAL);
                continue;
            }

            if (have >= need) map.put(key, IngredientStatus.ENOUGH);
            else if (have > 0) map.put(key, IngredientStatus.PARTIAL);
            else map.put(key, IngredientStatus.MISSING);
        }

        return map;
    }


	public static class RecipeItemViewHolder extends RecyclerView.ViewHolder {
		TextView titleTextView;
		TextView descriptionTextView;
		RecyclerView ingredientsRv;
		KitchenItemAdapter ingredientsAdapter;
		CheckBox favoriteCheckBox;

		public RecipeItemViewHolder(@NonNull View itemView) {
			super(itemView);
			titleTextView = itemView.findViewById(R.id.tv_recipe_name);
			descriptionTextView = itemView.findViewById(R.id.tv_recipe_description);
			ingredientsRv = itemView.findViewById(R.id.rv_ingredients_preview);
			ingredientsAdapter = new KitchenItemAdapter();
			ingredientsAdapter.setShowQuantity(false);
			ingredientsAdapter.setShowName(false);
			ingredientsAdapter.setShowIcon(true);
			ingredientsAdapter.setSmallIcons(true);
			ingredientsAdapter.setMinimalStyle(true);
			ingredientsRv.setLayoutManager(
					new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false)
			);
			ingredientsRv.setAdapter(ingredientsAdapter);
			favoriteCheckBox = itemView.findViewById(R.id.btn_favorite);
            ingredientsRv.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {

                private final android.view.GestureDetector detector =
                        new android.view.GestureDetector(itemView.getContext(),
                                new android.view.GestureDetector.SimpleOnGestureListener() {
                                    @Override
                                    public boolean onSingleTapUp(android.view.MotionEvent e) {
                                        itemView.performClick();
                                        return true;
                                    }
                                });

                @Override
                public boolean onInterceptTouchEvent(@NonNull RecyclerView rv,
                                                     @NonNull android.view.MotionEvent e) {
                    return detector.onTouchEvent(e);
                }
            });

        }
	}
}
