package edu.northeastern.numad23sp_team16.Project;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import edu.northeastern.numad23sp_team16.R;

public class MyReminder extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the reminder message from the intent extras
        String reminderMessage = getIntent().getStringExtra("reminder_message");
        Log.d("MyReminder", "Reminder received: " + reminderMessage);

        // Create a notification using the reminder message
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default")
                .setSmallIcon(R.drawable.heart)
                .setContentTitle("Reminder")
                .setContentText(reminderMessage)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.POST_NOTIFICATIONS }, 0);
            return;
        } else {
            // Permission already granted, show the notification
            notificationManager.notify(0, builder.build());
        }

        // Close the activity
        finish();
    }
}
