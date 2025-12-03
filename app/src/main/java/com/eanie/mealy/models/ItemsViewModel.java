package com.eanie.mealy.models;

import android.app.Application;
import android.widget.Toast;

import com.eanie.mealy.R;
import com.eanie.mealy.data.ItemsRepo;
import com.eanie.mealy.ui.kitchen.KitchenItem;
import com.eanie.mealy.ui.kitchen.Resources;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

public class ItemsViewModel extends UserDataViewModel {
	private final ItemsRepo repo = new ItemsRepo();

	private final LiveData<List<KitchenItem>> ingredients = Transformations.switchMap(userId, id -> {
		if (id == null || id.isEmpty()) return new MutableLiveData<>(new ArrayList<>());
		return repo.itemsOf(id);
	});

	public ItemsViewModel(@NonNull Application application) {
		super(application);
	}

	public LiveData<List<KitchenItem>> myItems() {
		return ingredients;
	}

	public void addIngredient(KitchenItem item) {
		if (getUserId() != null) {
			Toast.makeText(
					getApplication(),
					"Added " + Resources.getString(getApplication(), item.getIngredietKey(), R.string.ing_eggs),
					Toast.LENGTH_SHORT
			).show();
			if (getUserId() != null)
				repo.addIngredient(getUserId(), item);
		}
	}
}