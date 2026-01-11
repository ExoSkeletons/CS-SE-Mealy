package com.eanie.mealy.ui.kitchen.recipe;

import com.eanie.mealy.Quantity;
import com.eanie.mealy.Recipe;
import com.eanie.mealy.UnitType;
import com.eanie.mealy.ui.kitchen.KitchenItem;

import java.util.List;


public class RecipeBrowseDemo {

    public static List<Recipe> createDemoRecipes() {
        String demoChef = "demo-chef";
        return List.of(
                new Recipe(
                        "123",
                        "Pasta Bolognese",
                        "Classic pasta with red sauce.",
                        List.of(
                                new KitchenItem("ing_pasta", new Quantity(200, UnitType.GRAMS)),
                                new KitchenItem("ing_tomato", new Quantity(.10, UnitType.LITERS)),
                                new KitchenItem("ing_onion", new Quantity(3))
                        ),
                        demoChef
                ),
                new Recipe(
                        "456",
                        "Vegetable Stir-Fry",
                        "Quick dinner with mixed vegetables.",
                        List.of(),
                        demoChef
                ),
                new Recipe(
                        "789",
                        "Chocolate Cake",
                        "Rich and moist chocolate cake.",
                        List.of(
                                new KitchenItem("ing_flour", new Quantity(500, UnitType.GRAMS)),
                                new KitchenItem("ing_sugar", new Quantity(50, UnitType.GRAMS)),
                                new KitchenItem("ing_eggs", new Quantity(3)),
                                new KitchenItem("ing_butter", new Quantity(50, UnitType.GRAMS)),
                                new KitchenItem("ing_chocolate", new Quantity(200, UnitType.GRAMS)),
                                new KitchenItem("ing_vanilla", new Quantity(0.5, UnitType.TABLE_SPOONS)),
                                new KitchenItem("ing_baking_soda", new Quantity(2, UnitType.TABLE_SPOONS)),
                                new KitchenItem("ing_salt", new Quantity(UnitType.TABLE_SPOONS)),
                                new KitchenItem("ing_milk", new Quantity(.10, UnitType.LITERS))
                        ),
                        demoChef
                ),
                new Recipe(
                        "321",
                        "Omelette",
                        "Simple omelette with cheese and herbs.",
                        List.of(
                                new KitchenItem("ing_eggs", new Quantity(3)),
                                new KitchenItem("ing_cheese", new Quantity(200, UnitType.GRAMS)),
                                new KitchenItem("ing_salt", new Quantity(2, UnitType.TABLE_SPOONS)),
                                new KitchenItem("ing_pepper", new Quantity(UnitType.TABLE_SPOONS)),
                                new KitchenItem("ing_oil", new Quantity(10, UnitType.GRAMS)),
                                new KitchenItem("ing_onion", new Quantity(1)),
                                new KitchenItem("ing_garlic", new Quantity(1)),
                                new KitchenItem("ing_mushroom", new Quantity(200, UnitType.GRAMS))
                        ),
                        demoChef
                ));
    }
}
