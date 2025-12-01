package com.eanie.mealy.models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UserDataViewModel extends AndroidViewModel {
	protected final MutableLiveData<String> userId = new MutableLiveData<>();

	public UserDataViewModel(@NonNull Application application) {
		super(application);
	}

	public void setUserId(String id) {
		if (userId.getValue() == null || !userId.getValue().equals(id))
			userId.postValue(id);
	}

	public String getUserId() {
		return userId.getValue();
	}

	public MutableLiveData<String> userId() {
		return userId;
	}
}
