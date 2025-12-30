package com.eanie.mealy.ui.kitchen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.eanie.mealy.Quantity;
import com.eanie.mealy.R;
import com.eanie.mealy.UnitType;

public class KitchenItemAdapter extends ListAdapter<KitchenItem, KitchenItemAdapter.ViewHolder> {

    private final boolean showIcon;

    public KitchenItemAdapter(boolean showIcon) {
        super(new KitchenItemItemCallback());
        this.showIcon = showIcon;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.kitchen_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        KitchenItem item = getItem(position);

        // הצגת השם
        holder.nameTextView.setText(
                Resources.getString(
                        holder.itemView.getContext(),
                        item.getIngredientKey(),
                        item.getIngredientKey())
        );

        holder.quantityTextView.setText(item.getQuantity().toString());

        if (!showIcon) {
            holder.iconImageView.setVisibility(View.GONE);
        } else {
            holder.iconImageView.setImageDrawable(
                    Resources.getDrawable(
                            holder.itemView.getContext(),
                            item.getIngredientKey(),
                            R.drawable.ic_launcher_foreground)
            );
            holder.iconImageView.setVisibility(View.VISIBLE);
        }

        holder.btnIncrease.setOnClickListener(v -> {
            int pos = holder.getBindingAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            KitchenItem current = getItem(pos);

            Quantity q = current.getQuantity();
            double amount = q.getAmount();
            UnitType unit = q.getUnitType();
            double step = unit.stepAmountBy;
            amount = amount + step;
            current.setQuantity(new Quantity(amount, unit));
            notifyItemChanged(pos);
            q.normalize();


            current.setQuantity(new Quantity(amount, unit));

            notifyItemChanged(pos);
        });

        // ===== כפתור - =====
        holder.btnDecrease.setOnClickListener(v -> {
            int pos = holder.getBindingAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            KitchenItem current = getItem(pos);

            Quantity q = current.getQuantity();
            double amount = q.getAmount();
            UnitType unit = q.getUnitType();

            double step = unit.stepAmountBy;
            if (amount > 0) {
                amount = amount - step;
                if (amount < 0) amount = 0; // לוודא לא לרדת מתחת ל-0
                current.setQuantity(new Quantity(amount, unit));
                q.normalize();
                notifyItemChanged(pos);

            }
        });
    }

    // השוואה בין פריטים לריענון יעיל של הרשימה
    private static class KitchenItemItemCallback extends DiffUtil.ItemCallback<KitchenItem> {
        @Override
        public boolean areItemsTheSame(@NonNull KitchenItem oldItem, @NonNull KitchenItem newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull KitchenItem oldItem, @NonNull KitchenItem newItem) {
            return oldItem.getIngredientKey().equals(newItem.getIngredientKey())
                    && oldItem.getQuantity().equals(newItem.getQuantity());
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iconImageView;
        TextView nameTextView;
        TextView quantityTextView;
        Button btnIncrease;
        Button btnDecrease;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.item_icon);
            nameTextView = itemView.findViewById(R.id.item_name);
            quantityTextView = itemView.findViewById(R.id.item_quantity);
            btnIncrease = itemView.findViewById(R.id.btn_increase);
            btnDecrease = itemView.findViewById(R.id.btn_decrease);
        }
    }
}
