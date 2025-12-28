package com.eanie.mealy.ui.kitchen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;


import com.eanie.mealy.R;

public class KitchenItemAdapter extends ListAdapter<KitchenItem, KitchenItemAdapter.ViewHolder> {
    private final boolean showIcon;

    public KitchenItemAdapter(boolean showIcon) {
        super(new KitchenItemItemCallback());
        this.showIcon = showIcon;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.kitchen_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        KitchenItem item = getItem(position);

        holder.quantityTextView.setText(item.getQuantity().toString());
        holder.nameTextView.setText(Resources.getString(holder.itemView.getContext(), item.getIngredietKey(), R.string.ing_eggs));
        if (!showIcon)
            holder.iconImageView.setVisibility(View.GONE);
        else {
            holder.iconImageView.setImageDrawable(Resources.getDrawable(holder.itemView.getContext(), item.getIngredietKey(), R.drawable.ic_ing_eggs));
            holder.iconImageView.setVisibility(View.VISIBLE);
        }
    }

    private static class KitchenItemItemCallback extends DiffUtil.ItemCallback<KitchenItem> {
        @Override
        public boolean areItemsTheSame(@NonNull KitchenItem oldItem, @NonNull KitchenItem newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull KitchenItem oldItem, @NonNull KitchenItem newItem) {
            return oldItem.getIngredietKey().equals(newItem.getIngredietKey());
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImageView;
        TextView nameTextView;
        TextView quantityTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.item_icon);
            nameTextView = itemView.findViewById(R.id.item_name);
            quantityTextView = itemView.findViewById(R.id.item_quantity);
        }
    }
}
