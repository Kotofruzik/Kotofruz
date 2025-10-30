package com.example.autoschoolbtgp.adminPanel.chats;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private OnChatClickListener listener;

    public interface OnChatClickListener {
        void onChatClick(ChatModel chat);
    }

    public ChatsAdapter(OnChatClickListener listener) {
        this.listener = listener;
    }

    public void updateChats(List<ChatModel> newChats) {
        this.chats = newChats;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ChatsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatsViewHolder holder, int position) {
        ChatModel chat = chats.get(position);
        holder.bind(chat);
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    class ChatsViewHolder extends RecyclerView.ViewHolder {
        ImageView chatImageView;
        TextView textChatName, textLastMessage, textLastMessageTime;

        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);
            chatImageView = itemView.findViewById(R.id.chatImageView);
            textChatName = itemView.findViewById(R.id.textChatName);
            textLastMessage = itemView.findViewById(R.id.textLastMessage);
            textLastMessageTime = itemView.findViewById(R.id.textLastMessageTime);

            // Устанавливаем обработчик на ВЕСЬ элемент списка
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onChatClick(chats.get(position));
                }
            });
        }

        public void bind(ChatModel chat) {
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
        }
    }
}