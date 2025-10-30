package com.example.autoschoolbtgp.adminPanel.chat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.autoschoolbtgp.R;
import com.example.autoschoolbtgp.databinding.ActivityChatBinding;

import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity_SENIOR";
    private ActivityChatBinding binding;
    private ChatViewModel viewModel;
    private MessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Обработка WindowInsets (для Edge-to-Edge)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        adapter = new MessageAdapter();

        RecyclerView recyclerView = binding.recyclerViewMessages;
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        // --- Наблюдение за сообщениями ---
        viewModel.getMessages().observe(this, messages -> {
            if (messages != null) {
                Log.d(TAG, "onCreate -> messagesLiveData: Получено " + messages.size() + " сообщений");
                adapter.updateMessages(messages);
                // Прокручиваем вниз
                recyclerView.scrollToPosition(messages.size() - 1);
            } else {
                Log.w(TAG, "onCreate -> messagesLiveData: Получен null вместо списка сообщений");
            }
        });

        // --- Наблюдение за ошибками ---
        viewModel.getError().observe(this, error -> {
            if (error != null) {
                Log.e(TAG, "onCreate -> errorLiveData: ОШИБКА: " + error);
                Toast.makeText(ChatActivity.this, "Ошибка: " + error, Toast.LENGTH_LONG).show();
            }
        });

        // --- Наблюдение за сообщениями об успехе ---
        viewModel.getSuccessMessage().observe(this, message -> {
            if (message != null) {
                Log.d(TAG, "onCreate -> successMessageLiveData: СООБЩЕНИЕ: " + message);
                Toast.makeText(ChatActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });

        // --- Наблюдение за состоянием загрузки ---
        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading != null) {
                Log.d(TAG, "onCreate -> isLoadingLiveData: Состояние загрузки изменилось на: " + isLoading);
                binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });
        // --- Конец наблюдения ---

        setupClickListeners();

        // Загрузка сообщений
        String chatId = getIntent().getStringExtra("chatId");
        if (chatId != null) {
            Log.d(TAG, "onCreate: Получен chatId из Intent: " + chatId);
            viewModel.loadMessages(chatId);
        } else {
            String errorMsg = "Ошибка: chatId не передан в Intent";
            Log.e(TAG, "onCreate: " + errorMsg);
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void setupClickListeners() {
        Log.d(TAG, "setupClickListeners: Настройка обработчиков кликов.");

        binding.btnSend.setOnClickListener(v -> {
            Log.d(TAG, "btnSend: Нажата кнопка 'Отправить сообщение'.");
            String text = binding.editTextMessage.getText().toString().trim();
            if (TextUtils.isEmpty(text)) {
                Log.w(TAG, "btnSend: Текст сообщения пуст.");
                Toast.makeText(ChatActivity.this, "Введите сообщение", Toast.LENGTH_SHORT).show();
                return;
            }

            String chatId = getIntent().getStringExtra("chatId");
            if (chatId != null) {
                Log.d(TAG, "btnSend: Отправка сообщения в чат с chatId: " + chatId);
                viewModel.sendMessage(chatId, text);
                binding.editTextMessage.setText(""); // Очищаем поле ввода
            } else {
                String errorMsg = "Ошибка: chatId не передан в Intent";
                Log.e(TAG, "btnSend: " + errorMsg);
                Toast.makeText(ChatActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });

        binding.btnBack.setOnClickListener(v -> {
            Log.d(TAG, "btnBack: Нажата кнопка 'Назад'.");
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Уничтожение Activity.");
        binding = null;
    }
}