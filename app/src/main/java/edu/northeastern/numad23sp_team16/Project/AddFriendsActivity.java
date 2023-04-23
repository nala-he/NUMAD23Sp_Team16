package edu.northeastern.numad23sp_team16.Project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import edu.northeastern.numad23sp_team16.R;
import edu.northeastern.numad23sp_team16.models.Friend;
import edu.northeastern.numad23sp_team16.models.Message;
import edu.northeastern.numad23sp_team16.models.User;

public class AddFriendsActivity extends AppCompatActivity {

    private RecyclerView userListRecyclerView;
    private UsernameAdapter usernameAdapter;
    private ArrayList<Username> usersList = new ArrayList<>();
    private List<String> newFriendIdsList = new ArrayList<>();
    private List<String> preFriendIdsList = new ArrayList<>();

    private final String USERS_LIST = "USERS_LIST";
    private final String CURRENT_USER = "CURRENT_USER";
    private final String LOGIN_TIME = "LOGIN_TIME";

    private String currentUser;
    private String loginTime;

    private DatabaseReference usersRef;
    private DatabaseReference friendsRef;
    private static int friendIdCounter = 1;
    private DatabaseReference mDatabase;

    private String channelId = "notification_channel_0";

    private DatabaseReference messagesRef;
    private ChildEventListener messagesChildEventListener;
    private Map<Integer, Integer> dogHealth;
    private Map<Integer, Integer> catHealth;
    private int notificationId = 1;

    private final int PERMISSION_REQUEST_CODE = 0;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_status);
        Date date = new Date();
        Timestamp currentTime = new Timestamp(date.getTime());

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
        title.setText("Add New Friends");

        // Retrieve currently logged in user -- Yutong
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentUser = extras.getString(CURRENT_USER);
            loginTime = extras.getString(LOGIN_TIME);
            notificationId = extras.getInt("notification_id");
        }

        Log.i("AddFriendsActivity onCreate", "notification_id: " + notificationId);

        // initialize usersRef and friendsRef from firebase database
        usersRef = FirebaseDatabase.getInstance().getReference("FinalProject").child("FinalProjectUsers");
        friendsRef = FirebaseDatabase.getInstance().getReference("FinalProject").child("FinalProjectFriends");

        // check if there is a previously saved instance
        if (savedInstanceState == null) {
            // get preFriendIdsList data from firebase
            getFriendIdsListData();
            // get usersList data and update the previously selected friends status
            getUsersListData();
        }

        // initialize views
        userListRecyclerView = findViewById(R.id.userlist_recyclerview);
        userListRecyclerView.setLayoutManager(new LinearLayoutManager(AddFriendsActivity.this));


        TextView userListTitle = findViewById(R.id.user_list_title);
        userListTitle.setText(R.string.list_of_users);

        Button addButton = findViewById(R.id.add_selected_friends_button);
        Button sendButton = findViewById(R.id.send_status_button);
        sendButton.setVisibility(View.INVISIBLE);
        addButton.setText(R.string.add_friends);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Username username : usersList) {
                    if (username.isSelected()) {
                        newFriendIdsList.add(username.getUserId());
                    }
                }
                if (newFriendIdsList.size() == 0) {
                    Toast.makeText(AddFriendsActivity.this, "Did not add any friends.",
                            Toast.LENGTH_LONG).show();
                } else {
                    // TODO: save new friend items from newFriendIdsList to the firebase database
                    for (String each : newFriendIdsList) {
                        Friend newFriend = new Friend(currentUser, each);
                        // add a child node to use the time as part of the unique friend id, format "friend16082271023"
                        String time = String.valueOf(System.currentTimeMillis()/1000);
                        String friendId = "friend" + time + friendIdCounter++;
                        friendsRef.child(friendId).setValue(newFriend);
                    }
                    Toast.makeText(AddFriendsActivity.this, "Saved selected users as friends.",
                            Toast.LENGTH_LONG).show();
                }

                Intent intent = new Intent(AddFriendsActivity.this, ShareActivity.class);
                // close all the activities in the call stack above ShareActivity and bring it to
                // the top of the call stack
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                // pass the current user id back to share activity
                intent.putExtra(CURRENT_USER, currentUser);
                intent.putExtra(LOGIN_TIME, loginTime);
                intent.putExtra("notification_id", notificationId);

                messagesRef.removeEventListener(messagesChildEventListener);

                startActivity(intent);
            }
        });
        // Map pet health images to health status
        assignPetHealthImages();

        // Connect to firebase database
        mDatabase = FirebaseDatabase.getInstance().getReference("FinalProject");

        // receive the status notification if happen to be the currently logged in user
        // initialize messagesRef from firebase database
        messagesRef = mDatabase.child("FinalProjectMessages");

        // Create new child event listener for messages
        messagesChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String s) {
                Log.i("ProjectEntry", "currentUser in listener: " + currentUser);
                Log.i("ProjectEntry", "loginTime in listener: " + loginTime);

                Message message = snapshot.getValue(Message.class);
                if (message != null) {
                    Timestamp messageTime = Timestamp.valueOf(message.timeStamp);
                    Log.i("ProjectEntryActivity", " currentUser: " + currentUser +
                            " message time: " + messageTime + " login time: " + loginTime);
//                    if (message.receiverId.equals(currentUser) && messageTime.after(Timestamp.valueOf(loginTime))) {

                    if (message.receiverId.equals(currentUser) && messageTime.after(currentTime)) {
                        // send and receive status message
                        Log.i("ProjectEntryActivity",
                                "receiverId: " + message.receiverId
                                        + " currentUser: " + currentUser
                                        + " sender: " + message.senderName);
                        sendStatusMessage(message.senderName, message.petType,
                                message.petName, message.heartCount);
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
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors here
                Log.e("ProjectEntryActivity", "Error retrieving goals: " + databaseError.getMessage());
            }
        };

        messagesRef.addChildEventListener(messagesChildEventListener);

    }

    public void sendStatusMessage(String senderName, String petType, String petName, int heartCount) {

        // Build notification
        // Need to define a channel ID after Android Oreo
        // Get pet image depending on pet type and heart count
        int id = petType.equals("dog") ? petHealthImage(dogHealth, heartCount) : petHealthImage(catHealth, heartCount);

//        int id = Integer.parseInt(petIconId);
        Bitmap myBitmap = BitmapFactory.decodeResource(getResources(), id);

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

        notificationManager.notify(notificationId++, notifyBuild.build());

        // if only want to let the notification panel show the latest one notification, use this below
//        notificationManager.notify(notificationId, notifyBuild.build());
        Log.i("AddFriendsActivity", "receive notification " + notificationId);


    }

    private void assignPetHealthImages() {
        // Map dog's health to appropriate image
        dogHealth = new HashMap<>();
        dogHealth.put(10, R.drawable.dog_10);
        dogHealth.put(9, R.drawable.dog_5_9);
        dogHealth.put(8, R.drawable.dog_5_9);
        dogHealth.put(7, R.drawable.dog_5_9);
        dogHealth.put(6, R.drawable.dog_5_9);
        dogHealth.put(5, R.drawable.dog_5_9);
        dogHealth.put(4, R.drawable.dog_2_4);
        dogHealth.put(3, R.drawable.dog_2_4);
        dogHealth.put(2, R.drawable.dog_2_4);
        dogHealth.put(1, R.drawable.dog_1);
        dogHealth.put(0, R.drawable.dog_0);

        // Map cat's health to appropriate image
        catHealth = new HashMap<>();
        catHealth.put(10, R.drawable.cat_10);
        catHealth.put(9, R.drawable.cat_5_9);
        catHealth.put(8, R.drawable.cat_5_9);
        catHealth.put(7, R.drawable.cat_5_9);
        catHealth.put(6, R.drawable.cat_5_9);
        catHealth.put(5, R.drawable.cat_5_9);
        catHealth.put(4, R.drawable.cat_2_4);
        catHealth.put(3, R.drawable.cat_2_4);
        catHealth.put(2, R.drawable.cat_2_4);
        catHealth.put(1, R.drawable.cat_1);
        catHealth.put(0, R.drawable.cat_0);
    }

    private Integer petHealthImage(Map<Integer, Integer> mappedPetImages, int petHealth) {
        // Return appropriate pet health image depending on pet's health condition and type of pet chosen
        if (petHealth == 10) {
            // 10 hearts
            return mappedPetImages.get(10);

        } else if (petHealth >= 5 && petHealth < 10) {
            // 5-9 hearts
            return mappedPetImages.get(5);

        } else if (petHealth >= 2 && petHealth < 5) {
            // 2-4 hearts
            return mappedPetImages.get(2);

        } else if (petHealth == 1) {
            // 1 heart
            return mappedPetImages.get(1);

        } else if (petHealth == 0) {
            // 0 hearts
            return mappedPetImages.get(0);

        }
        return mappedPetImages.get(10);
    }

    // this event will enable the back function to the back button on press in customized action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            messagesRef.removeEventListener(messagesChildEventListener);

            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // obtain preFriendIdsList from firebase database
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
                                preFriendIdsList.add(friendId);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void getUsersListData() {
        // Connect with firebase
        usersRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            String userId = Objects.requireNonNull(data.getKey());
                            String userName = Objects.requireNonNull(data.getValue(User.class)).getUsername();
                            Username nameItem = new Username(userName, userId);
                            // check if the user is the currentUser
                            if (userId.equals(currentUser)) {
                                continue;
                            }
                            // check if the user is already a friend in the preFriendIdsList from database
                            if (preFriendIdsList != null
                                    && preFriendIdsList.stream().anyMatch(each -> each.equals(userId))) {
                                nameItem.setSelected(true);
                            }
                            // check if the unselected name is already in the usersList
                            if (!nameItem.isSelected() && usersList.stream()
                                    .noneMatch(each -> each.getName().equals(nameItem.getName()))) {
                                usersList.add(nameItem);
                            }
                        }
                        usernameAdapter = new UsernameAdapter(usersList);
                        userListRecyclerView.setAdapter(usernameAdapter);

                        if (usersList.isEmpty() && !preFriendIdsList.isEmpty()) {
                            Toast.makeText(AddFriendsActivity.this,
                                    "You have already added all users as friends. No more to be added!",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // store usersList selected status
        outState.putParcelableArrayList(USERS_LIST, usersList);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // restore usersList selected status
        usersList = savedInstanceState.getParcelableArrayList(USERS_LIST);
        getFriendIdsListData();
        getUsersListData();
    }
}