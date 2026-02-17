package com.eanie.mealy.data;

import android.net.Uri;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;

public class ImageRepo {
	StorageReference sto;

	public ImageRepo() {
		sto = FirebaseStorage.getInstance().getReference();
	}

	public String upload(Recipe recipe, @NonNull Uri imageUri) {
		StorageReference ref = sto.child("recipes/" + recipe.getChefId() + "/" + recipe.getName() + imageUri.getLastPathSegment());
		ref.putFile(imageUri);
		return ref.getPath();
	}
}
