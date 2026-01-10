package com.eanie.mealy.ui.kitchen.recipe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eanie.mealy.R;
import com.eanie.mealy.ui.kitchen.KitchenItem;

import java.util.ArrayList;
import java.util.List;

public class IngredientPreviewAdapter extends RecyclerView.Adapter<IngredientPreviewAdapter.VH> {

    private final List<KitchenItem> items = new ArrayList<>();

    public void submitItems(List<KitchenItem> newItems) {
        items.clear();
        if (newItems != null) items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ingredient_chip_item, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        private final ImageView iv;
        private final Context context;

        VH(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            iv = itemView.findViewById(R.id.iv_ingredient);
        }

        void bind(KitchenItem item) {
            String key = item.getIngredientKey();
            int resId = resolveIcon(context, key);
            iv.setImageResource(resId);
        }

        private static int resolveIcon(Context context, String key) {
            if (key != null) key = key.trim().toLowerCase();

            String[] candidates = new String[] {
                    (key == null ? null : "ic_" + key),
                    (key == null ? null : key),
                    (key == null ? null : "ic_" + key.replace("ing_", ""))
            };

            for (String name : candidates) {
                if (name == null) continue;
                int id = context.getResources().getIdentifier(
                        name, "drawable", context.getPackageName()
                );
                if (id != 0) return id;
            }
            return R.drawable.ic_ing_friger;

        }


    }
}
