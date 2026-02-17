package com.eanie.mealy.ui.kitchen;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.eanie.mealy.R;
import com.eanie.mealy.data.ItemKeyCallback;
import com.eanie.mealy.data.Notification;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class NotificationAdapter extends ListAdapter<Notification, NotificationAdapter.ViewHolder> {

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, HH:mm", Locale.getDefault());

	public interface OnMarkReadListener {
		void markAsRead(Notification notification);
	}

	private final OnMarkReadListener markAsRead;
    private final java.util.Map<String, String> senderNameCache;

    protected NotificationAdapter(OnMarkReadListener markAsRead,
                                  java.util.Map<String, String> senderNameCache) {
        super(new ItemKeyCallback<>(Notification::getId));
        this.markAsRead = markAsRead;
        this.senderNameCache = senderNameCache;
    }


    @NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		Notification notification = getItem(position);
		if (notification == null) return;

        String text = notification.getText();

        String senderId = notification.getSenderUuid();
        String name = (senderId != null && senderNameCache != null)
                ? senderNameCache.get(senderId)
                : null;

        if (name != null && !name.isEmpty()) {
            holder.tvText.setText(name + " " + text);
        } else {
            holder.tvText.setText(text);
        }
		if (notification.getTimestamp() != null)
			holder.tvTime.setText(dateFormat.format(notification.getTimestamp().toDate()));
		else holder.tvTime.setText("");
		holder.tvText.setTypeface(null, !notification.isRead() ? Typeface.BOLD : Typeface.NORMAL);
		holder.btnMarkRead.setVisibility(notification.isRead() ? View.INVISIBLE : View.VISIBLE);
		holder.btnMarkRead.setOnClickListener(v -> {
			markAsRead.markAsRead(notification);
			notifyItemChanged(position);
		});
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		private final TextView tvText;
		private final TextView tvTime;
		private final ImageButton btnMarkRead;

		public ViewHolder(@NonNull View itemView) {
			super(itemView);
			tvText = itemView.findViewById(R.id.tv_notification_text);
			tvTime = itemView.findViewById(R.id.tv_notification_time);
			btnMarkRead = itemView.findViewById(R.id.btn_mark_read);
		}
	}
}
