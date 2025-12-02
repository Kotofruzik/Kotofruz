package com.example.autoschoolbtgp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.autoschoolbtgp.adminPanel.AdminActivity;
import com.parse.ParseUser;

import com.parse.ParseCloud;
import com.parse.FunctionCallback;
import com.parse.ParseException;
import java.util.HashMap;
import java.util.Map;

public class InstructorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_instructor);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnSendTestPush = findViewById(R.id.btnSendTestPush);
        btnSendTestPush.setOnClickListener(v -> sendTestPush());
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUserRoleAndRedirect();
    }

    private void checkUserRoleAndRedirect() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            currentUser.fetchInBackground((userObject, e) -> {
                if (e == null && userObject != null) {
                    ParseUser user = (ParseUser) userObject;
                    String role = user.getString("role");
                    if (role != null) {
                        if (!"instructor".equals(role)) {
                            // Если роль изменилась и пользователь больше не instructor, направляем на нужный экран
                            Intent intent;
                            switch (role) {
                                case "admin":
                                    intent = new Intent(this, AdminActivity.class);
                                    break;
                                case "student":
                                    intent = new Intent(this, StudentActivity.class);
                                    break;
                                default:
                                    intent = new Intent(this, LoginActivity.class);
                                    break;
                            }
                            Toast.makeText(this, "Ваша роль изменилась: " + role + ". Перенаправляем...", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                            finish();
                        }
                    }
                }
            });
        }
    }

    private void sendTestPush() {
        Map<String, Object> params = new HashMap<>(); // без параметров

        ParseCloud.callFunctionInBackground("sendTestPushToAll", params,
                (result, e) -> {
                    if (e == null) {
                        runOnUiThread(() ->
                                Toast.makeText(this,
                                        "Тестовый push отправлен",
                                        Toast.LENGTH_SHORT).show()
                        );
                    } else {
                        e.printStackTrace();
                        runOnUiThread(() ->
                                Toast.makeText(this,
                                        "Ошибка отправки push: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show()
                        );
                    }
                });
    }
}
