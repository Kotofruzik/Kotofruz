package com.example.autoschoolbtgp.ui.users;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autoschoolbtgp.R;
import com.example.autoschoolbtgp.ui.chat.ChatActivity;

import java.util.List;

public class UsersFragment extends Fragment {
    private UsersViewModel viewModel;
    private UsersAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_users);
        adapter = new UsersAdapter(new UsersAdapter.OnUserActionsListener() {
            @Override
            public void onChangeRole(UserModel user, String newRole) {
                viewModel.changeUserRole(user.getId(), newRole);
            }

            @Override
            public void onOpenChat(UserModel user) {
                viewModel.openChatWithUser(user.getId());
                // Здесь открываем чат, например:
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("chatId", "chat_id_from_backend");
                startActivity(intent);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(UsersViewModel.class);
        viewModel.getUsers().observe(getViewLifecycleOwner(), users -> {
            if (users != null) {
                adapter.updateUsers(users);
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            // Показать ошибку, например, через Toast
        });

        viewModel.loadUsers();

        return view;
    }
}