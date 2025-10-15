package com.example.autoschoolbtgp.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageView;
import com.example.autoschoolbtgp.LoginActivity;
import com.example.autoschoolbtgp.R;
import com.example.autoschoolbtgp.databinding.FragmentProfileBinding;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        startCropActivity(selectedImageUri);
                    }
                }
            });

    private final ActivityResultLauncher<CropImageContractOptions> cropLauncher = registerForActivityResult(
            new CropImageContract(),
            result -> {
                if (result.isSuccessful()) {
                    Uri resultUri = result.getUriContent();
                    if (resultUri != null) {
                        handleCroppedImage(resultUri);
                    } else {
                        Toast.makeText(requireContext(), "Ошибка: URI результата ImageCropper был null.", Toast.LENGTH_SHORT).show();
                    }
                } else if (!result.isSuccessful() && result.getError() == null) {
                    Toast.makeText(requireContext(), "Обрезка фото отменена", Toast.LENGTH_SHORT).show();
                } else {
                    Exception error = result.getError();
                    Toast.makeText(requireContext(), "Ошибка обрезки фото: " + (error != null ? error.getMessage() : "Неизвестная ошибка"), Toast.LENGTH_SHORT).show();
                }
                if (binding != null) {
                    binding.btnChangePhoto.setEnabled(true);
                }
            });

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        viewModel.loadCurrentUser();

        viewModel.getUserData().observe(getViewLifecycleOwner(), userModel -> {
            if (userModel != null) {
                binding.editFirstName.setText(userModel.getName());
                binding.editLastName.setText(userModel.getSurname());
                binding.editMiddleName.setText(userModel.getMiddleName() != null ? userModel.getMiddleName() : "");

                String photoUrl = userModel.getAvatarUrl();
                if (photoUrl != null && !photoUrl.isEmpty()) {
                    Glide.with(this)
                            .load(photoUrl)
                            .placeholder(R.drawable.users_icon)
                            .error(R.drawable.users_icon)
                            .into(binding.profileImage);
                } else {
                    binding.profileImage.setImageResource(R.drawable.users_icon);
                }
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), "Ошибка: " + error, Toast.LENGTH_LONG).show();
                if (binding != null) {
                    binding.btnChangePhoto.setEnabled(true);
                }
            }
        });

        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                if (binding != null) {
                    binding.btnChangePhoto.setEnabled(true);
                }
            }
        });

        setupClickListeners();

        return view;
    }

    private void setupClickListeners() {
        binding.btnChangePhoto.setOnClickListener(v -> {
            binding.btnChangePhoto.setEnabled(false);
            openGallery();
        });

        binding.btnSave.setOnClickListener(v -> {
            String firstName = binding.editFirstName.getText().toString().trim();
            String lastName = binding.editLastName.getText().toString().trim();
            String middleName = binding.editMiddleName.getText().toString().trim();
            String password = binding.editPassword.getText().toString().trim();

            if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName)) {
                Toast.makeText(requireContext(), "Имя и фамилия обязательны", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.updateProfileTextOnly(firstName, lastName, middleName, password);
        });

        binding.btnLogout.setOnClickListener(v -> {
            viewModel.logout();

            Intent intent = new Intent(requireContext(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void startCropActivity(Uri sourceUri) {
        File destinationFile = new File(requireContext().getCacheDir(), "cropped_image_senior.jpg");
        Uri destinationUri = Uri.fromFile(destinationFile);

        CropImageContractOptions options = new CropImageContractOptions(sourceUri, new com.canhub.cropper.CropImageOptions());
        options.setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .setFixAspectRatio(true)
                .setCropShape(CropImageView.CropShape.OVAL)
                .setRequestedSize(1024, 1024)
                .setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                .setOutputCompressQuality(85);

        cropLauncher.launch(options);
    }

    private void handleCroppedImage(Uri croppedImageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), croppedImageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos);
            byte[] imageBytes = baos.toByteArray();

            viewModel.uploadNewPhotoAndSaveProfile(imageBytes);
            binding.profileImage.setImageURI(croppedImageUri);

        } catch (IOException e) {
            Toast.makeText(requireContext(), "Ошибка обработки фото: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            if (binding != null) {
                binding.btnChangePhoto.setEnabled(true);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}