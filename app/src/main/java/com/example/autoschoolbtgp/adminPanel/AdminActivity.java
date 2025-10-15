package com.example.autoschoolbtgp.adminPanel;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.autoschoolbtgp.InstructorActivity;
import com.example.autoschoolbtgp.LoginActivity;
import com.example.autoschoolbtgp.R;
import com.example.autoschoolbtgp.StudentActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.autoschoolbtgp.databinding.ActivityAdminBinding;

import com.parse.ParseUser;

public class AdminActivity extends AppCompatActivity {

    private ActivityAdminBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_admin);

        NavigationUI.setupWithNavController(navView, navController);
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
                        if (!"admin".equals(role)) {
                            // Если роль изменилась и пользователь больше не админ, направляем на нужный экран
                            Intent intent;
                            switch (role) {
                                case "instructor":
                                    intent = new Intent(this, InstructorActivity.class);
                                    break;
                                case "student":
                                    intent = new Intent(this, StudentActivity.class);
                                    break;
                                default:
                                    // Если роль непредусмотренная, можно вернуть на вход или общий экран
                                    intent = new Intent(this, LoginActivity.class);
                                    break;
                            }
                            Toast.makeText(this, "Ваша роль изменилась: " + role + ". Перенаправляем...", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                            finish();
                        }
                        // Текущая роль admin — остаёмся здесь
                    }
                }
            });
        }
    }
}

