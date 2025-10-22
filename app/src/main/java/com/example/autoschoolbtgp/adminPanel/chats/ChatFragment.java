package com.example.autoschoolbtgp.adminPanel.chats;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autoschoolbtgp.R;
import com.example.autoschoolbtgp.adminPanel.chat.MessageAdapter;
import com.example.autoschoolbtgp.databinding.FragmentChatBinding;
import com.example.autoschoolbtgp.adminPanel.chats.MessageModel;

import java.util.List;

public class ChatFragment extends Fragment {
    private static final String TAG = "ChatFragment_SENIOR";
    private FragmentChatBinding binding;
    private ChatViewModel viewModel;
    private MessageAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        adapter = new MessageAdapter();

        RecyclerView recyclerView = binding.recyclerViewMessages;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        viewModel.getMessages().observe(getViewLifecycleOwner(), messages -> {
            if (messages != null) {
                adapter.updateMessages(messages);
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Log.e(TAG, "Ошибка: " + error);
                Toast.makeText(requireContext(), "Ошибка: " + error, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Log.d(TAG, "Сообщение: " + message);
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        binding.buttonSend.setOnClickListener(v -> {
            String text = binding.editTextMessage.getText().toString().trim();
            if (!TextUtils.isEmpty(text)) {
                String chatId = getArguments().getString("chatId");
                if (chatId != null) {
                    viewModel.sendMessage(chatId, text); // <-- Передаём chatId и text
                } else {
                    Log.e("ChatFragment", "Ошибка: chatId не передан в аргументах фрагмента");
                    Toast.makeText(requireContext(), "Ошибка: chatId не передан", Toast.LENGTH_SHORT).show();
                }
                binding.editTextMessage.setText("");
            } else {
                Toast.makeText(requireContext(), "Введите сообщение", Toast.LENGTH_SHORT).show();
            }
        });

        // Загружаем сообщения
        String chatId = getArguments().getString("chatId");
        if (chatId != null) {
            viewModel.loadMessages(chatId);
        } else {
            Toast.makeText(requireContext(), "Ошибка: chatId не передан", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}