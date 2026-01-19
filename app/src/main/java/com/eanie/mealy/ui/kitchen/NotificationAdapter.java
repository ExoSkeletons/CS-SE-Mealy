package com.eanie.mealy.ui.kitchen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

	protected NotificationAdapter() {
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
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		holder.bind(getItem(position));
	}

	static class ViewHolder extends RecyclerView.ViewHolder {
		private final TextView tvText;
		private final TextView tvTime;
		private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, HH:mm", Locale.getDefault());

		public ViewHolder(@NonNull View itemView) {
			super(itemView);
			tvText = itemView.findViewById(R.id.tv_notification_text);
			tvTime = itemView.findViewById(R.id.tv_notification_time);
		}

		public void bind(Notification notification) {
			tvText.setText(notification.getText());
			if (notification.getTimestamp() != null) {
				tvTime.setText(dateFormat.format(notification.getTimestamp().toDate()));
			}
		}
	}
}
