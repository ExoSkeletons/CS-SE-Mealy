package com.eanie.mealy.models;

import android.app.Application;

import com.eanie.mealy.data.Notification;
import com.eanie.mealy.data.NotificationRepo;
import com.google.firebase.Timestamp;

import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

public class NotificationViewModel extends UserViewModel {
	private final NotificationRepo repo = new NotificationRepo();

	private final LiveData<List<Notification>> notifications = Transformations.switchMap(userId, repo::getForUser);


	public NotificationViewModel(@NonNull Application application) {
		super(application);
	}

	public LiveData<List<Notification>> notifications() {
		return notifications;
	}

	public void send(String text, String toUserId) {
		if (text == null || text.isEmpty()) return;
		if (userId.getValue() == null) return;

		var notification = new Notification();
		notification.setReceiverUuid(toUserId);
		notification.setSenderUuid(userId.getValue());
		notification.setText(text);
		notification.setTimestamp(Timestamp.now());

		repo.insert(notification);
	}

	public void markAsRead(Notification notification) {
		if (notification == null || userId.getValue() == null) return;
		repo.setStatusFor(userId.getValue(), List.of(notification), true);
	}

	public void markAllAsRead() {
		List<Notification> all = notifications.getValue();
		if (all == null || userId.getValue() == null) return;

		List<Notification> unread = all.stream()
				.filter(n -> !n.isRead())
				.collect(Collectors.toList());
		if (!unread.isEmpty())
			repo.setStatusFor(userId.getValue(), unread, true);
	}
}
