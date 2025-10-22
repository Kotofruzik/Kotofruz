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
import com.example.autoschoolbtgp.databinding.FragmentChatsBinding;
import com.example.autoschoolbtgp.adminPanel.chat.ChatActivity;

import java.util.List;

public class ChatsFragment extends Fragment {
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
        adapter = new ChatsAdapter(new ChatsAdapter.OnChatActionsListener() {
            @Override
            public void onOpenChat(ChatModel chat) {
                Intent intent = new Intent(requireContext(), ChatActivity.class);
                intent.putExtra("chatId", chat.getId());
                startActivity(intent);
            }

            @Override
            public void onDeleteChat(ChatModel chat) {
                // TODO: Реализовать удаление чата
            }
        });

        RecyclerView recyclerView = binding.recyclerViewChats;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        viewModel.getChats().observe(getViewLifecycleOwner(), chats -> {
            if (chats != null) {
                adapter.updateChats(chats);
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

        viewModel.loadChats();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}