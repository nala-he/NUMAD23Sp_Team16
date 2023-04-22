package edu.northeastern.numad23sp_team16.Project;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import edu.northeastern.numad23sp_team16.R;

//<<<<<<< HEAD:app/src/main/java/edu/northeastern/numad23sp_team16/Project/MyReminder.java
public class MyReminder extends BroadcastReceiver {

    private int notificationId = 0;
    private String channelId = "notification_channel_0";
    private int requestCode;


    @Override
    public void onReceive(Context context, Intent intent) {
        // Get the reminder message from the intent extras
        String reminderMessage = intent.getStringExtra("reminder_message");
        requestCode = intent.getIntExtra("request_code", 0);
        Log.d("MyReminder", "Reminder received: " + reminderMessage);

        // Create a notification using the reminder message
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.flag)
                .setContentTitle("Reminder")
                .setContentText(reminderMessage)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

//<<<<<<< HEAD:app/src/main/java/edu/northeastern/numad23sp_team16/Project/MyReminder.java
        NotificationManager mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
//        mNotificationManager.notify(0, builder.build());
        mNotificationManager.notify(notificationId++, builder.build());

//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
//
//
//        // if only want to let the notification panel show the latest one notification, use this below
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
//
//        }
//
//        notificationManager.notify(notificationId, builder.build());
        Log.i("MyReminder", "receive notification");


    }
}