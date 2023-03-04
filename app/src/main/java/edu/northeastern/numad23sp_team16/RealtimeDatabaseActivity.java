package edu.northeastern.numad23sp_team16;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import edu.northeastern.numad23sp_team16.models.Message;

public class RealtimeDatabaseActivity extends AppCompatActivity {
    private static final String TAG = RealtimeDatabaseActivity.class.getSimpleName();

    private DatabaseReference mDatabase;
    private String loggedInUser;
    private String recipient;
    private Integer stickerId;

    private TextView userSender;
    private TextView userReceiver;
    private ImageView sentSticker;

    // hardcoded for testing, needs to update later
    private static int messageId = 1;


    private String channelId = "notification_channel_0";
    private int notificationId;

    private List<Message> receivedHistory;
    private Map<String, Integer> sentStickersCount;

    private final String CURRENT_USER = "CURRENT_USER";
    private final String RECEIVER = "RECEIVER";
    private final String STICKER = "STICKER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime_database);

        userSender = (TextView) findViewById(R.id.username_sender);
        userReceiver = (TextView) findViewById(R.id.username_receiver);
        sentSticker = (ImageView) findViewById(R.id.sent_sticker);

        // Retrieve currently logged in user
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            loggedInUser = extras.getString(CURRENT_USER);
            recipient = extras.getString(RECEIVER);
            // stickerId will be updated later when the current user sent other messages
            stickerId = extras.getInt(STICKER);
            userSender.setText(loggedInUser);
            userReceiver.setText(recipient);
            // sentSticker will be updated later when the current user sent other messages
            sentSticker.setImageResource(stickerId);
        }

        notificationId = 0;
        receivedHistory = new ArrayList<>();
        sentStickersCount = new HashMap<>();

        createNotificationChannel();

        // Connect with firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // Update the sticker in realtime
        mDatabase.child("messages")
                .addChildEventListener(
                        new ChildEventListener() {

                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                                showSticker(dataSnapshot);
                                getStickerCountAndHistory(dataSnapshot);

//                                Message message = dataSnapshot.getValue(Message.class);
//
//                                if (message != null
//                                        && Objects.equals(message.receiverName, loggedInUser)) {
//                                    sendNotification(message.senderName, message.stickerId);
//                                }
                                Log.e(TAG, "onChildAdded: dataSnapshot = " + dataSnapshot.getValue().toString());
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                                showSticker(dataSnapshot);
//                                Message message = dataSnapshot.getValue(Message.class);
//                                if (message != null
//                                        && Objects.equals(message.receiverName, loggedInUser)) {
//                                    sendNotification(message.senderName, message.stickerId);
//                                }
//                                Log.v(TAG, "onChildChanged: " + dataSnapshot.getValue().toString());
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

        // Send the new message containing sticker sending info to the Realtime Database
        onSendSticker(mDatabase, recipient, loggedInUser, stickerId);

        // initialize the two buttons for the history lists
        Button countButton = (Button) findViewById(R.id.show_sticker_count_button);
        Button historyButton = (Button) findViewById(R.id.show_history_button);
        countButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showStickerCount();
            }
        });
        historyButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showStickerHistory();
            }
        });
    }


    // Send sticker button
//    public void sendSticker(View view) {
//        RealtimeDatabaseActivity.this.onSendSticker(mDatabase, receiver, loggedInUser, stickerId);
//    }

    private void onSendSticker(DatabaseReference postRef,
                               String receiver, String sender, Integer sticker) {
        // add the time as part of the message id to avoid new message overriding the previous message
        // with the same id
        String time = String.valueOf(System.currentTimeMillis()/1000);
        postRef.child("messages")
                .child("message" + time + messageId++)
                .setValue(new Message(receiver, sender, String.valueOf(sticker)));
        postRef
                .child("messages")
                .child("message" + time + messageId)
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {

                        Message message = mutableData.getValue(Message.class);

                        if (receiver == null || message == null) {
                            return Transaction.success(mutableData);
                        }

                        if (message.receiverName.equals(receiver)) {
                            message.stickerId = String.valueOf(sticker);
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

    private void getStickerCountAndHistory(DataSnapshot dataSnapshot) {
        Message message = dataSnapshot.getValue(Message.class);
        if (message != null) {

            // add sticker count to sentStickersCount map
            if (Objects.equals(message.senderName, loggedInUser)) {
                if (sentStickersCount.containsKey(message.stickerId)) {
                    Integer count = sentStickersCount.get(message.stickerId);
                    count += 1;
                    sentStickersCount.put(message.stickerId, count);
                } else {
                    sentStickersCount.put(message.stickerId, 1);
                }
            }

            Log.e(TAG, "sentStickersCount:" + sentStickersCount.toString());

            if (Objects.equals(message.receiverName, loggedInUser)) {
                // send notification to the specific receiver
                sendNotification(message.senderName, message.stickerId);

                // add the matched message to the history list
                receivedHistory.add(message);
            }

            Log.e(TAG, "receivedHistory:" + receivedHistory.toString());
        }
    }

//    private void getStickerHistory(DataSnapshot dataSnapshot) {
//        Message message = dataSnapshot.getValue(Message.class);
//        if (message != null) {
//            if (Objects.equals(message.receiverName, loggedInUser)) {
//                // add the matched message to the history list
//                receivedHistory.add(message);
//            }
//            Log.e(TAG, "receivedHistory:" + receivedHistory.toString());
//            Log.e(TAG, "receivedHistory:" + receivedHistory.get(0).stickerId);
//

//        }
//    }

    public void showStickerCount() {
        //TODO: Display how many of each kind of sticker a user sent
    }

    public void showStickerHistory() {
        // TODO: Display history of stickers user has received (which sticker received, who sent it,
        //  when it was sent)
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

    public void sendNotification(String sender, String stickerId) {

        // Build notification
        // Need to define a channel ID after Android Oreo
        Bitmap myBitmap = BitmapFactory.decodeResource(getResources(), Integer.parseInt(stickerId));
        NotificationCompat.Builder notifyBuild = new NotificationCompat.Builder(this, channelId)
                //"Notification icons must be entirely white."
                .setSmallIcon(R.drawable.foo)
                .setContentTitle("You received a sticker from " + sender)
//                .setContentText("Subject")
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

        // if only want to let the notification panel show the latest one notification, use this below
//        notificationManager.notify(notificationId, notifyBuild.build());
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