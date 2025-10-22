package com.example.autoschoolbtgp.adminPanel.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.autoschoolbtgp.R;
import com.example.autoschoolbtgp.adminPanel.chats.MessageModel;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<MessageModel> messages = new ArrayList<>();

    public void updateMessages(List<MessageModel> newMessages) {
        this.messages = newMessages;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        MessageModel message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView textSenderName, textMessage, textTime;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textSenderName = itemView.findViewById(R.id.text_sender_name);
            textMessage = itemView.findViewById(R.id.text_message);
            textTime = itemView.findViewById(R.id.text_time);
        }

        public void bind(MessageModel message) {
            textSenderName.setText(message.getSenderName());
            textMessage.setText(message.getText());
            textTime.setText(message.getCreatedAt());
        }
    }
}