package edu.northeastern.numad23sp_team16.Project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import edu.northeastern.numad23sp_team16.R;
import edu.northeastern.numad23sp_team16.models.Message;
import edu.northeastern.numad23sp_team16.models.User;


public class SendStatusActivity extends AppCompatActivity {
    private static final String TAG = "SendStatusActivity";
    private String channelId = "notification_channel_0";
    private int notificationId = 0;
    private static int messageId = 1;

    private ArrayList<Username> friendsList = new ArrayList<>();
    private List<String> friendIdsList = new ArrayList<>();

    private List<Username> receiverList = new ArrayList<>();
    private final int PERMISSION_REQUEST_CODE = 0;

    private final String FRIENDS_LIST = "FRIENDS_LIST";
    private final String CURRENT_USER = "CURRENT_USER";
    private final String LOGIN_TIME = "LOGIN_TIME";

    private String currentUser;
    private Timestamp loginTime;

    private User currentUserDetail;
    private RecyclerView friendListRecyclerView;
    private UsernameAdapter friendListAdapter;
    private DatabaseReference projectDatabase;
    private DatabaseReference usersRef;
    private DatabaseReference friendsRef;
    private DatabaseReference messagesRef;



    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_status);

        // customize action bar back button and title
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar_layout);
        actionBar.setHomeAsUpIndicator(R.drawable.back_button);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#D9D9D9")));
        actionBar.setDisplayHomeAsUpEnabled(true);
        // change action bar title
        AppCompatTextView title = findViewById(R.id.navbar_title);
        title.setText("Send Pet Status to Friends");

        // Retrieve currently logged in user -- Yutong
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentUser = extras.getString(CURRENT_USER);
            if (extras.getString(LOGIN_TIME) == null) {
                // get the login time
                Date date = new Date();
                loginTime = new Timestamp(date.getTime());
            } else {
                loginTime = Timestamp.valueOf(extras.getString(LOGIN_TIME));
            }
        }


        // initialize usersRef and friendsRef from firebase database
        projectDatabase = FirebaseDatabase.getInstance().getReference("FinalProject");
        usersRef = projectDatabase.child("FinalProjectUsers");
        friendsRef = projectDatabase.child("FinalProjectFriends");
        messagesRef = projectDatabase.child("FinalProjectMessages");

        // initialize views
        friendListRecyclerView = findViewById(R.id.userlist_recyclerview);
        friendListRecyclerView.setLayoutManager(new LinearLayoutManager(SendStatusActivity.this));
        TextView direction = findViewById(R.id.share_direction);
        direction.setText(R.string.send_my_pet_status_to_selected_friends);
        TextView userListTitle = findViewById(R.id.user_list_title);
        userListTitle.setText("List of Friends");

        Button addButton = findViewById(R.id.add_selected_friends_button);
        addButton.setVisibility(View.INVISIBLE);
        Button sendButton = findViewById(R.id.send_status_button);
        sendButton.setText(R.string.send_my_pet_status_to_friends);

        createNotificationChannel();

        // check if there is a previously saved instance
        if (savedInstanceState == null) {
            // get friends' ids from firebase
            getFriendIdsListData();
            // get friends' names based on ids from firebase
            getFriendNameList();
        }

        // change the hardcoded heartCount to user's pet heartCount from database
        int heartCount = 8;
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Username username : friendsList) {
                    if (username.isSelected()) {
                        receiverList.add(username);
                    }
                }
                // send user's pet status to selected friends from friendsList
                usersRef.addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                // store the currentUser's detail info in User class from the database
                                for (DataSnapshot data : snapshot.getChildren()) {
                                    if (Objects.equals(data.getKey(), currentUser)) {
                                        currentUserDetail = snapshot.child(currentUser).getValue(User.class);
                                        if (receiverList.size() != 0) {
                                            for (Username each : receiverList) {
                                                if (currentUserDetail != null) {
                                                    // send the status message record to database
                                                    onSendStatus(each.getUserId(), currentUser,
                                                            currentUserDetail.getUsername(),
                                                            heartCount,
                                                            currentUserDetail.getPetType(),
                                                            currentUserDetail.getPetName());
                                                }
                                            }
                                        } else {
                                            Toast.makeText(SendStatusActivity.this, "Did not select any friends.",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        }
                );

//                // receive the status notification if happen to be the currently logged in user
//                messagesRef.addChildEventListener(
//                        new ChildEventListener() {
//                            @Override
//                            public void onChildAdded(@NonNull DataSnapshot snapshot, String s) {
//                                Message message = snapshot.getValue(Message.class);
//                                if (message != null) {
//                                    Timestamp messageTime = Timestamp.valueOf(message.timeStamp);
//                                    Log.i("SendStatusActivity",
//                                            "message time: " + messageTime + " login time: " + loginTime);
//                                    if (message.receiverId.equals(currentUser) && messageTime.after(loginTime)) {
//                                        // send and receive status message
//                                        Log.i("SendStatusActivity", "send status");
//                                        sendStatusMessage(message.senderName, message.petType,
//                                                message.petName, heartCount);
//                                    }
//                                }
//                            }
//
//                            @Override
//                            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//                            }
//
//                            @Override
//                            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//
//                            }
//
//                            @Override
//                            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//
//                            }
//                        }
//                );


                Intent intent = new Intent(SendStatusActivity.this, ShareActivity.class);
                // close all the activities in the call stack above ShareActivity and bring it to
                // the top of the call stack
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

//                // pass the friendsList to the ShareActivity, need to be replaced with data from firebase later
//                Bundle bundle = new Bundle();
//                bundle.putParcelableArrayList(FRIENDS_LIST, (ArrayList<? extends Parcelable>) friendsList);
//                intent.putExtras(bundle);

                // pass the current user id back to share activity
                intent.putExtra(CURRENT_USER, currentUser);
                startActivity(intent);
            }
        });

        // receive the status notification if happen to be the currently logged in user
        messagesRef.addChildEventListener(
                new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, String s) {
                        Message message = snapshot.getValue(Message.class);
                        if (message != null) {
                            Timestamp messageTime = Timestamp.valueOf(message.timeStamp);
                            Log.i("SendStatusActivity",
                                    "message time: " + messageTime + " login time: " + loginTime);
                            if (message.receiverId.equals(currentUser) && messageTime.after(loginTime)) {
                                // send and receive status message
                                Log.i("SendStatusActivity", "send status");
                                sendStatusMessage(message.senderName, message.petType,
                                        message.petName, heartCount);
                            }
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );
    }

    // this event will enable the back function to the back button on press in customized action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // obtain friends data from firebase database
    private void getFriendIdsListData() {
        // Connect with firebase
        friendsRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            if (Objects.equals(data.child("currentUserId")
                                    .getValue(String.class), currentUser)) {
                                String friendId = Objects.requireNonNull(data.child("friendId")
                                        .getValue(String.class));
                                friendIdsList.add(friendId);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void getFriendNameList() {
        // Connect with firebase
        usersRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (String userId : friendIdsList) {
                            String name = snapshot.child(userId).child("username").getValue(String.class);
                            Username friend = new Username(name, userId);
                            // check if the name is already in the friendsList
                            if (friendsList.stream()
                                    .noneMatch(each -> each.getName().equals(name))) {
                                friendsList.add(friend);
                            }
                        }
                        friendListAdapter = new UsernameAdapter(friendsList);
                        friendListRecyclerView.setAdapter(friendListAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // store friendsList selected status
        outState.putParcelableArrayList(FRIENDS_LIST, friendsList);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // restore friendsList selected status
        friendsList = savedInstanceState.getParcelableArrayList(FRIENDS_LIST);
        getFriendIdsListData();
        getFriendNameList();
    }

    // create and send the status message to database
    private void onSendStatus(String receiver, String senderId, String senderName, int heartCount,
                              String petType, String petName) {

        // add the time as part of the message id to avoid new message overwriting the previous message
        // with the same id
        String time = String.valueOf(System.currentTimeMillis()/1000);
        messagesRef.child("message" + time + messageId++)
                .setValue(new Message(receiver, senderId, senderName, heartCount, petType, petName));
        messagesRef.child("message" + time + messageId)
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {

                        Message message = mutableData.getValue(Message.class);

                        if (receiver == null || message == null) {
                            return Transaction.success(mutableData);
                        }

                        if (message.receiverId.equals(receiver)) {
                            mutableData.setValue(message);
                        }

                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b,
                                           DataSnapshot dataSnapshot) {
                        Toast.makeText(getApplicationContext(), "Pet status sent to selected friends",
                                Toast.LENGTH_LONG).show();
                        // Transaction completed
                        Log.d(TAG, "postTransaction:onComplete:" + databaseError);
                    }
                });
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

    // send status message to the receiver
    public void sendStatusMessage(String senderName, String petType, String petName, int heartCount) {

        // Build notification
        // Need to define a channel ID after Android Oreo
        int id = petType.equals("dog") ? R.drawable.dog_small : R.drawable.cat_small;
//        int id = Integer.parseInt(petIconId);
        Bitmap myBitmap = BitmapFactory.decodeResource(getResources(), id);

//        // check if the perIconId is an unknown id, if yes, set a default unknown_sticker image
//        // as an image placeholder
//        if (id != R.drawable.dog_small && id != R.drawable.cat_small) {
//            Toast.makeText(SendStatusActivity.this, "Received an unknown pet icon id.",
//                    Toast.LENGTH_LONG).show();
//            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.unknown_sticker);
//        }

        NotificationCompat.Builder notifyBuild = new NotificationCompat.Builder(this, channelId)
                //"Notification icons must be entirely white."
                .setSmallIcon(R.drawable.heart)
                .setContentTitle("You received a GoalForIt pet status from " + senderName)
                .setContentText(senderName + "'s " + petType + " " + petName + " has " + heartCount
                        + "/10 hearts.")
                .setLargeIcon(myBitmap)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(myBitmap)
                        .bigLargeIcon(null))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                // hide the notification after its selected
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setShowWhen(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);

        }

//        notificationManager.notify(notificationId++, notifyBuild.build());

        // if only want to let the notification panel show the latest one notification, use this below
        notificationManager.notify(notificationId, notifyBuild.build());
        Log.i("SendStatusActivity", "receive notification");


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
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
                return;
        }

    }
}