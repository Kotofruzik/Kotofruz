package com.example.autoschoolbtgp.adminPanel.chat;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autoschoolbtgp.R;
import com.example.autoschoolbtgp.databinding.ActivityChatBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.parse.ParseUser;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity_SENIOR";
    private ActivityChatBinding binding;
    private ChatViewModel viewModel;
    private MessageAdapter adapter;
    private String targetUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        targetUserId = getIntent().getStringExtra("targetUserId");
        if (targetUserId == null) {
            Toast.makeText(this, "Ошибка: не указан ID пользователя", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        String currentUserId = ParseUser.getCurrentUser().getObjectId();
        if (currentUserId == null) {
            Toast.makeText(this, "Ошибка: пользователь не авторизован", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        adapter = new MessageAdapter(currentUserId);

        RecyclerView recyclerView = binding.recyclerViewMessages;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Наблюдение за сообщениями
        viewModel.getMessages().observe(this, messages -> {
            if (messages != null) {
                adapter.updateMessages(messages);
                if (!messages.isEmpty()) {
                    recyclerView.scrollToPosition(messages.size() - 1);
                }
            }
        });

        viewModel.getError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(ChatActivity.this, "Ошибка: " + error, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getSuccessMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(ChatActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        setupClickListeners();
        viewModel.loadMessagesForUser(targetUserId); // Старый метод: по targetUserId

        // Фокус на поле ввода при запуске
        binding.editTextMessage.requestFocus();

        // Показать клавиатуру
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    private void setupClickListeners() {
        binding.btnSend.setOnClickListener(v -> {
            String text = binding.editTextMessage.getText().toString().trim();
            if (TextUtils.isEmpty(text)) {
                Toast.makeText(this, "Введите сообщение", Toast.LENGTH_SHORT).show();
                return;
            }
            viewModel.sendMessageToUser(targetUserId, text); // Старый метод
            binding.editTextMessage.setText("");
        });

        binding.btnBack.setOnClickListener(v -> finish());
    }
}
