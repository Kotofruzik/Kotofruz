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

import com.example.autoschoolbtgp.R;
import com.example.autoschoolbtgp.adminPanel.chat.ChatActivity;
import com.example.autoschoolbtgp.databinding.FragmentChatsListBinding;

import java.util.List;

public class ChatsListFragment extends Fragment {
    private static final String TAG = "ChatsListFragment_SENIOR";
    private FragmentChatsListBinding binding;
    private ChatsListViewModel viewModel;
    private ChatsListAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChatsListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        viewModel = new ViewModelProvider(this).get(ChatsListViewModel.class);
        adapter = new ChatsListAdapter(new ChatsListAdapter.OnChatListClickListener() {
            @Override
            public void onChatClick(ChatListModel chat) {
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
                    binding.textViewNoChats.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    binding.textViewNoChats.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            } else {
                Log.w(TAG, "onCreateView -> chatsLiveData: Получен null вместо списка чатов");
                binding.textViewNoChats.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });

        // --- Наблюдение за ошибками ---
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Log.e(TAG, "onCreateView -> errorLiveData: ОШИБКА: " + error);
                Toast.makeText(requireContext(), "Ошибка: " + error, Toast.LENGTH_LONG).show();
            }
        });

        // --- Наблюдение за сообщениями об успехе ---
        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Log.d(TAG, "onCreateView -> successMessageLiveData: СООБЩЕНИЕ: " + message);
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                Log.d(TAG, "onCreateView -> isLoadingLiveData: Состояние загрузки изменилось на: " + isLoading);
                binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });

        Log.d(TAG, "onCreateView: Запуск загрузки чатов из ViewModel.");
        viewModel.loadChats();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}