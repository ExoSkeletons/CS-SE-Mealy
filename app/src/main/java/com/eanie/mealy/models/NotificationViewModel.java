package com.eanie.mealy.models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.eanie.mealy.data.Notification;
import com.eanie.mealy.data.NotificationRepo;
import com.eanie.mealy.data.Recipe;
import com.google.firebase.Timestamp;

import java.util.List;
import java.util.stream.Collectors;

public class NotificationViewModel extends UserViewModel {
	private final NotificationRepo repo = new NotificationRepo();

	private final LiveData<List<Notification>> notifications = Transformations.switchMap(userId, repo::getForUser);
    private static final long SPAM_COOLDOWN_MS = 15_000; // 15 seconds
    private long lastSentAtMs = 0;
    private String lastSentKey = null;



    public NotificationViewModel(@NonNull Application application) {
		super(application);
	}

	public LiveData<List<Notification>> notifications() {
		return notifications;
	}

    private void send(String toUserId, Notification notification) {
        if (toUserId == null || toUserId.isEmpty()) return;
        if (notification == null) return;
        if (userId.getValue() == null) return;

        // --- Spam mitigation (throttle duplicates) ---
        String text = notification.getText() == null ? "" : notification.getText();
        String key = toUserId + "|" + text;

        long now = System.currentTimeMillis();
        boolean sameAsLast = key.equals(lastSentKey);
        boolean tooSoon = (now - lastSentAtMs) < SPAM_COOLDOWN_MS;

        if (sameAsLast && tooSoon) {
            return; // ignore spam
        }

        lastSentKey = key;
        lastSentAtMs = now;
        // --- end spam mitigation ---

        notification.setReceiverUuid(toUserId);
        notification.setSenderUuid(userId.getValue());
        notification.setTimestamp(Timestamp.now());
        repo.insert(notification);
    }


    public void send(String text, String toUserId) {
		if (text == null || text.isEmpty()) return;
		if (userId.getValue() == null) return;

		var notification = new Notification();
		notification.setText(text);

		send(toUserId, notification);
	}

	public void sendRecipeLiked(Recipe recipe) {
		if (userId.getValue() == null) return;
		if (recipe == null || recipe.getChefId() == null) return;

		var notification = new Notification();
        notification.setText("liked your " + recipe.getName() + " recipe!");

        send(recipe.getChefId(), notification);
	}

	public void sendRecipeUsed(Recipe recipe) {
		if (userId.getValue() == null) return;
		if (recipe == null || recipe.getChefId() == null) return;

		var notification = new Notification();
        String displayName = "Someone";
        var fbUser = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser != null && fbUser.getDisplayName() != null && !fbUser.getDisplayName().isEmpty()) {
            displayName = fbUser.getDisplayName().split(" ")[0];
        }
        notification.setText("used your " + recipe.getName() + " recipe!");

		send(recipe.getChefId(), notification);
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
