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

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder> {
    private List<ChatModel> chats = new ArrayList<>();
    private OnChatActionsListener listener;

    public interface OnChatActionsListener {
        void onOpenChat(ChatModel chat);
        void onDeleteChat(ChatModel chat);
    }

    public ChatsAdapter(OnChatActionsListener listener) {
        this.listener = listener;
    }

    public void updateChats(List<ChatModel> newChats) {
        this.chats = newChats;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_card, parent, false);
        return new ChatsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatsViewHolder holder, int position) {
        ChatModel chat = chats.get(position);
        holder.bind(chat, listener);
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    static class ChatsViewHolder extends RecyclerView.ViewHolder {
        ImageView chatImageView;
        TextView textChatName, textLastMessage;
        Button btnOpenChat, btnDeleteChat;

        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);
            chatImageView = itemView.findViewById(R.id.chatImageView);
            textChatName = itemView.findViewById(R.id.textChatName);
            textLastMessage = itemView.findViewById(R.id.textLastMessage);
            btnOpenChat = itemView.findViewById(R.id.btnOpenChat);
            btnDeleteChat = itemView.findViewById(R.id.btnDeleteChat);
        }

        public void bind(ChatModel chat, OnChatActionsListener listener) {
            textChatName.setText(chat.getName());
            textLastMessage.setText(chat.getLastMessage() != null ? chat.getLastMessage() : "Нет сообщений");

            String photo = chat.getPhoto();
            if (photo != null && !photo.isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(photo)
                        .placeholder(R.drawable.users_icon)
                        .error(R.drawable.users_icon)
                        .into(chatImageView);
            } else {
                chatImageView.setImageResource(R.drawable.users_icon);
            }

            btnOpenChat.setOnClickListener(v -> listener.onOpenChat(chat));
            btnDeleteChat.setOnClickListener(v -> listener.onDeleteChat(chat));
        }
    }
}