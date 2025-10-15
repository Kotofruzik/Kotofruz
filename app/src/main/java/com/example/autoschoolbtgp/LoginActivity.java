package com.example.autoschoolbtgp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.autoschoolbtgp.adminPanel.AdminActivity;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            openNextActivity(currentUser);
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.email_et);
        passwordEditText = findViewById(R.id.password_et);
        loginButton = findViewById(R.id.login_btn);
        registerButton = findViewById(R.id.register_btn);

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(LoginActivity.this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
            } else {
                loginUser(email, password);
            }
        });

        registerButton.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void loginUser(String email, String password) {
        ParseUser.logInInBackground(email, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    Toast.makeText(LoginActivity.this, "Вход выполнен успешно", Toast.LENGTH_SHORT).show();
                    openNextActivity(user);
                    finish();
                } else if (e != null && e.getCode() == ParseException.INVALID_SESSION_TOKEN) {
                    Toast.makeText(LoginActivity.this, "Сессия истекла, войдите заново", Toast.LENGTH_LONG).show();
                    ParseUser.logOut();
                    Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Ошибка входа: " + (e != null ? e.getMessage() : "неизвестная ошибка"), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void openNextActivity(ParseUser user) {
        String role = user.getString("role");
        if ("admin".equals(role)) {
            startActivity(new Intent(this, AdminActivity.class));
        } else if ("instructor".equals(role)) {
            startActivity(new Intent(this, InstructorActivity.class));
        } else {
            startActivity(new Intent(this, StudentActivityTwo.class));
        }
    }
}
