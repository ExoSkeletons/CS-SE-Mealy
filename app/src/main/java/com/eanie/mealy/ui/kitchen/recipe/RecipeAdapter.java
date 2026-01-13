package com.eanie.mealy.ui.kitchen.recipe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.eanie.mealy.R;
import com.eanie.mealy.Recipe;
import com.eanie.mealy.ui.kitchen.KitchenItem;

import java.util.List;
import java.util.Objects;


public class RecipeAdapter extends ListAdapter<Recipe, RecipeAdapter.RecipeItemViewHolder> {
    private final OnRecipeClickListener listener;

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    public RecipeAdapter(OnRecipeClickListener listener) {
        super(new RecipeItemItemCallback());
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecipeItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_item, parent, false);
        return new RecipeItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeItemViewHolder holder, int position) {
        Recipe recipe = getItem(position);

        holder.titleTextView.setText(recipe.getName());
        holder.descriptionTextView.setText(recipe.getInstructions()); // todo: get description

        holder.favoriteCheckBox.setChecked(FavoritesStore.isFavorite(recipe));
	    holder.favoriteCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> FavoritesStore.setFavorite(recipe, isChecked));

        List<KitchenItem> ingredients = recipe.getIngredients();
        if (ingredients == null || ingredients.isEmpty()) {
            holder.ingredientsRv.setVisibility(View.GONE);
        } else {
            holder.ingredientsRv.setVisibility(View.VISIBLE);
            holder.ingredientsAdapter.submitItems(limit(ingredients, 8));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onRecipeClick(recipe);
        });

    }
    private static <T> List<T> limit(List<T> list, int max) {
        if (list == null) return List.of();
        return list.size() <= max ? list : list.subList(0, max);
    }


    public static class RecipeItemViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;
        RecyclerView ingredientsRv;
        IngredientPreviewAdapter ingredientsAdapter;
        CheckBox favoriteCheckBox;

        public RecipeItemViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.tv_recipe_name);
            descriptionTextView = itemView.findViewById(R.id.tv_recipe_description);
            ingredientsRv = itemView.findViewById(R.id.rv_ingredients_preview);
            ingredientsAdapter = new IngredientPreviewAdapter();
            favoriteCheckBox = itemView.findViewById(R.id.btn_favorite);

            ingredientsRv.setLayoutManager(
                    new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false)
            );
            ingredientsRv.setAdapter(ingredientsAdapter);
        }
    }

    private static class RecipeItemItemCallback extends DiffUtil.ItemCallback<Recipe> {
        @Override
        public boolean areItemsTheSame(@NonNull Recipe oldItem, @NonNull Recipe newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Recipe oldItem, @NonNull Recipe newItem) {
            return Objects.equals(oldItem.getId(), newItem.getId());
        }

    }
}
