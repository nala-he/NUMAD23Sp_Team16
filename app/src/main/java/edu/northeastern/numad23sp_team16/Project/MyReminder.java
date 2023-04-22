package edu.northeastern.numad23sp_team16.Project;

import android.Manifest;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import edu.northeastern.numad23sp_team16.MainActivity;
import edu.northeastern.numad23sp_team16.R;

public class MyReminder extends IntentService {

    private static final int NOTIFICATION_ID = 0;

    public MyReminder() {
        super("MyReminder");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Get the reminder message from the intent extras
        String reminderMessage = intent.getStringExtra("reminder_message");
        Log.d("MyReminder", "Reminder received: " + reminderMessage);

        // Create a notification using the reminder message
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default")
                .setSmallIcon(R.drawable.heart)
                .setContentTitle("Reminder")
                .setContentText(reminderMessage)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent notifyIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 2, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //to be able to launch your activity from the notification
        builder.setContentIntent(pendingIntent);
        Notification notificationCompat = builder.build();

        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            ActivityCompat.requestPermissions(, new String[] { Manifest.permission.POST_NOTIFICATIONS }, 0);
            return;
        } else {
            // Permission already granted, show the notification
            notificationManager.notify(0, builder.build());
        }
    }
}
