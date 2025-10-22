package com.example.autoschoolbtgp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.parse.ParseFile;
import com.parse.ParseUser;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import de.hdodenhof.circleimageview.CircleImageView;

import com.example.autoschoolbtgp.adminPanel.AdminActivity;
import com.example.autoschoolbtgp.databinding.ActivityStudentBinding;

public class StudentActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityStudentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityStudentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        View rootView = binding.getRoot();
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setSupportActionBar(binding.appBarStudentActivityTwo.toolbar);
        binding.appBarStudentActivityTwo.fab.setOnClickListener(view -> {
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .setAnchorView(R.id.fab)
                    .show();
        });

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_student_activity_two);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUserRoleAndRedirect();
    }

    private void checkUserRoleAndRedirect() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            redirectToLogin();
            return;
        }

        currentUser.fetchInBackground((userObj, e) -> {
            if (e != null || userObj == null) {
                runOnUiThread(this::redirectToLogin);
                return;
            }

            String role = userObj.getString("role");
            if (role == null || role.isEmpty()) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Роль пользователя не установлена", Toast.LENGTH_SHORT).show();
                    redirectToLogin();
                });
                return;
            }

            if (!"student".equals(role)) {
                Intent intent;
                if ("admin".equals(role)) {
                    intent = new Intent(this, AdminActivity.class);
                } else if ("instructor".equals(role)) {
                    intent = new Intent(this, InstructorActivity.class);
                } else {
                    intent = new Intent(this, LoginActivity.class);
                }
                runOnUiThread(() -> {
                    Toast.makeText(this, "Ваша роль: " + role + ". Перенаправляем...", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    finish();
                });
                return;
            }

            // ✅ Пользователь — студент → обновляем шапку
            runOnUiThread(this::updateNavHeaderWithUser);
        });
    }

    private void updateNavHeaderWithUser() {
        ParseUser user = ParseUser.getCurrentUser();
        if (user == null) return; // на всякий случай

        NavigationView navigationView = binding.navView;
        View headerView = navigationView.getHeaderView(0);

        CircleImageView navAvatar = headerView.findViewById(R.id.navAvatar);
        TextView navName = headerView.findViewById(R.id.navName);
        TextView navEmail = headerView.findViewById(R.id.navEmail);

        // === Имя и фамилия ===
        String firstName = user.getString("firstName");
        String lastName = user.getString("lastName");

        String fullName = "";
        if (firstName != null && !firstName.trim().isEmpty()) {
            fullName = firstName.trim();
            if (lastName != null && !lastName.trim().isEmpty()) {
                fullName += " " + lastName.trim();
            }
        } else if (lastName != null && !lastName.trim().isEmpty()) {
            fullName = lastName.trim();
        } else {
            fullName = user.getUsername(); // fallback
        }

        navName.setText(fullName);

        // === Email ===
        String email = user.getEmail();
        navEmail.setText(email != null ? email : "");

        // === Аватар ===
        ParseFile photoFile = user.getParseFile("photo"); // ← поле "photo"
        String photoUrl = photoFile != null ? photoFile.getUrl() : null;

        if (photoUrl != null && !photoUrl.isEmpty()) {
            Glide.with(this)
                    .load(photoUrl)
                    .placeholder(R.drawable.username_icon)
                    .error(R.drawable.username_icon)
                    .into(navAvatar);
        } else {
            navAvatar.setImageResource(R.drawable.username_icon);
        }
    }

    private void redirectToLogin() {
        Toast.makeText(this, "Требуется авторизация", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.student_activity, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_student_activity_two);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}