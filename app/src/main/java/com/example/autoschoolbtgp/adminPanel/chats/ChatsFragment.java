package com.example.autoschoolbtgp.adminPanel.chats; // <-- Убедись, что пакет правильный

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
import com.example.autoschoolbtgp.databinding.FragmentChatsBinding; // <-- Убедись, что binding правильный
import com.example.autoschoolbtgp.adminPanel.chat.ChatActivity; // <-- Убедись, что ChatActivity существует
import com.example.autoschoolbtgp.adminPanel.users.UserModel; // <-- Убедись, что UserModel правильный
import com.example.autoschoolbtgp.adminPanel.users.UsersAdapter; // <-- Убедись, что UsersAdapter правильный
import com.example.autoschoolbtgp.adminPanel.users.UsersViewModel; // <-- Убедись, что UsersViewModel правильный

import java.util.List;

public class ChatsFragment extends Fragment {

    private static final String TAG = "ChatsFragment_SENIOR";
    private FragmentChatsBinding binding; // <-- Убедись, что binding правильный
    private UsersViewModel viewModel; // <-- Используем UsersViewModel для загрузки пользователей
    private UsersAdapter adapter; // <-- Используем UsersAdapter для отображения

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Фрагмент создается.");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Создание представления фрагмента.");
        binding = FragmentChatsBinding.inflate(inflater, container, false); // <-- Убедись, что binding правильный
        View view = binding.getRoot();

        // --- Инициализация RecyclerView ---
        Log.d(TAG, "onCreateView: Инициализация RecyclerView.");
        RecyclerView recyclerView = binding.recyclerViewChats; // <-- Убедись, что ID правильный
        adapter = new UsersAdapter(new UsersAdapter.OnUserActionsListener() { // <-- Используем UsersAdapter
            @Override
            public void onChangeRole(UserModel user, String newRole) {
                Log.d(TAG, "onChangeRole: Смена роли для пользователя " + user.getId() + " на " + newRole);
                viewModel.changeUserRole(user.getId(), newRole); // <-- Вызываем changeUserRole из UsersViewModel
            }

            @Override
            public void onOpenChat(UserModel user) {
                Log.d(TAG, "onOpenChat: Открытие чата с пользователем " + user.getId());
                viewModel.openChatWithUser(user.getId()); // <-- Вызываем openChatWithUser из UsersViewModel
                // --- Открываем ChatActivity ---
                Intent intent = new Intent(getActivity(), ChatActivity.class); // <-- Убедись, что ChatActivity существует
                intent.putExtra("chatId", "chat_id_from_backend"); // <-- Замени на реальный chatId из ViewModel
                startActivity(intent);
                // ---
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false)); // <-- Вертикальный список
        recyclerView.setAdapter(adapter);
        Log.d(TAG, "onCreateView: RecyclerView инициализирован.");
        // --- Конец инициализации RecyclerView ---

        // --- Инициализация ViewModel ---
        Log.d(TAG, "onCreateView: Инициализация ViewModel.");
        viewModel = new ViewModelProvider(this).get(UsersViewModel.class); // <-- Используем UsersViewModel
        Log.d(TAG, "onCreateView: ViewModel инициализирована.");
        // --- Конец инициализации ViewModel ---

        // --- Наблюдение за LiveData ---
        Log.d(TAG, "onCreateView: Начало наблюдения за LiveData из ViewModel.");
        viewModel.getUsers().observe(getViewLifecycleOwner(), users -> {
            if (users != null) {
                Log.d(TAG, "onCreateView -> usersLiveData: Получено " + users.size() + " пользователей.");
                adapter.updateUsers(users); // <-- Обновляем адаптер
            } else {
                Log.w(TAG, "onCreateView -> usersLiveData: Получен null вместо списка.");
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
        Log.d(TAG, "onCreateView: Наблюдение за LiveData установлено.");
        // --- Конец наблюдения за LiveData ---

        // --- Загрузка пользователей ---
        Log.d(TAG, "onCreateView: Вызов loadUsers из ViewModel.");
        viewModel.loadUsers(); // <-- Вызываем loadUsers из UsersViewModel
        Log.d(TAG, "onCreateView: loadUsers вызван.");
        // --- Конец загрузки пользователей ---

        Log.d(TAG, "onCreateView: Представление фрагмента создано.");
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: Уничтожение представления фрагмента.");
        binding = null;
    }
}