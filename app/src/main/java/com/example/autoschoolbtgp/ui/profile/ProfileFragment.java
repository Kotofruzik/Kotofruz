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
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.autoschoolbtgp.R;
import com.example.autoschoolbtgp.databinding.FragmentProfileBinding;
import com.example.autoschoolbtgp.ui.users.UserModel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment_SENIOR";
    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;

    // Лаунчер для выбора фото из галереи
    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        handleSelectedImage(selectedImageUri);
                    }
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

        // Загрузка текущих данных
        viewModel.loadCurrentUser();

        // Обновление UI из ViewModel
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
                Log.e(TAG, "Ошибка: " + error);
                Toast.makeText(requireContext(), "Ошибка: " + error, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Log.d(TAG, "Сообщение: " + message);
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        setupClickListeners();

        return view;
    }

    private void setupClickListeners() {
        binding.btnChangePhoto.setOnClickListener(v -> {
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
            // TODO: Добавьте навигацию на экран входа
            // Intent intent = new Intent(requireContext(), LoginActivity.class);
            // startActivity(intent);
            // requireActivity().finish();
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void handleSelectedImage(Uri selectedImageUri) {
        Log.d(TAG, "handleSelectedImage: Начало обработки выбранного изображения. URI: " + selectedImageUri);
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), selectedImageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos);
            byte[] imageBytes = baos.toByteArray();
            Log.d(TAG, "handleSelectedImage: Изображение конвертировано в byte[], размер: " + imageBytes.length + " байт.");

            // Передаем байты в ViewModel
            viewModel.setNewPhotoBytes(imageBytes);

            // Обновляем изображение в UI сразу после выбора
            binding.profileImage.setImageURI(selectedImageUri);

            // --- ВАЖНО: Вызываем updateProfile сразу после выбора фото ---
            String firstName = binding.editFirstName.getText().toString().trim();
            String lastName = binding.editLastName.getText().toString().trim();
            String middleName = binding.editMiddleName.getText().toString().trim();
            String password = binding.editPassword.getText().toString().trim();

            viewModel.updateProfile(firstName, lastName, middleName, password);
            // ---

        } catch (IOException e) {
            Log.e(TAG, "Ошибка при обработке выбранного изображения", e);
            Toast.makeText(requireContext(), "Ошибка обработки фото: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}