package com.eanie.mealy.models;

import android.app.Application;

import com.eanie.mealy.data.UserData;
import com.eanie.mealy.data.UserRepo;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

public class UserInfoViewModel extends UserDataViewModel {
	private final UserRepo repo = new UserRepo();

	private final LiveData<UserData> userData = Transformations.switchMap(userId, id -> {
		if (id == null || id.isEmpty()) return new MutableLiveData<>(null);
		return repo.getDataOf(id);
	});

	protected final LiveData<Boolean> isChef = Transformations.map(userData, u -> u != null && u.isChef());

	public UserInfoViewModel(@NonNull Application application) {
		super(application);
	}

	protected void updateData(UserData data) {
		if (data == null) return;
		repo.insert(getUserId(), data);
	}

	public LiveData<Boolean> isChef() {
		return isChef;
	}

	public void setIsChef(boolean isChef) {
		if (getUserId() == null) return;

		var data = userData.getValue();
		if (data == null) return;
		data.setIsChef(isChef);
		updateData(data);
	}
}
