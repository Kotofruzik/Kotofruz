package com.example.autoschoolbtgp.adminPanel.chat;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autoschoolbtgp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<MessageModel> messages = new ArrayList<>();
    private String currentUserId;

    public MessageAdapter(String currentUserId) {
        this.currentUserId = currentUserId;
    }

    public void updateMessages(List<MessageModel> newMessages) {
        this.messages = newMessages != null ? newMessages : new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        MessageModel message = messages.get(position);
        if (currentUserId != null && currentUserId.equals(message.getSenderId())) {
            return R.layout.item_message_sent; // Ваши сообщения — справа
        } else {
            return R.layout.item_message_received; // Сообщения собеседника — слева
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new MessageViewHolder(view);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        MessageModel message = messages.get(position);

        // ✅ Устанавливаем имя отправителя
        holder.textSenderName.setText(message.getSenderName());

        // ✅ Устанавливаем текст сообщения
        holder.textMessage.setText(message.getText());

        // ✅ Устанавливаем время
        Date createdAt = message.getCreatedAt();
        if (createdAt != null) {
            long now = System.currentTimeMillis();
            long diffDays = (now - createdAt.getTime()) / (1000 * 60 * 60 * 24);

            SimpleDateFormat format = diffDays == 0
                    ? new SimpleDateFormat("HH:mm", Locale.getDefault())
                    : new SimpleDateFormat("dd.MM.yy", Locale.getDefault());

            holder.textTime.setText(format.format(createdAt));
        } else {
            holder.textTime.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView textSenderName;
        TextView textMessage;
        TextView textTime;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textSenderName = itemView.findViewById(R.id.textSenderName);
            textMessage = itemView.findViewById(R.id.textMessage);
            textTime = itemView.findViewById(R.id.textTime);
        }
    }
}