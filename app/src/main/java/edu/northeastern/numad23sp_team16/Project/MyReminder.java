package edu.northeastern.numad23sp_team16.Project;

import android.Manifest;
//<<<<<<< HEAD:app/src/main/java/edu/northeastern/numad23sp_team16/Project/MyReminder.java
import android.app.NotificationManager;
//=======
import android.app.Activity;
//>>>>>>> origin/send-reminder:app/src/main/java/edu/northeastern/numad23sp_team16/Project/ReminderAlarmReceiver.java
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import edu.northeastern.numad23sp_team16.MainActivity;
import edu.northeastern.numad23sp_team16.R;

//<<<<<<< HEAD:app/src/main/java/edu/northeastern/numad23sp_team16/Project/MyReminder.java
public class MyReminder extends BroadcastReceiver {

    private int notificationId = 0;
    private String channelId = "notification_channel_0";


    @Override
    public void onReceive(Context context, Intent intent) {
        // Get the reminder message from the intent extras
        String reminderMessage = intent.getStringExtra("reminder_message");
        Log.d("MyReminder", "Reminder received: " + reminderMessage);

        // Create a notification using the reminder message
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.flag)
//=======
//public class ReminderAlarmReceiver extends BroadcastReceiver {
//    @Override
//    public void onReceive(Context context, Intent intent) {

        // Get the reminder message from the intent extras
//        String reminderMessage = intent.getStringExtra("reminder_message");
//        Log.d("reminderMessage in AlarmReceiver: ",reminderMessage);
//        Log.d("MyReminder", "Reminder received: " + reminderMessage);

//        // Create a notification using the reminder message
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"notification_channel_0")
//                .setSmallIcon(R.drawable.heart)
//>>>>>>> origin/send-reminder:app/src/main/java/edu/northeastern/numad23sp_team16/Project/ReminderAlarmReceiver.java
                .setContentTitle("Reminder")
                .setContentText(reminderMessage)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

//<<<<<<< HEAD:app/src/main/java/edu/northeastern/numad23sp_team16/Project/MyReminder.java
        NotificationManager mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, builder.build());

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



//        // Show the notification
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//            // Request the permission
//            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.POST_NOTIFICATIONS }, 0);
//            return;
//        } else {
//            // Permission already granted, show the notification
//            notificationManager.notify(0, builder.build());
//        }
//
//        // Close the activity
//        finish();
//=======
//        // Show the notification
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//            // Request the permission
//            ActivityCompat.requestPermissions((Activity) context, new String[] { Manifest.permission.POST_NOTIFICATIONS }, 0);
//            return;
//        } else {
//            // Permission already granted, show the notification
//            notificationManager.notify(0, builder.build());
//        }
//
//
//
//
//>>>>>>> origin/send-reminder:app/src/main/java/edu/northeastern/numad23sp_team16/Project/ReminderAlarmReceiver.java
    }
}