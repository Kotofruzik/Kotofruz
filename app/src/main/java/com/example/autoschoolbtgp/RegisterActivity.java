package com.example.autoschoolbtgp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageView;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class RegisterActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1001;

    private EditText lastNameEditText, firstNameEditText, middleNameEditText, emailEditText, passwordEditText;
    private ImageView photoImageView;
    private Button selectPhotoButton, registerButton;
    private ImageButton backButton;

    private Uri selectedPhotoUri = null;
    private byte[] selectedPhotoBytes = null;

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
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
                        Toast.makeText(RegisterActivity.this, "Ошибка: URI результата ImageCropper был null.", Toast.LENGTH_SHORT).show();
                    }
                } else if (!result.isSuccessful() && result.getError() == null) {
                    Toast.makeText(RegisterActivity.this, "Обрезка фото отменена", Toast.LENGTH_SHORT).show();
                } else {
                    Exception error = result.getError();
                    Toast.makeText(RegisterActivity.this, "Ошибка обрезки фото: " + (error != null ? error.getMessage() : "Неизвестная ошибка"), Toast.LENGTH_SHORT).show();
                }
                selectPhotoButton.setEnabled(true);
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        lastNameEditText = findViewById(R.id.last_name_et);
        firstNameEditText = findViewById(R.id.first_name_et);
        middleNameEditText = findViewById(R.id.middle_name_et);
        emailEditText = findViewById(R.id.email_et);
        passwordEditText = findViewById(R.id.password_et);
        photoImageView = findViewById(R.id.ivPhoto);
        selectPhotoButton = findViewById(R.id.select_photo_btn);
        registerButton = findViewById(R.id.register_btn);
        backButton = findViewById(R.id.back_btn);

        selectPhotoButton.setOnClickListener(v -> {
            selectPhotoButton.setEnabled(false);
            openImageChooser();
        });

        backButton.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });

        registerButton.setOnClickListener(v -> {
            String lastName = lastNameEditText.getText().toString().trim();
            String firstName = firstNameEditText.getText().toString().trim();
            String middleName = middleNameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (TextUtils.isEmpty(lastName) || TextUtils.isEmpty(firstName)
                    || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(RegisterActivity.this, "Пожалуйста, заполните обязательные поля", Toast.LENGTH_SHORT).show();
                return;
            }
            registerUser(lastName, firstName, middleName, email, password);
        });
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    private void startCropActivity(Uri sourceUri) {
        File destinationFile = new File(getCacheDir(), "cropped_image_senior.jpg");
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
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), croppedImageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos);
            byte[] imageBytes = baos.toByteArray();

            selectedPhotoBytes = imageBytes;
            photoImageView.setImageURI(croppedImageUri);

        } catch (IOException e) {
            Toast.makeText(this, "Ошибка обработки фото: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            selectPhotoButton.setEnabled(true);
        }
    }

    private void registerUser(String lastName, String firstName, String middleName, String email, String password) {
        ParseUser user = new ParseUser();

        user.setUsername(email);
        user.setEmail(email);
        user.setPassword(password);

        user.put("lastName", lastName);
        user.put("firstName", firstName);
        user.put("middleName", middleName);
        user.put("role", "student");

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    if (selectedPhotoBytes != null && selectedPhotoBytes.length > 0) {
                        uploadPhotoAndSaveUser(user, selectedPhotoBytes);
                    } else {
                        onRegistrationSuccess();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Ошибка регистрации: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void uploadPhotoAndSaveUser(ParseUser user, byte[] photoBytes) {
        ParseFile photoFile = new ParseFile("profile_photo.jpg", photoBytes);
        photoFile.saveInBackground((SaveCallback) e -> {
            if (e == null) {
                user.put("photo", photoFile);
                user.saveInBackground((SaveCallback) e2 -> {
                    if (e2 == null) {
                        onRegistrationSuccess();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Ошибка сохранения фото: " + e2.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                Toast.makeText(RegisterActivity.this, "Ошибка загрузки фото: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void onRegistrationSuccess() {
        Toast.makeText(RegisterActivity.this, "Регистрация прошла успешно", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(RegisterActivity.this, StudentActivity.class));
        finish();
    }
}