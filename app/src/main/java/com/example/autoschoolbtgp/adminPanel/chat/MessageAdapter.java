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

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

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
            return VIEW_TYPE_SENT; // Ваши сообщения — справа
        } else {
            return VIEW_TYPE_RECEIVED; // Сообщения собеседника — слева
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId;
        if (viewType == VIEW_TYPE_SENT) {
            layoutId = R.layout.item_message_sent;
        } else {
            layoutId = R.layout.item_message_received;
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new MessageViewHolder(view);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        if (position >= messages.size()) return; // Защита от IndexOutOfBounds

        MessageModel message = messages.get(position);

        // ✅ Проверки на null перед setText()
        if (holder.textSenderName != null) {
            holder.textSenderName.setText(message.getSenderName() != null ? message.getSenderName() : "");
        }

        if (holder.textMessage != null) {
            holder.textMessage.setText(message.getText() != null ? message.getText() : "");
        }

        if (holder.textTime != null && message.getCreatedAt() != null) {
            Date createdAt = message.getCreatedAt();
            long now = System.currentTimeMillis();
            long diffDays = (now - createdAt.getTime()) / (1000 * 60 * 60 * 24);

            SimpleDateFormat format = diffDays == 0
                    ? new SimpleDateFormat("HH:mm", Locale.getDefault())
                    : new SimpleDateFormat("dd.MM.yy", Locale.getDefault());

            holder.textTime.setText(format.format(createdAt));
        } else if (holder.textTime != null) {
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
            textSenderName = itemView.findViewById(R.id.text_sender_name);
            textMessage = itemView.findViewById(R.id.text_message);
            textTime = itemView.findViewById(R.id.text_time);
        }
    }
}
