package com.eanie.mealy.models;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.eanie.mealy.Recipe;
import com.eanie.mealy.ui.kitchen.KitchenItem;

import java.util.List;

public class RecipeAddViewModel extends UserRecipesViewModel {
    public RecipeAddViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<String> name = new MutableLiveData<>();
    public MutableLiveData<String> owner = new MutableLiveData<>();
    public MutableLiveData<List<KitchenItem>> ingredients = new MutableLiveData<>();

    public MutableLiveData<String> instructions = new MutableLiveData<>();
    public MutableLiveData<Uri> image = new MutableLiveData<>();

    public Recipe buildRecipe() {
        return new Recipe(
                owner.getValue(),
                name.getValue(),
                instructions.getValue(),
                ingredients.getValue(),
                owner.getValue()
        );
    }

    public void saveRecipe() {
        // todo: submit image to firebase storage
        // todo: generate recipe id?
        add(buildRecipe()); // save recipe to firebase
    }

    public void addIngredient(KitchenItem kitchenItem) {
        var items = ingredients.getValue();
        if (items == null) items = List.of();
        items.add(kitchenItem);
        ingredients.postValue(items);
    }
}
