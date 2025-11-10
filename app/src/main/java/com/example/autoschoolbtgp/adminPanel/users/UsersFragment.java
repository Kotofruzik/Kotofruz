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
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("targetUserId", user.getId());
                startActivity(intent);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(UsersViewModel.class);

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                if (isLoading) {
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.recyclerViewUsers.setVisibility(View.GONE);
                } else {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.recyclerViewUsers.setVisibility(View.VISIBLE);
                }
            }
        });

        viewModel.getUsers().observe(getViewLifecycleOwner(), users -> {
            if (users != null) {
                Log.d(TAG, "Получено " + users.size() + " пользователей");
                adapter.updateUsers(users);
                binding.progressBar.setVisibility(View.GONE);
                binding.recyclerViewUsers.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Log.e(TAG, "Ошибка: " + error);
                Toast.makeText(requireContext(), "Ошибка: " + error, Toast.LENGTH_LONG).show();
                binding.progressBar.setVisibility(View.GONE);
                binding.recyclerViewUsers.setVisibility(View.VISIBLE);
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