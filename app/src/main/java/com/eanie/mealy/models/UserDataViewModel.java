package com.eanie.mealy.models;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UserDataViewModel extends ViewModel {
	protected final MutableLiveData<String> userId = new MutableLiveData<>();

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
