package com.eanie.mealy.ui.recipe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;


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

	@Nullable
	private Map<String, Map<String, IngredientStatus>> statusMap = new HashMap<>();


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

	public void submitStatusMap(@Nullable Map<String, Map<String, IngredientStatus>> statusMap) {
		this.statusMap = statusMap != null ? statusMap : new HashMap<>();
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
		var recipe = getItem(position);
		if (recipe == null) return;

		holder.titleTextView.setText(recipe.getName());
		holder.descriptionTextView.setText(recipe.getInstructions()); // todo: get description

		holder.favoriteCheckBox.setVisibility(showFavored ? View.VISIBLE : View.GONE);
		holder.favoriteCheckBox.setOnCheckedChangeListener(null);
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
			holder.ingredientsAdapter.submitList(ingredients);

			if (statusMap != null)
				holder.ingredientsAdapter.setStatusMap(statusMap.get(recipe.getId()));
		}

		holder.itemView.setOnClickListener(v -> {
			if (clickListener != null) clickListener.onRecipeClick(recipe);
		});
	}

	@Override
	public void onViewRecycled(@NonNull RecipeItemViewHolder holder) {
		super.onViewRecycled(holder);
		// Force the inner list to empty so it doesn't
		// get re-used when this ViewHolder is reused.
		holder.ingredientsAdapter.submitList(null);
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
		}
	}
}
