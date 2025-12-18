package com.example.autoschoolbtgp.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.NetworkType;

import java.util.concurrent.TimeUnit;
import com.example.autoschoolbtgp.R;

public class NotificationsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        Button testButton = view.findViewById(R.id.btn_test_notification);
        Button remindButton = view.findViewById(R.id.btn_tomorrow_reminder);

        testButton.setOnClickListener(v -> {
            scheduleNotification("Тестовое уведомление", 5000);

        });

        remindButton.setOnClickListener(v -> {
           scheduleNotification("Не забудьте завтра", 24 * 60 * 60 * 1000);
        });
        return view;
    }

    private void scheduleNotification(String message, long delay) {
        // ⭐ Теперь Builder будет работать
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build();

        Data notificationData = new Data.Builder()
                .putString("message", message)
                .build();

        OneTimeWorkRequest notificationWork = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .setInputData(notificationData)
                .build();

        WorkManager.getInstance(requireContext()).enqueue(notificationWork);
    }
}
