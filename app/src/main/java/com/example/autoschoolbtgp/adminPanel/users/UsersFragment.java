package com.example.autoschoolbtgp.adminPanel.users;

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
import com.example.autoschoolbtgp.databinding.FragmentUsersBinding;
import com.example.autoschoolbtgp.adminPanel.users.UserModel;

import java.util.List;

public class UsersFragment extends Fragment {
    private static final String TAG = "UsersFragment_SENIOR";
    private FragmentUsersBinding binding;
    private UsersViewModel viewModel;
    private UsersAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentUsersBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        RecyclerView recyclerView = binding.recyclerViewUsers;
        adapter = new UsersAdapter(new UsersAdapter.OnUserActionsListener() {
            @Override
            public void onChangeRole(UserModel user, String newRole) {
                viewModel.changeUserRole(user.getId(), newRole);
            }

            @Override
            public void onOpenChat(UserModel user) {
                Log.d(TAG, "onOpenChat: нажата кнопка 'открыть чат' для пользователя с ID: " + user.getId());
                viewModel.openChatWithUser(user.getId());
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(UsersViewModel.class);

        viewModel.getChatId().observe(getViewLifecycleOwner(), chatId -> {
            if (chatId != null) {
                Log.d(TAG, "onCreateView -> chatIdLiveData: получен chatId: " + chatId);
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("chatId", chatId);
                startActivity(intent);
            } else {
                Log.w(TAG, "onCreateView -> chatIdLiveData: получен null вместо chatId");
            }
        });
        // --- ИЗМЕНЕНИЕ КОНЕЦ ---

        viewModel.getUsers().observe(getViewLifecycleOwner(), users -> {
            if (users != null) {
                android.util.Log.d(TAG, "Получено " + users.size() + " пользователей");
                adapter.updateUsers(users);
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                android.util.Log.e(TAG, "Ошибка: " + error);
                Toast.makeText(requireContext(), "Ошибка: " + error, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                android.util.Log.d(TAG, "Сообщение: " + message);
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.loadUsers();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}