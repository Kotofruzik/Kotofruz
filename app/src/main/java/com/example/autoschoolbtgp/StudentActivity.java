package com.example.autoschoolbtgp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.autoschoolbtgp.adminPanel.AdminActivity;
import com.parse.ParseUser;

public class StudentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUserRoleAndRedirect();
    }

    private void checkUserRoleAndRedirect() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            currentUser.fetchInBackground((userObj, e) -> {
                if (e == null && userObj != null) {
                    ParseUser user = (ParseUser) userObj;
                    String role = user.getString("role");
                    if (role != null) {
                        if (!"student".equals(role)) {
                            Intent intent;
                            switch (role) {
                                case "admin":
                                    intent = new Intent(this, AdminActivity.class);
                                    break;
                                case "instructor":
                                    intent = new Intent(this, InstructorActivity.class);
                                    break;
                                default:
                                    intent = new Intent(this, LoginActivity.class);
                                    break;
                            }
                            Toast.makeText(this, "Ваша роль изменилась на: " + role + ". Перенаправляем...", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                            finish();
                        }
                        // если роль student — остаемся здесь
                    }
                }
            });
        }
    }
}
