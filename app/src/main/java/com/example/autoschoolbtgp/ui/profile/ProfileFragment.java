package com.example.autoschoolbtgp.ui.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.autoschoolbtgp.R;
import com.example.autoschoolbtgp.databinding.FragmentProfileBinding;
import com.yalantis.ucrop.UCrop;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.IOException;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;

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

        // Загрузка текущих данных
        viewModel.loadCurrentUser();

        // Обновление UI из ViewModel
        viewModel.getUserData().observe(getViewLifecycleOwner(), userData -> {
            if (userData != null) {
                binding.editFirstName.setText(userData.firstName);
                binding.editLastName.setText(userData.lastName);
                binding.editMiddleName.setText(userData.middleName);
                // Загрузка фото (если ParseFile)
                // TODO: Загрузка фото из ParseFile в ImageView (например, с помощью Picasso или Glide)
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        setupClickListeners();

        return view;
    }

    private void setupClickListeners() {
        binding.btnChangePhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1000);
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

            viewModel.updateProfile(firstName, lastName, middleName, password);
        });

        binding.btnLogout.setOnClickListener(v -> {
            viewModel.logout();
            startActivity(new Intent(requireContext(), com.example.autoschoolbtgp.LoginActivity.class));
            requireActivity().finish();
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null) {
                // Устанавливаем фото в CircleImageView
                binding.profileImage.setImageURI(resultUri);

                // Конвертируем в byte[] для Parse
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), resultUri);
                    java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                    byte[] imageBytes = baos.toByteArray();

                    // Передаем фото в ViewModel
                    viewModel.setNewPhotoBytes(imageBytes);
                } catch (IOException e) {
                    Log.e("ProfileFragment", "Ошибка при загрузке фото", e);
                    Toast.makeText(requireContext(), "Ошибка загрузки фото", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (resultCode == UCrop.RESULT_ERROR && requestCode == UCrop.REQUEST_CROP) {
            final Throwable cropError = UCrop.getError(data);
            if (cropError != null) {
                Log.e("ProfileFragment", "Ошибка UCrop", cropError);
                Toast.makeText(requireContext(), "Ошибка обрезки фото", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 1000 && resultCode == getActivity().RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                startCropActivity(selectedImageUri);
            }
        }
    }

    private void startCropActivity(Uri sourceUri) {
        File destinationFile = new File(requireContext().getCacheDir(), "cropped_image.jpg");
        Uri destinationUri = Uri.fromFile(destinationFile);

        UCrop.Options options = new UCrop.Options();
        options.setCircleDimmedLayer(true); // Это делает обрезку под круг
        options.setToolbarTitle("Обрежьте фото"); // Название инструмента
        options.setToolbarColor(getResources().getColor(R.color.black)); // Цвет панели
        options.setStatusBarColor(getResources().getColor(R.color.white)); // Цвет статус-бара
        options.setToolbarColor(getResources().getColor(R.color.blue));
        ///options.setFreeStyleCropEnabled(true);///

        UCrop.of(sourceUri, destinationUri)
                .withOptions(options)
                .start(requireActivity(), UCrop.REQUEST_CROP);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}