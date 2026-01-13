package com.eanie.mealy.models;

import android.app.Application;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class UserDataViewModel extends AndroidViewModel {
	public static final String ARG_UUID = "uuid";

	protected final MutableLiveData<String> userId = new MutableLiveData<>();

	public UserDataViewModel(@NonNull Application application) {
		super(application);
	}

	public void setUserId(String id) {
		if (!Objects.equals(userId.getValue(), id))
			userId.postValue(id);
	}

	public String getUserId() {
		return userId.getValue();
	}

	public LiveData<String> userId() {
		return userId;
	}
}
