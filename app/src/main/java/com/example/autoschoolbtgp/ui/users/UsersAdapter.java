package com.example.autoschoolbtgp.ui.users;

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

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder> {
    private List<UserModel> users = new ArrayList<>();
    private OnUserActionsListener listener;

    public interface OnUserActionsListener {
        void onChangeRole(UserModel user, String newRole);
        void onOpenChat(UserModel user);
    }

    public UsersAdapter(OnUserActionsListener listener) {
        this.listener = listener;
    }

    public void updateUsers(List<UserModel> newUsers) {
        this.users = newUsers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        UserModel user = users.get(position);
        holder.bind(user, listener);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class UsersViewHolder extends RecyclerView.ViewHolder {
        ImageView userImageView;
        TextView textName, textSurname, textRole;
        Button btnChat, btnChangeRole;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            userImageView = itemView.findViewById(R.id.userImageView);
            textName = itemView.findViewById(R.id.textName);
            textSurname = itemView.findViewById(R.id.textSurname);
            textRole = itemView.findViewById(R.id.textRole);
            btnChat = itemView.findViewById(R.id.btnChat);
            btnChangeRole = itemView.findViewById(R.id.btnChangeRole);
        }

        public void bind(UserModel user, OnUserActionsListener listener) {
            textName.setText(user.getName());
            textSurname.setText(user.getSurname());
            textRole.setText("Роль: " + user.getRole());

            // Загружаем аватарку
            String photoUrl = user.getAvatarUrl();
            if (photoUrl != null && !photoUrl.isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(photoUrl)
                        .placeholder(R.drawable.users_icon) // Дефолтная аватарка
                        .error(R.drawable.users_icon)       // Если ошибка
                        .into(userImageView);
            } else {
                userImageView.setImageResource(R.drawable.users_icon); // Дефолтная аватарка
            }

            btnChangeRole.setOnClickListener(v -> {
                showRoleSelectionDialog(itemView.getContext(), user, listener);
            });

            btnChat.setOnClickListener(v -> listener.onOpenChat(user));
        }

        private void showRoleSelectionDialog(android.content.Context context, UserModel user, OnUserActionsListener listener) {
            String[] roles = {"admin", "instructor", "student"};
            String currentRole = user.getRole();

            // Найдем индекс текущей роли
            int selected = -1;
            for (int i = 0; i < roles.length; i++) {
                if (roles[i].equals(currentRole)) {
                    selected = i;
                    break;
                }
            }

            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
            builder.setTitle("Выберите роль");

            builder.setSingleChoiceItems(roles, selected, (dialog, which) -> {
                String newRole = roles[which];
                listener.onChangeRole(user, newRole);
                dialog.dismiss();
            });

            builder.setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss());

            builder.show();
        }
    }
}