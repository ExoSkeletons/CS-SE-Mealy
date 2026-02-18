package com.eanie.mealy.data;

import android.net.Uri;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;

public class ImageRepo {
	private final StorageReference sto;

	public ImageRepo() {
		sto = FirebaseStorage.getInstance().getReference();
	}

	public void upload(@NonNull Recipe recipe, @NonNull Uri imageUri, OnSuccessListener<String> onSuccess, OnFailureListener onFailure) {
		String path = "recipes/" + recipe.getChefId() + "/" + recipe.getName();
		StorageReference ref = sto.child(path);
		ref.putFile(imageUri)
				.addOnSuccessListener(snapshot -> onSuccess.onSuccess(snapshot.getStorage().getPath()))
				.addOnFailureListener(onFailure);
	}

	public void getDownloadUrl(String path, OnSuccessListener<Uri> onSuccess, OnFailureListener onFailure) {
		if (path == null || path.isEmpty()) {
			onFailure.onFailure(new IllegalArgumentException("Path is null or empty"));
			return;
		}
		sto.child(path).getDownloadUrl()
				.addOnSuccessListener(onSuccess)
				.addOnFailureListener(onFailure);
	}
}
