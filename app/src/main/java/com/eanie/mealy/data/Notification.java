package com.eanie.mealy.data;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;

import java.util.Objects;

public class Notification {
	@DocumentId
	String id;
	String receiverUuid;
	String senderUuid;
	String text;
	Timestamp timestamp;
    String senderName;

    boolean read;

	public Notification() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getReceiverUuid() {
		return receiverUuid;
	}

	public void setReceiverUuid(String receiverUuid) {
		this.receiverUuid = receiverUuid;
	}

	public String getSenderUuid() {
		return senderUuid;
	}

	public void setSenderUuid(String senderUuid) {
		this.senderUuid = senderUuid;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }


    public boolean isRead() {
		return read;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Notification that)) return false;
		return read == that.read
				&& Objects.equals(receiverUuid, that.receiverUuid)
				&& Objects.equals(senderUuid, that.senderUuid)
				&& Objects.equals(text, that.text)
				&& Objects.equals(timestamp, that.timestamp);
	}

	@NonNull
	@Override
	public String toString() {
		return "Notification{" +
				"@" + id + ',' +
				'\'' + senderUuid + '\'' + " -> " +
				'\'' + receiverUuid + '\'' + ',' +
				timestamp.toDate() + ',' +
				'\"' + text.substring(0, Math.min(text.length(), 10)) + '\"' +
				'}';
	}
}
