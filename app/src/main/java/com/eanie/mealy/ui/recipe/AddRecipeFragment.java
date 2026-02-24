package com.eanie.mealy.ui.recipe;
import android.Manifest;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.eanie.mealy.R;
import com.eanie.mealy.data.Recipe;
import com.eanie.mealy.models.ItemsViewModel;
import com.eanie.mealy.models.RecipeAddViewModel;
import com.eanie.mealy.ui.kitchen.KitchenFragment;
import com.eanie.mealy.ui.kitchen.KitchenItemAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AddRecipeFragment extends Fragment {

	private static final String ARG_RECIPE = "recipe";

	private RecipeAddViewModel recipeAddVM;
	private ItemsViewModel itemsVM;

	private Recipe editingRecipe = null;

	private ActivityResultLauncher<String> chooseImageLauncher;
	private ActivityResultLauncher<Uri> takePictureLauncher;
	private ActivityResultLauncher<String> requestCameraPermissionLauncher;

	private Uri pendingCameraUri = null;

	public static AddRecipeFragment newInstance(@Nullable Recipe recipe) {
		AddRecipeFragment fragment = new AddRecipeFragment();
		if (recipe != null) {
			Bundle args = new Bundle();
			args.putSerializable(ARG_RECIPE, recipe);
			fragment.setArguments(args);
		}
		return fragment;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater,
							 @Nullable ViewGroup container,
							 @Nullable Bundle savedInstanceState) {
		var provider = new ViewModelProvider(requireActivity());
		recipeAddVM = provider.get(RecipeAddViewModel.class);
		itemsVM = provider.get(ItemsViewModel.class);

		var user = FirebaseAuth.getInstance().getCurrentUser();
		if (user != null)
			recipeAddVM.setUserId(user.getUid());

		if (getArguments() != null) {
			editingRecipe =
					(Recipe) getArguments().getSerializable(ARG_RECIPE);
		}

		chooseImageLauncher =
				registerForActivityResult(
						new ActivityResultContracts.GetContent(),
						uri -> {
							if (uri != null)
								recipeAddVM.setImage(uri);
						});

		takePictureLauncher =
				registerForActivityResult(
						new ActivityResultContracts.TakePicture(),
						success -> {
							if (success && pendingCameraUri != null)
								recipeAddVM.setImage(pendingCameraUri);
						});

		requestCameraPermissionLauncher =
				registerForActivityResult(
						new ActivityResultContracts.RequestPermission(),
						granted -> {
							if (granted) openCamera();
							else Toast.makeText(
									requireContext(),
									"Camera permission denied",
									Toast.LENGTH_SHORT).show();
						});

		return inflater.inflate(
				R.layout.fragment_recipe_add,
				container,
				false);
	}

	@Override
	public void onViewCreated(@NonNull View view,
							  @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		EditText etName =
				view.findViewById(R.id.et_recipe_name);
		EditText etInstructions =
				view.findViewById(R.id.et_instructions);
		FloatingActionButton btnSave =
				view.findViewById(R.id.btn_save_recipe);
		ImageButton btnCancel =
				view.findViewById(R.id.btn_close);

		ImageView ivRecipePhoto =
				view.findViewById(R.id.iv_recipe_photo);
		View btnRemoveImg =
				view.findViewById(R.id.btn_remove_img);
		View btnChoose =
				view.findViewById(R.id.btn_gallery);
		View btnCamera =
				view.findViewById(R.id.btn_camera);


		if (editingRecipe != null) {

			etName.setText(editingRecipe.getName());
			etInstructions.setText(
					editingRecipe.getInstructions());

			recipeAddVM.name.setValue(
					editingRecipe.getName());
			recipeAddVM.instructions.setValue(
					editingRecipe.getInstructions());
			recipeAddVM.ingredients.setValue(
					new ArrayList<>(
							editingRecipe.getIngredients()));
		}


		recipeAddVM.image.observe(
				getViewLifecycleOwner(),
				uri -> {
					if (uri != null)
						ivRecipePhoto.setImageURI(uri);
					else
						ivRecipePhoto.setImageDrawable(null);

					btnRemoveImg.setVisibility(
							uri != null ?
									View.VISIBLE :
									View.GONE);
				});

		btnRemoveImg.setOnClickListener(
				v -> recipeAddVM.setImage(null));
		btnChoose.setOnClickListener(
				v -> chooseImageLauncher.launch("image/*"));
		btnCamera.setOnClickListener(
				v -> openCamera());


		RecyclerView rvIngredients =
				view.findViewById(R.id.rv_ingredients);

		KitchenItemAdapter adapter =
				new KitchenItemAdapter(true);
		adapter.setItemClickListener(clicked ->
				KitchenFragment.showEditIngredientDialog(
						requireContext(),
						clicked,
						recipeAddVM::updateIngredient
				)
		);
		adapter.setQuantityListener(
				new KitchenItemAdapter.OnQuantityChangeListener() {
					@Override
					public void onPlus(String ingredientKey) {
						recipeAddVM.increaseAmount(ingredientKey, 1.0);
					}

					@Override
					public void onMinus(String ingredientKey) {
						recipeAddVM.increaseAmount(ingredientKey, -1.0);
					}
				}
		);
		adapter.setShowQuantity(true);

		rvIngredients.setLayoutManager(
				new GridLayoutManager(
						requireContext(), 2));
		rvIngredients.setAdapter(adapter);

		recipeAddVM.ingredients.observe(
				getViewLifecycleOwner(),
				adapter::submitList);

		Button btnAddIngredient =
				view.findViewById(R.id.btn_add_ingredient);

		btnAddIngredient.setOnClickListener(v ->
				KitchenFragment.showAddIngredientsDialog(
						getContext(),
						recipeAddVM.ingredients.getValue(),
						itemsVM.ingredients().getValue(),
						recipeAddVM::addIngredient,
						itemsVM::add));


		etName.addTextChangedListener(
				new TextWatcher() {
					@Override
					public void afterTextChanged(
							Editable s) {
						recipeAddVM.name.postValue(
								s.toString());
					}
					@Override public void beforeTextChanged(
							CharSequence s,int a,int b,int c){}
					@Override public void onTextChanged(
							CharSequence s,int a,int b,int c){}
				});

		etInstructions.addTextChangedListener(
				new TextWatcher() {
					@Override
					public void afterTextChanged(
							Editable s) {
						recipeAddVM.instructions.postValue(
								s.toString());
					}
					@Override public void beforeTextChanged(
							CharSequence s,int a,int b,int c){}
					@Override public void onTextChanged(
							CharSequence s,int a,int b,int c){}
				});

		btnSave.setOnClickListener(
				v -> saveRecipe());

		btnCancel.setOnClickListener(
				v -> getParentFragmentManager()
						.popBackStack());
	}

	private void saveRecipe() {

		if (editingRecipe != null) {

			recipeAddVM.updateRecipe(
					editingRecipe,
					updatedRecipe -> {
						RecipeFragment fragment =
								RecipeFragment.newInstance(updatedRecipe);
						getParentFragmentManager()
								.beginTransaction()
								.replace(R.id.container, fragment)
								.commit();
					}
			);

		} else {

			recipeAddVM.saveRecipe(
					recipe ->
							getParentFragmentManager()
									.popBackStack());
		}
	}

	private void openCamera() {

		if (ContextCompat.checkSelfPermission(
				requireContext(),
				Manifest.permission.CAMERA)
				!= android.content.pm.PackageManager.PERMISSION_GRANTED) {

			requestCameraPermissionLauncher
					.launch(Manifest.permission.CAMERA);
			return;
		}
		try {

			File dir = new File(
					requireContext()
							.getCacheDir(),
					"images");

			if (!dir.exists())
				dir.mkdirs();

			File imageFile =
					File.createTempFile(
							"recipe_", ".jpg", dir);

			pendingCameraUri =
					FileProvider.getUriForFile(
							requireContext(),
							requireContext()
									.getPackageName()
									+ ".fileprovider",
							imageFile);

			takePictureLauncher
					.launch(pendingCameraUri);

		} catch (IOException e) {
			Toast.makeText(requireContext(),
					"Failed to open camera",
					Toast.LENGTH_SHORT).show();
		}
	}
}
