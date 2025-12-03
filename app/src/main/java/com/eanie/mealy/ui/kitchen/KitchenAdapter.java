package com.eanie.mealy.ui.kitchen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.eanie.mealy.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
		var context = holder.itemView.getContext();
		KitchenItem item = kitchenItems.get(position);
		var key = item.getIngredietKey();

		holder.quantityTextView.setText(item.getQuantity().toString());
		holder.nameTextView.setText(Resources.getString(context, key, R.string.ing_eggs));
		holder.iconImageView.setImageDrawable(Resources.getDrawable(context, key, R.drawable.ic_launcher_foreground));
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
