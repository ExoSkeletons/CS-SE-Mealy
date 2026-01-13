package com.eanie.mealy.ui.kitchen.recipe;

import static com.eanie.mealy.models.UserDataViewModel.ARG_UUID;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eanie.mealy.R;
import com.eanie.mealy.models.DiscoveryViewModel;
import com.eanie.mealy.models.UserRecipesViewModel;

public class RecipeBrowseFragment extends Fragment {

    private UserRecipesViewModel userRecipesVM;
    private DiscoveryViewModel discoveryVM;
    private RecipeAdapter adapter;

    public RecipeBrowseFragment() {
        // Required empty public constructor
    }

    public static RecipeBrowseFragment newInstance(String userId) {
        RecipeBrowseFragment fragment = new RecipeBrowseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_UUID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ViewModel של המתכונים לפי משתמש (אם אתן משתמשים בזה)
        userRecipesVM = getDefaultViewModelProviderFactory().create(UserRecipesViewModel.class);

        Bundle args = getArguments();
        if (args != null) {
            String userId = args.getString(ARG_UUID, null);
            if (userId != null) {
                userRecipesVM.setUserId(userId);
            }
        }

        // ViewModel משותף – אותו אחד ש-KitchenFragment משתמש בו
        discoveryVM = new ViewModelProvider(requireActivity()).get(DiscoveryViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recipe_browse, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rvRecipes = view.findViewById(R.id.rv_recipes);

        adapter = new RecipeAdapter(recipe -> {
            // מעברים למסך מתכון מלא
            String userId = userRecipesVM.getUserId(); // יכול להיות null, זה בסדר – רק עובר הלאה
            RecipeFragment recipeFragment = RecipeFragment.newInstance(userId, recipe);

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.container, recipeFragment)
                    .addToBackStack("recipe-full")
                    .commit();
        });

        adapter.submitList(RecipeBrowseDemo.createDemoRecipes());

        rvRecipes.setAdapter(adapter);
        rvRecipes.setLayoutManager(new LinearLayoutManager(getContext()));
        View btnFavorites = view.findViewById(R.id.btn_open_favorites);
        btnFavorites.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.container, new FavoritesFragment())
                    .addToBackStack("favorites")
                    .commit();
        });


		/*discoveryVM.makeableRecipes().observe(getViewLifecycleOwner(), recipes -> {
			if (recipes == null) adapter.submitList(Collections.emptyList());
			else adapter.submitList(new ArrayList<>(recipes));
		});*/
	}

}
