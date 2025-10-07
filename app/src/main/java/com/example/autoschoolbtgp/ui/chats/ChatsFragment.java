package com.example.autoschoolbtgp.ui.chats;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.autoschoolbtgp.databinding.FragmentChatsBinding;

public class ChatsFragment extends Fragment {

    private FragmentChatsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ChatsViewModel chatsViewModel =
                new ViewModelProvider(this).get(ChatsViewModel.class);

        binding = FragmentChatsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textChats;
        chatsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}