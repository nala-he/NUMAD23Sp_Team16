package edu.northeastern.numad23sp_team16.Project;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import edu.northeastern.numad23sp_team16.R;

public class ReminderAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        // Get the reminder message from the intent extras
        String reminderMessage = intent.getStringExtra("reminder_message");
        Log.d("reminderMessage in AlarmReceiver: ",reminderMessage);
        Log.d("MyReminder", "Reminder received: " + reminderMessage);

        // Create a notification using the reminder message
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"notification_channel_0")
                .setSmallIcon(R.drawable.heart)
                .setContentTitle("Reminder")
                .setContentText(reminderMessage)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            ActivityCompat.requestPermissions((Activity) context, new String[] { Manifest.permission.POST_NOTIFICATIONS }, 0);
            return;
        } else {
            // Permission already granted, show the notification
            notificationManager.notify(0, builder.build());
        }




    }
}