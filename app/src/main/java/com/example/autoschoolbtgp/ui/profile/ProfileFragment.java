package com.example.autoschoolbtgp.ui.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.autoschoolbtgp.LoginActivity;
import com.example.autoschoolbtgp.databinding.FragmentProfileBinding;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Инициализация лаунчера для выбора фото
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                Uri selectedImageUri = result.getData().getData();
                if (selectedImageUri != null) {
                    try {
                        // Загрузка изображения в ImageView
                        binding.profileImage.setImageURI(selectedImageUri);

                        // Конвертация в Bitmap -> byte[] для Parse
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), selectedImageUri);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos); // Сжимаем до 80%
                        byte[] imageBytes = baos.toByteArray();

                        // Передаем фото в ViewModel
                        viewModel.setNewPhotoBytes(imageBytes);
                    } catch (IOException e) {
                        Log.e("ProfileFragment", "Ошибка при загрузке фото", e);
                        Toast.makeText(requireContext(), "Ошибка загрузки фото", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
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
            imagePickerLauncher.launch(intent);
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
            startActivity(new Intent(requireContext(), LoginActivity.class));
            requireActivity().finish();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}