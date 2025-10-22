// ChatActivity.java
package com.example.autoschoolbtgp.adminPanel.chat;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
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
import com.example.autoschoolbtgp.adminPanel.chats.ChatViewModel; // <-- Используем ChatViewModel

public class ChatActivity extends AppCompatActivity {
    private ChatViewModel viewModel; // <-- Используем ChatViewModel
    private MessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        // Обработка WindowInsets (для Edge-to-Edge)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Инициализация RecyclerView, EditText, Button
        RecyclerView recyclerView = findViewById(R.id.recycler_view_messages);
        EditText editTextMessage = findViewById(R.id.edit_text_message);
        Button buttonSend = findViewById(R.id.button_send);

        // Инициализация адаптера
        adapter = new MessageAdapter(); // Убедись, что MessageAdapter создан
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Инициализация ViewModel
        viewModel = new ViewModelProvider(this).get(ChatViewModel.class); // <-- Используем ChatViewModel

        // --- Наблюдение за сообщениями ---
        viewModel.getMessages().observe(this, messages -> {
            if (messages != null) {
                adapter.updateMessages(messages);
            }
        });

        // --- Наблюдение за ошибками ---
        viewModel.getError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(ChatActivity.this, "Ошибка: " + error, Toast.LENGTH_LONG).show();
            }
        });

        // --- Наблюдение за сообщениями об успехе ---
        viewModel.getSuccessMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(ChatActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
        // --- Конец наблюдения ---

        // Загрузка сообщений
        String chatId = getIntent().getStringExtra("chatId");
        if (chatId != null) {
            viewModel.loadMessages(chatId); // <-- Вызываем loadMessages из ChatViewModel
        } else {
            Toast.makeText(this, "Ошибка: chatId не передан", Toast.LENGTH_SHORT).show();
        }

        // Отправка сообщения
        buttonSend.setOnClickListener(v -> {
            String text = editTextMessage.getText().toString().trim();
            if (!text.isEmpty() && chatId != null) {
                viewModel.sendMessage(chatId, text); // <-- Вызываем sendMessage из ChatViewModel
                editTextMessage.setText(""); // Очищаем поле ввода
            } else if (text.isEmpty()) {
                Toast.makeText(this, "Введите сообщение", Toast.LENGTH_SHORT).show();
            } else if (chatId == null) {
                Toast.makeText(this, "Ошибка: chatId не передан", Toast.LENGTH_SHORT).show();
            }
        });
    }
}