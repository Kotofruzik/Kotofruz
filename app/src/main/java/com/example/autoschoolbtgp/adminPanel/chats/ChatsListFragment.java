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

public class ChatsListFragment extends Fragment {
    private static final String TAG = "ChatsFragment_SENIOR";
    private FragmentChatsBinding binding;
    private ChatsViewModel viewModel;
    private ChatsAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChatsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        viewModel = new ViewModelProvider(this).get(ChatsViewModel.class);
        adapter = new ChatsAdapter(new ChatsAdapter.OnChatClickListener() {
            @Override
            public void onChatClick(ChatModel chat) {
                Log.d(TAG, "onChatClick: Нажат чат с ID: " + chat.getId());
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("chatId", chat.getId());
                startActivity(intent);
            }
        });

        RecyclerView recyclerView = binding.recyclerViewChats;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        // --- Наблюдение за чатами ---
        viewModel.getChats().observe(getViewLifecycleOwner(), chats -> {
            if (chats != null) {
                Log.d(TAG, "onCreateView -> chatsLiveData: Получено " + chats.size() + " чатов");
                adapter.updateChats(chats);
                // Если чатов нет - показываем сообщение
                if (chats.isEmpty()) {
                    Log.d(TAG, "onCreateView -> chatsLiveData: Список чатов пуст.");
                    binding.textViewNoChats.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    Log.d(TAG, "onCreateView -> chatsLiveData: Список чатов не пуст.");
                    binding.textViewNoChats.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            } else {
                Log.w(TAG, "onCreateView -> chatsLiveData: Получен null вместо списка чатов");
                binding.textViewNoChats.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Log.e(TAG, "onCreateView -> errorLiveData: ОШИБКА: " + error);
                Toast.makeText(requireContext(), "Ошибка: " + error, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Log.d(TAG, "onCreateView -> successMessageLiveData: СООБЩЕНИЕ: " + message);
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        // --- Наблюдение за состоянием загрузки ---
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                Log.d(TAG, "onCreateView -> isLoadingLiveData: Состояние загрузки изменилось на: " + isLoading);
                binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });
        // --- Конец наблюдения ---

        // --- Загрузка чатов ---
        Log.d(TAG, "onCreateView: Запуск загрузки чатов из ViewModel.");
        viewModel.loadChats();
        // --- Конец загрузки чатов ---

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: Уничтожение представления фрагмента.");
        binding = null;
    }
}