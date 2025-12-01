package com.eanie.mealy.ui.kitchen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.eanie.mealy.R;

import java.util.List;

public class KitchenAdapter extends RecyclerView.Adapter<KitchenAdapter.ViewHolder> {

    private final List<KitchenItem> kitchenItems;

    public KitchenAdapter(List<KitchenItem> kitchenItems) {
        this.kitchenItems = kitchenItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.kitchen_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        KitchenItem item = kitchenItems.get(position);
        holder.quantityTextView.setText(item.getQuantity().toString());
	    // Dynamic Name
	    holder.nameTextView.setText(item.getIngredient().getDisplayName(holder.itemView.getContext()));

	    // Dynamic Icon
	    int iconResId = item.getIngredient().getIconResId(holder.itemView.getContext());
	    if (iconResId != 0) {
		    holder.iconImageView.setImageResource(iconResId);
	    }
    }

    @Override
    public int getItemCount() {
        return kitchenItems.size();
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
