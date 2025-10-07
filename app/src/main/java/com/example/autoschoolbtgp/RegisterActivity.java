package com.example.autoschoolbtgp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class RegisterActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1001;

    private EditText lastNameEditText, firstNameEditText, middleNameEditText, emailEditText, passwordEditText;
    private ImageView photoImageView;
    private Button selectPhotoButton, registerButton;
    private ImageButton backButton;

    private Uri selectedPhotoUri = null;
    private byte[] selectedPhotoBytes = null;

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

        selectPhotoButton.setOnClickListener(v -> openImageChooser());

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
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedPhotoUri = data.getData();
            photoImageView.setImageURI(selectedPhotoUri);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedPhotoUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int quality = 90;
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
                while (baos.toByteArray().length > 1024 * 1024 && quality > 10) {
                    baos.reset();
                    quality -= 10;
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
                }
                selectedPhotoBytes = baos.toByteArray();
            } catch (IOException e) {
                Toast.makeText(this, "Ошибка обработки изображения: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
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
                    if (selectedPhotoBytes != null) {
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
        photoFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    user.put("photo", photoFile);
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                onRegistrationSuccess();
                            } else {
                                Toast.makeText(RegisterActivity.this, "Ошибка сохранения фото: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(RegisterActivity.this, "Ошибка загрузки фото: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void onRegistrationSuccess() {
        Toast.makeText(RegisterActivity.this, "Регистрация прошла успешно", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(RegisterActivity.this, StudentActivity.class));
        finish();
    }
}
