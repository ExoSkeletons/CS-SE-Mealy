package com.eanie.mealy.models;

import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.eanie.mealy.R;
import com.eanie.mealy.data.ImageRepo;
import com.eanie.mealy.data.KitchenItem;
import com.eanie.mealy.data.Recipe;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SingleRecipeViewModel extends ViewModel {
	private final ImageRepo imageRepo = new ImageRepo();

	public MutableLiveData<String> name = new MutableLiveData<>("");
	public MutableLiveData<String> description = new MutableLiveData<>("");
	public MutableLiveData<String> instructions = new MutableLiveData<>("");
	public MutableLiveData<List<KitchenItem>> ingredients = new MutableLiveData<>(new ArrayList<>());
	public MutableLiveData<String> imagePath = new MutableLiveData<>(null);

	public Recipe build() {
		Recipe r = new Recipe(null,
				name.getValue(), // description.getValue(),
				instructions.getValue(),
				ingredients.getValue(),
				null
		);
		r.setImagePath(imagePath.getValue());
		return r;
	}

	public Recipe buildFor(String chefId) {
		var r = build();
		r.setChefId(chefId);
		return r;
	}

	public void set(Recipe recipe) {
		name.setValue(recipe.getName());
		// description.setValue(recipe.getDescription());
		instructions.setValue(recipe.getInstructions());
		ingredients.setValue(recipe.getIngredients() != null
				? new ArrayList<>(recipe.getIngredients())
				: new ArrayList<>()
		);
		imagePath.setValue(recipe.getImagePath());
	}

	public void addIngredient(KitchenItem item) {
		List<KitchenItem> current = ingredients.getValue();
		if (current != null) {
			current.add(item);
			// Re-set the value to trigger observers
			ingredients.setValue(current);
		}
	}

	public void loadImage(String path, ImageView imageView) {
		if (path == null || path.isEmpty()) {
			imageView.setVisibility(View.GONE);
			// Toast.makeText(imageView.getContext(), "No image", Toast.LENGTH_SHORT).show();
			return;
		}

		imageView.setVisibility(View.VISIBLE);
		imageView.setImageResource(R.drawable.ic_launcher_foreground);
		imageRepo.getDownloadUrl(path,
				uri -> Glide.with(imageView.getContext())
						.load(uri)
						.placeholder(R.drawable.ic_launcher_background)
						.error(R.drawable.ic_launcher_foreground)
						.into(imageView),
				e -> {
					e.printStackTrace();
					Toast.makeText(imageView.getContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
					imageView.setVisibility(View.GONE);
				}
		);
	}
	public void delete(Recipe currentRecipe) {
	}
}