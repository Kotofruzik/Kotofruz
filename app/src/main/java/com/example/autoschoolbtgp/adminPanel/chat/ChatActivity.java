package com.example.autoschoolbtgp.adminPanel.chat;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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
import com.parse.ParseUser;

// 👇 Добавляем имплементацию ChatManager.ChatCallback
public class ChatActivity extends AppCompatActivity implements ChatManager.ChatCallback {
    private static final String TAG = "ChatActivity_SENIOR";
    private ActivityChatBinding binding;
    private ChatViewModel viewModel;
    private MessageAdapter adapter;
    private String targetUserId; // ID собеседника
    private String currentChatId; // будем использовать как уникальный ID чата

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Обработка WindowInsets
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Получаем targetUserId из Intent
        targetUserId = getIntent().getStringExtra("targetUserId");
        if (targetUserId == null) {
            String errorMsg = "Ошибка: не указан ID пользователя";
            Log.e(TAG, "onCreate: " + errorMsg);
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        String currentUserId = ParseUser.getCurrentUser().getObjectId();
        if (currentUserId == null) {
            Toast.makeText(this, "Ошибка: пользователь не авторизован", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // 👇 Формируем уникальный chatId (например, отсортированная пара ID)
        // Это нужно, чтобы один и тот же чат был на обоих устройствах
        String id1 = currentUserId.compareTo(targetUserId) < 0 ? currentUserId : targetUserId;
        String id2 = currentUserId.compareTo(targetUserId) < 0 ? targetUserId : currentUserId;
        currentChatId = id1 + "_" + id2;

        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        adapter = new MessageAdapter(currentUserId);

        RecyclerView recyclerView = binding.recyclerViewMessages;
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        // 🔻 УДАЛЯЕМ WebSocket-подключение
        // Было: viewModel.connectToChat(targetUserId);

        // Подписываемся на события от ChatManager (для silent push)
        ChatManager.getInstance().setCallback(this);

        // Наблюдение за сообщениями из ViewModel (загружаются при старте)
        viewModel.getMessages().observe(this, messages -> {
            if (messages != null) {
                Log.d(TAG, "onCreate -> messagesLiveData: Получено " + messages.size() + " сообщений");
                adapter.updateMessages(messages);
                if (!messages.isEmpty()) {
                    recyclerView.scrollToPosition(messages.size() - 1);
                }
            } else {
                Log.w(TAG, "onCreate -> messagesLiveData: Получен null вместо списка сообщений");
            }
        });

        viewModel.getError().observe(this, error -> {
            if (error != null) {
                Log.e(TAG, "onCreate -> errorLiveData: ОШИБКА: " + error);
                Toast.makeText(ChatActivity.this, "Ошибка: " + error, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getSuccessMessage().observe(this, message -> {
            if (message != null) {
                Log.d(TAG, "onCreate -> successMessageLiveData: СООБЩЕНИЕ: " + message);
                Toast.makeText(ChatActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading != null) {
                Log.d(TAG, "onCreate -> isLoadingLiveData: Состояние загрузки изменилось на: " + isLoading);
                binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });

        setupClickListeners();

        // Загружаем существующие сообщения
        Log.d(TAG, "onCreate: Загрузка сообщений для чата: " + currentChatId);
        viewModel.loadMessagesForChat(currentChatId); // ⚠️ изменили метод — см. ниже
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

            // Отправляем сообщение в чат
            Log.d(TAG, "btnSend: Отправка сообщения в чат: " + currentChatId);
            viewModel.sendMessageToChat(currentChatId, text, targetUserId); // ⚠️ обновили сигнатуру
            binding.editTextMessage.setText("");
        });

        binding.btnBack.setOnClickListener(v -> {
            Log.d(TAG, "btnBack: Нажата кнопка 'Назад'.");
            finish();
        });
    }

    // 👇 Реализация ChatManager.ChatCallback
    @Override
    public void onNewMessage(String chatId) {
        // Проверяем, что пришло сообщение именно в наш чат
        if (currentChatId.equals(chatId)) {
            Log.d(TAG, "onNewMessage: Получено новое сообщение для текущего чата — обновляем.");
            viewModel.loadMessagesForChat(currentChatId);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Уничтожение Activity.");
        ChatManager.getInstance().setCallback(null); // отписываемся
        binding = null;
    }
}