package com.example.autoschoolbtgp.adminPanel.chats;

import android.content.Intent;
import android.os.Bundle;
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

import com.example.autoschoolbtgp.adminPanel.chat.ChatActivity;
import com.example.autoschoolbtgp.databinding.FragmentChatsBinding;
import com.parse.ParseUser;

public class ChatsListFragment extends Fragment {

    private static final String TAG = "ChatsListFragment_SENIOR";
    private FragmentChatsBinding binding;
    private ChatsViewModel viewModel;
    private ChatsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChatsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        setupViewModel();
    }

    private void setupRecyclerView() {
        adapter = new ChatsAdapter(chat -> {
            String currentUserId = ParseUser.getCurrentUser().getObjectId();
            String senderId = chat.getSenderId();
            String receiverId = chat.getReceiverId();

            if (senderId == null || receiverId == null || currentUserId == null) {
                Log.e(TAG, "Невозможно определить собеседника: senderId=" + senderId + ", receiverId=" + receiverId + ", currentUserId=" + currentUserId);
                Toast.makeText(requireContext(), "Ошибка: не удалось определить собеседника", Toast.LENGTH_SHORT).show();
                return;
            }

            // Определяем собеседника
            String targetUserId = senderId.equals(currentUserId) ? receiverId : senderId;

            Log.d(TAG, "Открытие чата с пользователем: " + targetUserId);
            Intent intent = new Intent(requireContext(), ChatActivity.class);
            intent.putExtra("targetUserId", targetUserId);
            startActivity(intent);
        });

        binding.recyclerViewChats.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewChats.setAdapter(adapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(ChatsViewModel.class);

        // Загрузка
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                if (isLoading) {
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.recyclerViewChats.setVisibility(View.GONE);
                } else {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.recyclerViewChats.setVisibility(View.VISIBLE);
                }
            }
        });

        // Список чатов
        viewModel.getChats().observe(getViewLifecycleOwner(), chats -> {
            if (chats != null) {
                Log.d(TAG, "Получено " + chats.size() + " чатов");
                adapter.updateChats(chats);
            }
        });

        // Ошибки
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Log.e(TAG, "Ошибка загрузки чатов: " + error);
                Toast.makeText(requireContext(), "Ошибка: " + error, Toast.LENGTH_LONG).show();
                binding.progressBar.setVisibility(View.GONE);
                binding.recyclerViewChats.setVisibility(View.VISIBLE);
            }
        });

        // Загружаем чаты
        viewModel.loadChats();
    }
}