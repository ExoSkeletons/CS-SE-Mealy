package com.eanie.mealy.ui.kitchen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.eanie.mealy.R;
import com.eanie.mealy.data.IngredientStatus;
import com.eanie.mealy.data.ItemKeyCallback;
import com.eanie.mealy.data.KitchenItem;
import com.eanie.mealy.ui.Resources;
import com.google.android.material.card.MaterialCardView;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;




public class KitchenItemAdapter extends ListAdapter<KitchenItem, KitchenItemAdapter.ViewHolder> {
	public interface OnItemClickListener {
		void onItemClick(KitchenItem item);
	}

	public interface OnQuantityChangeListener {
		void onPlus(String ingredientKey);

		void onMinus(String ingredientKey);
	}

	private boolean minimalStyle = false;
	private boolean showIcon = true;
	private boolean smallIcons = false;
	private boolean showQuantity = true;
	private boolean showName = true;

	private boolean isSelectionEnabled = false;
	private final Set<String> selectedKeys = new HashSet<>();

	private OnQuantityChangeListener quantityListener;
	private OnItemClickListener itemClickListener;
	private final boolean compact;
	private Map<String, IngredientStatus> statusMap = Map.of();


	public KitchenItemAdapter(boolean compact) {
		super(new ItemKeyCallback<>(KitchenItem::getIngredientKey));
		this.compact = compact;
	}

	public KitchenItemAdapter() {
		this(false);
	}

	public void setQuantityListener(OnQuantityChangeListener quantityListener) {
		this.quantityListener = quantityListener;
		notifyDataSetChanged();
	}

	public void setItemClickListener(OnItemClickListener itemClickListener) {
		this.itemClickListener = itemClickListener;
		notifyDataSetChanged();
	}

	public void setMinimalStyle(boolean minimalStyle) {
		this.minimalStyle = minimalStyle;
		notifyDataSetChanged();
	}

	public void setShowIcon(boolean showIcon) {
		this.showIcon = showIcon;
		notifyDataSetChanged();
	}

	public void setSmallIcons(boolean smallIcons) {
		this.smallIcons = smallIcons;
		notifyDataSetChanged();
	}

	public void setShowQuantity(boolean showQuantity) {
		this.showQuantity = showQuantity;
		notifyDataSetChanged();
	}

	public void setShowName(boolean showName) {
		this.showName = showName;
		notifyDataSetChanged();
	}

	public void setSelectionMode(boolean enabled) {
		this.isSelectionEnabled = enabled;
		notifyDataSetChanged();
	}
    public void setStatusMap(Map<String, IngredientStatus> statusMap) {
        this.statusMap = (statusMap != null) ? statusMap : Map.of();
        notifyDataSetChanged();
    }


	public Set<String> getSelectedKeys() {
		return selectedKeys;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(
				compact ? R.layout.item_kitchen_item_compact : R.layout.item_kitchen_item,
				parent, false
		);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		var context = holder.itemView.getContext();
		var item = getItem(position);
		var itemKey = item.getIngredientKey();

		// style
		var card = (MaterialCardView) holder.itemView;
		if (minimalStyle) {
			card.setCardBackgroundColor(android.graphics.Color.TRANSPARENT);
			card.setCardElevation(0);
			card.setStrokeWidth(0);
			((ViewGroup.MarginLayoutParams) card.getLayoutParams()).setMargins(0, 0, 0, 0);
			holder.iconImageView.setElevation(10f);
		} else {
			card.setCardElevation(8f);
			holder.iconImageView.setElevation(0f);
		}
		if (smallIcons) holder.iconImageView.setLayoutParams(new FrameLayout.LayoutParams(94, 94));

		// selection and clicking
		if (isSelectionEnabled) {
			holder.checkBox.setVisibility(View.VISIBLE);
			holder.checkBox.setOnCheckedChangeListener(null);
			holder.checkBox.setChecked(selectedKeys.contains(itemKey));

			holder.checkBox.setOnCheckedChangeListener((btn, isChecked) -> {
				if (isChecked) selectedKeys.add(itemKey);
				else selectedKeys.remove(itemKey);
			});

			// Make whole card clickable for easier selection
			holder.itemView.setOnClickListener(v -> {
				holder.checkBox.performClick();
				if (itemClickListener != null)
					itemClickListener.onItemClick(item);
			});
			holder.itemView.setClickable(true);
		} else {
			holder.checkBox.setVisibility(View.GONE);
			holder.itemView.setOnClickListener(itemClickListener != null
					? v -> itemClickListener.onItemClick(item)
					: null
			);
			holder.itemView.setClickable(itemClickListener != null);
		}

		// data binding
		holder.nameTextView.setText(Resources.getString(context, itemKey, itemKey));
		holder.quantityTextView.setText(item.getQuantity().toString());

		// visibility
		holder.nameTextView.setVisibility(showName ? View.VISIBLE : View.GONE);
		holder.quantityTextView.setVisibility(showQuantity ? View.VISIBLE : View.GONE);
		holder.btnIncrease.setVisibility(quantityListener != null ? View.VISIBLE : View.GONE);
		holder.btnDecrease.setVisibility(quantityListener != null ? View.VISIBLE : View.GONE);
        if (showIcon) {
            if (holder.iconBadge != null) holder.iconBadge.setVisibility(View.VISIBLE);
            holder.iconImageView.setImageDrawable(Resources.getItemIcon(context, itemKey));
            holder.iconImageView.setVisibility(View.VISIBLE);

            // ---- status badge background ----
            var status = statusMap.getOrDefault(itemKey, IngredientStatus.ENOUGH);

            holder.iconImageView.clearColorFilter();
            holder.iconImageView.setAlpha(1f);

            int bgRes;
            switch (status) {
                case PARTIAL:
                    bgRes = R.drawable.bg_icon_badge_partial;
                    break;
                case MISSING:
                    bgRes = R.drawable.bg_icon_badge_missing;
                    break;
                case ENOUGH:
                default:
                    bgRes = R.drawable.bg_icon_badge_enough;
                    break;
            }

            if (holder.iconBadge != null) {
                holder.iconBadge.setBackgroundResource(bgRes);
            }

        } else {
            holder.iconImageView.setVisibility(View.GONE);
            if (holder.iconBadge != null) holder.iconBadge.setVisibility(View.GONE);

        }


        // quantity controls
		if (quantityListener != null) {
			holder.btnIncrease.setOnClickListener(v -> quantityListener.onPlus(itemKey));
			holder.btnDecrease.setOnClickListener(v -> quantityListener.onMinus(itemKey));
		}
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {

		ImageView iconImageView;
		TextView nameTextView;
		TextView quantityTextView;
		ImageButton btnIncrease;
		ImageButton btnDecrease;
		CheckBox checkBox;
        FrameLayout iconBadge;


        public ViewHolder(@NonNull View itemView) {
			super(itemView);
			iconImageView = itemView.findViewById(R.id.item_icon);
			nameTextView = itemView.findViewById(R.id.item_name);
			quantityTextView = itemView.findViewById(R.id.item_quantity);
			btnIncrease = itemView.findViewById(R.id.btn_increase);
			btnDecrease = itemView.findViewById(R.id.btn_decrease);
			checkBox = itemView.findViewById(R.id.item_checkbox);
            iconBadge = itemView.findViewById(R.id.icon_badge);

        }
	}
}
