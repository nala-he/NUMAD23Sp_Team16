package edu.northeastern.numad23sp_team16.receivednotification;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import edu.northeastern.numad23sp_team16.R;

public class RealtimeDatabaseActivity extends AppCompatActivity {
    private static final String TAG = RealtimeDatabaseActivity.class.getSimpleName();

    private DatabaseReference mDatabase;
    private String loggedInUser;
    private TextView username1;
    private TextView sticker1;
    private TextView username2;
    private TextView sticker2;
    private RadioButton receiver1;
    private RadioButton option1;

    // hardcoded for testing, needs to update later
    private static int messageId = 1;


    private String channelId = "notification_channel_0";
    private int notificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime_database);

        // hardcoded for testing, needs to update later
        loggedInUser = "A";
        username1 = (TextView) findViewById(R.id.username1);
        username2 = (TextView) findViewById(R.id.username2);
        sticker1 = (TextView) findViewById(R.id.sticker1);
        sticker2 = (TextView) findViewById(R.id.sticker2);
        receiver1 = (RadioButton) findViewById(R.id.receiver1);
        option1 = (RadioButton) findViewById(R.id.sticker_option1);
        notificationId = 0;

        createNotificationChannel();

        // Connect with firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // Update the sticker in realtime
        mDatabase.child("Messages")
                .addChildEventListener(
                        new ChildEventListener() {

                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                showSticker(dataSnapshot);
                                Log.e(TAG, "onChildAdded: dataSnapshot = " + dataSnapshot.getValue().toString());
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                showSticker(dataSnapshot);
                                Log.v(TAG, "onChildChanged: " + dataSnapshot.getValue().toString());
                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(TAG, "onCancelled:" + databaseError);
                                Toast.makeText(getApplicationContext()
                                        , "DBError: " + databaseError, Toast.LENGTH_SHORT).show();
                            }
                        }
                );
    }


    // Send sticker button
    public void sendSticker(View view) {
        RealtimeDatabaseActivity.this.onSendSticker(mDatabase,
                receiver1.isChecked() ? "B" : "C", loggedInUser,
                option1.isChecked() ? "1" : "2");
    }

    private void onSendSticker(DatabaseReference postRef,
                               String receiver, String sender, String sticker) {

        postRef.child("Messages")
                .child("message" + String.valueOf(messageId++))
                .setValue(new Message(receiver, sender, sticker));
        postRef
                .child("Messages")
                .child("message" + messageId)
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {

                        Message message = mutableData.getValue(Message.class);

                        if (receiver == null || message == null) {
                            return Transaction.success(mutableData);
                        }

                        if (message.receiverName.equals(receiver)) {
                            message.stickerId = sticker;
                            mutableData.setValue(message);
                        }

                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b,
                                           DataSnapshot dataSnapshot) {
                        // Transaction completed
                        Log.d(TAG, "postTransaction:onComplete:" + databaseError);
                    }
                });
    }


    private void showSticker(DataSnapshot dataSnapshot) {
        Message message = dataSnapshot.getValue(Message.class);
        if (message != null) {
            if (Objects.equals(message.receiverName, loggedInUser)) {
                sendNotification(message.senderName);
            }
            if (message.receiverName.equalsIgnoreCase("B")) {
                username1.setText("B");
                sticker1.setText(message.stickerId);
            } else {
                username2.setText("C");
                sticker2.setText(message.stickerId);
            }
        }
    }

    public void createNotificationChannel() {
        // This must be called early because it must be called before a notification is sent.
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notification Name";
            String description = "Notification Channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);

            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void sendNotification(String sender) {

        // Build notification
        // Need to define a channel ID after Android Oreo
        Bitmap myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.thinking_face);
        NotificationCompat.Builder notifyBuild = new NotificationCompat.Builder(this, channelId)
                //"Notification icons must be entirely white."
                .setSmallIcon(R.drawable.foo)
                .setContentTitle("New sticker from " + sender)
                .setContentText("Subject")
                .setLargeIcon(myBitmap)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(myBitmap)
                        .bigLargeIcon(null))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                // hide the notification after its selected
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            // calling ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS}, 0);
        }

        notificationManager.notify(notificationId++, notifyBuild.build());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            Log.v(TAG, "The user gave access.");
            Toast.makeText(this, "The user gave permission.", Toast.LENGTH_SHORT).show();

        } else {
            Log.e(TAG, "User denied permission.");
            // permission denied
            Toast.makeText(this, "The user denied permission.", Toast.LENGTH_SHORT).show();
        }
    }
}