package com.example.autoschoolbtgp;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.autoschoolbtgp.adminPanel.AdminActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView registerButton;
    private static final String TAG = "LoginActivity";
    private static final int NOTIFICATION_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 🎯 ДОБАВЬТЕ ЭТО ПЕРВОЙ СТРОКОЙ
        Log.d(TAG, "=================== ЗАПУСК ПРИЛОЖЕНИЯ ===================");
        Log.d(TAG, "onCreate вызван");

        // Проверяем, есть ли уже залогиненный пользователь
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            Log.d(TAG, "Пользователь уже авторизован, переход...");
            openNextActivity(currentUser);
            finish();
            return;
        }

        setContentView(R.layout.activity_login);
        Log.d(TAG, "Layout загружен");

        // 🔥 ВЫЗОВИТЕ ЭТОТ МЕТОД ПРЯМО ЗДЕСЬ
        testFirebaseImmediately();

        // Инициализация Firebase FCM
        initFirebaseFCM();

        // Запрос разрешений для уведомлений (Android 13+)
        requestNotificationPermission();

        // Находим элементы UI
        emailEditText = findViewById(R.id.email_et);
        passwordEditText = findViewById(R.id.password_et);
        loginButton = findViewById(R.id.login_btn);
        registerButton = findViewById(R.id.register_btn);

        // Обработчик кнопки входа
        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(LoginActivity.this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
            } else {
                loginUser(email, password);
            }
        });

        // Обработчик кнопки регистрации
        registerButton.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        /**registerButton.setText("ПРЯМОЕ УВЕДОМЛЕНИЕ");
        registerButton.setOnClickListener(v -> {
            Log.d("LoginActivity", "Прямой вызов уведомления...");

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            String channelId = "test_channel";

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        channelId,
                        "Тестовый канал",
                        NotificationManager.IMPORTANCE_HIGH
                );
                manager.createNotificationChannel(channel);
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.notifications)
                    .setContentTitle("Прямой тест")
                    .setContentText("Уведомление без WorkManager")
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            manager.notify(999, builder.build());
            Log.d("LoginActivity", "Прямое уведомление отправлено!!!");
            Toast.makeText(this, "Прямое уведомление отправлено", Toast.LENGTH_SHORT).show();
        });*/
    }

    /**
     * 🔥 НОВЫЙ МЕТОД: Тест Firebase сразу при запуске
     */
    private void testFirebaseImmediately() {
        Log.d(TAG, "🔥 ТЕСТ FIREBASE: запускаем...");

        try {
            // Проверяем инициализацию Firebase
            Log.d(TAG, "Проверяем FirebaseApp...");
            if (FirebaseApp.getApps(this).isEmpty()) {
                Log.e(TAG, "❌ FirebaseApp не инициализирован!");
                Toast.makeText(this, "Firebase не инициализирован", Toast.LENGTH_LONG).show();
            } else {
                Log.d(TAG, "✅ FirebaseApp инициализирован");
            }

            // Пробуем получить токен сразу
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(task -> {
                        Log.d(TAG, "🔥 Тест токена завершен");
                        if (!task.isSuccessful()) {
                            Log.e(TAG, "❌ ТЕСТ: Ошибка получения токена", task.getException());
                            Toast.makeText(LoginActivity.this,
                                    "Ошибка Firebase: " + (task.getException() != null ?
                                            task.getException().getMessage() : "unknown"),
                                    Toast.LENGTH_LONG).show();
                        } else {
                            String token = task.getResult();
                            Log.d(TAG, "✅ ТЕСТ: Токен получен! Первые 30 символов: "
                                    + token.substring(0, Math.min(30, token.length())));
                            Toast.makeText(LoginActivity.this,
                                    "Токен получен! Проверьте Logcat",
                                    Toast.LENGTH_LONG).show();
                        }
                    });

        } catch (Exception e) {
            Log.e(TAG, "❌ Исключение при тесте Firebase: ", e);
            Toast.makeText(this, "Exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Инициализация Firebase Cloud Messaging
     */
    private void initFirebaseFCM() {
        Log.d(TAG, "🎯 initFirebaseFCM вызван");

        // Получаем FCM токен устройства
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    Log.d(TAG, "🎯 Обработчик токена вызван, успешно: " + task.isSuccessful());

                    if (!task.isSuccessful()) {
                        Log.e(TAG, "❌ НЕ удалось получить FCM токен", task.getException());
                        if (task.getException() != null) {
                            task.getException().printStackTrace();
                        }
                        return;
                    }

                    String token = task.getResult();
                    Log.d(TAG, "✅ FCM Token получен! Длина: " + token.length());
                    Log.d(TAG, "✅ FCM Token (первые 50 символов): " +
                            token.substring(0, Math.min(50, token.length())));

                    // Также выводим в консоль Android Studio
                    System.out.println("🎯 FCM TOKEN: " + token);

                    // Сохраняем токен в SharedPreferences
                    saveFcmTokenToPreferences(token);
                });

        // Подписываемся на общую тему для автошколы
        FirebaseMessaging.getInstance().subscribeToTopic("autoschool")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "✅ Успешно подписались на тему 'autoschool'");
                    } else {
                        Log.w(TAG, "⚠️ Не удалось подписаться на тему", task.getException());
                    }
                });
    }

    /**
     * Запрос разрешения на показ уведомлений (Android 13+)
     */
    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Проверяем, есть ли уже разрешение
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != android.content.pm.PackageManager.PERMISSION_GRANTED) {

                // Показываем объяснение перед запросом (опционально)
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.POST_NOTIFICATIONS)) {
                    Toast.makeText(this,
                            "Для получения уведомлений о занятиях и напоминаний требуется разрешение",
                            Toast.LENGTH_LONG).show();
                }

                // Запрашиваем разрешение
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_CODE);
            }
        }
    }

    /**
     * Обработка ответа на запрос разрешений
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Разрешение на уведомления получено");
            } else {
                Log.d(TAG, "Разрешение на уведомления отклонено");
                Toast.makeText(this,
                        "Вы не будете получать push-уведомления. Разрешение можно включить в настройках.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Сохранение FCM токена в SharedPreferences
     */
    private void saveFcmTokenToPreferences(String token) {
        getSharedPreferences("app_prefs", MODE_PRIVATE)
                .edit()
                .putString("fcm_token", token)
                .apply();
        Log.d(TAG, "FCM токен сохранен локально");
    }

    /**
     * Отправка FCM токена на сервер Parse
     */
    private void sendTokenToParseServer(String token) {
        // Этот метод будет вызван после успешного логина
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            currentUser.put("fcmToken", token);
            currentUser.saveInBackground(e -> {
                if (e == null) {
                    Log.d(TAG, "FCM токен успешно сохранен на сервере Parse");
                } else {
                    Log.e(TAG, "Ошибка сохранения FCM токена на сервере: " + e.getMessage());
                }
            });
        }
    }

    private void loginUser(String email, String password) {
        ParseUser.logInInBackground(email, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    Toast.makeText(LoginActivity.this, "Вход выполнен успешно", Toast.LENGTH_SHORT).show();

                    // Получаем сохраненный FCM токен и отправляем на сервер
                    String savedToken = getSharedPreferences("app_prefs", MODE_PRIVATE)
                            .getString("fcm_token", "");

                    if (!savedToken.isEmpty()) {
                        sendTokenToParseServer(savedToken);
                    }

                    // Открываем соответствующую активность
                    openNextActivity(user);
                    finish();
                } else if (e != null && e.getCode() == ParseException.INVALID_SESSION_TOKEN) {
                    Toast.makeText(LoginActivity.this, "Сессия истекла, войдите заново", Toast.LENGTH_LONG).show();
                    ParseUser.logOut();
                    Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this,
                            "Ошибка входа: " + (e != null ? e.getMessage() : "неизвестная ошибка"),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void openNextActivity(ParseUser user) {
        String role = user.getString("role");
        Intent intent;

        if ("admin".equals(role)) {
            intent = new Intent(this, AdminActivity.class);
        } else if ("instructor".equals(role)) {
            intent = new Intent(this, InstructorActivity.class);
        } else {
            intent = new Intent(this, StudentActivity.class);
        }

        // Передаем FCM токен в следующую активность (опционально)
        String fcmToken = getSharedPreferences("app_prefs", MODE_PRIVATE)
                .getString("fcm_token", "");
        if (!fcmToken.isEmpty()) {
            intent.putExtra("fcm_token", fcmToken);
        }

        startActivity(intent);
    }

    private void checkFirebaseConfig() {
        try {
            Log.d(TAG, "Проверяем google-services.json...");

            String senderId = getString(R.string.gcm_defaultSenderId);
            String projectId = getString(R.string.project_id);

            Log.d(TAG, "SenderId: " + senderId);
            Log.d(TAG, "ProjectId: " + projectId);
        } catch (Exception e) {
            Log.e(TAG, "Ошибка конфигурации Firebase: ", e);
        }
    }
}