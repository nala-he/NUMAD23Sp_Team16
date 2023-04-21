package edu.northeastern.numad23sp_team16.Project;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

// Broadcast receiver for goal reminders
public class MyReceiver extends BroadcastReceiver {
    public MyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent reminderIntent = new Intent(context, MyReminder.class);
        context.startService(reminderIntent);
    }
}
