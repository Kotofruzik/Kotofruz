package com.example.autoschoolbtgp.adminPanel.chats;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.autoschoolbtgp.R;
import java.util.ArrayList;
import java.util.List;

public class ChatsListAdapter extends RecyclerView.Adapter<ChatsListAdapter.ChatsListViewHolder> {
    private List<ChatListModel> chats = new ArrayList<>();
    private OnChatListClickListener listener;

    public interface OnChatListClickListener {
        void onChatClick(ChatListModel chat);
    }

    public ChatsListAdapter(OnChatListClickListener listener) {
        this.listener = listener;
    }

    public void updateChats(List<ChatListModel> newChats) {
        this.chats = newChats;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatsListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_list, parent, false);
        return new ChatsListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatsListViewHolder holder, int position) {
        ChatListModel chat = chats.get(position);
        holder.bind(chat, listener);
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    static class ChatsListViewHolder extends RecyclerView.ViewHolder {
        ImageView chatImageView;
        TextView textChatName, textLastMessage, textLastMessageTime;
        Button btnOpenChat;

        public ChatsListViewHolder(@NonNull View itemView) {
            super(itemView);
            chatImageView = itemView.findViewById(R.id.chatImageView);
            textChatName = itemView.findViewById(R.id.textChatName);
            textLastMessage = itemView.findViewById(R.id.textLastMessage);
            textLastMessageTime = itemView.findViewById(R.id.textLastMessageTime);
            btnOpenChat = itemView.findViewById(R.id.btnOpenChat);
        }

        public void bind(ChatListModel chat, OnChatListClickListener listener) {
            textChatName.setText(chat.getName());
            textLastMessage.setText(chat.getLastMessageText() != null ? chat.getLastMessageText() : "Нет сообщений");
            textLastMessageTime.setText(chat.getLastMessageTime() != null ? chat.getLastMessageTime() : "");

            String photoUrl = chat.getPhotoUrl();
            if (photoUrl != null && !photoUrl.isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(photoUrl)
                        .placeholder(R.drawable.users_icon)
                        .error(R.drawable.users_icon)
                        .into(chatImageView);
            } else {
                chatImageView.setImageResource(R.drawable.users_icon);
            }

            btnOpenChat.setOnClickListener(v -> listener.onChatClick(chat));
        }
    }
}