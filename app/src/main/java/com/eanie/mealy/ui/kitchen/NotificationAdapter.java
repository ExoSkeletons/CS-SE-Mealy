package com.eanie.mealy.ui.kitchen;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.eanie.mealy.R;
import com.eanie.mealy.data.Notification;

import java.text.SimpleDateFormat;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class NotificationAdapter extends ListAdapter<Notification, NotificationAdapter.ViewHolder> {
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, HH:mm", Locale.getDefault());

	public interface OnMarkReadListener {
		void markAsRead(Notification notification);
	}

	private final OnMarkReadListener markAsRead;

	protected NotificationAdapter(OnMarkReadListener markAsRead) {
		super(new DiffUtil.ItemCallback<Notification>() {
			@Override
			public boolean areItemsTheSame(@NonNull Notification oldItem, @NonNull Notification newItem) {
				return oldItem.getId().equals(newItem.getId());
			}

			@Override
			public boolean areContentsTheSame(@NonNull Notification oldItem, @NonNull Notification newItem) {
				return oldItem.getText().equals(newItem.getText()) &&
						oldItem.getTimestamp().equals(newItem.getTimestamp()) &&
						oldItem.isRead() == newItem.isRead();
			}
		});
		this.markAsRead = markAsRead;
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

		holder.tvText.setText(notification.getText());
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
