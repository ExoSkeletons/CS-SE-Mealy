package com.eanie.mealy.models;

import android.app.Application;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

public abstract class UserViewModel extends AndroidViewModel {
	public static final String ARG_UUID = "uuid";

	protected final MutableLiveData<String> userId = new MutableLiveData<>();
	protected final LiveData<String> userName = Transformations.map(userId, id -> {
		if (id == null || id.isEmpty()) return null;
		var user = FirebaseAuth.getInstance().getCurrentUser(); // todo: switch userId to fb-user / user-data class
		if (user == null) return null;
		return user.getDisplayName();
	});

	public UserViewModel(@NonNull Application application) {
		super(application);
	}

	public void setUserId(String id) {
		if (!Objects.equals(userId.getValue(), id))
			userId.setValue(id);
	}

	public String getUserId() {
		return userId.getValue();
	}

	public LiveData<String> userId() {
		return userId;
	}

	@NonNull
	public String getUserName() {
		return userName.getValue() == null ? "A User" : userName.getValue();
	}

	public LiveData<String> userName() {
		return userName;
	}

	public static <T extends Fragment> T withUserId(String userId, T fragment) {
		var fArgs = fragment.getArguments();
		Bundle args = fArgs == null ? new Bundle() : fArgs;
		args.putString(ARG_UUID, userId);
		fragment.setArguments(args);
		return fragment;
	}
}
