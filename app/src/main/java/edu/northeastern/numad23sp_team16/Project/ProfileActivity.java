package edu.northeastern.numad23sp_team16.Project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import edu.northeastern.numad23sp_team16.R;
import edu.northeastern.numad23sp_team16.models.Message;
import edu.northeastern.numad23sp_team16.models.User;

public class ProfileActivity extends AppCompatActivity {
    private final String CURRENT_USER = "CURRENT_USER";
    private final String LOGIN_TIME = "LOGIN_TIME";
    private final String USERNAME = "USERNAME";
    private final String PASSWORD = "PASSWORD";
    private final String EMAIL = "EMAIL";
    private final String PET_TYPE = "PET_TYPE";
    private final String PET_NAME = "PET_NAME";
    private String username;
    private DatabaseReference usersRef;
    private String email;
    private String password;
    private String petType;
    private String petName;
    private TextInputEditText username_input;
    private TextInputEditText password_input;
    private TextInputEditText email_input;
    private TextInputEditText petname_input;
    private RadioButton dog_button;
    private RadioButton cat_button;
    // current logged-in user's userId in the database
    private String currentUser;
    // date/time current user logged in
    private String loginTime;

    private ValueEventListener usersListener;
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
        setContentView(R.layout.activity_profile);
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

        // set user profile values to textInputEditText
        username_input = findViewById(R.id.username_input);
        // make username unchangeable per Yuan's suggestion
        username_input.setEnabled(false);
        password_input = findViewById(R.id.password_input);
        email_input = findViewById(R.id.email_input);
        petname_input = findViewById(R.id.petname_input);
        dog_button = findViewById(R.id.dog_radio_button);
        cat_button = findViewById(R.id.cat_radio_button);

        // Retrieve currently logged in user and login time
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentUser = extras.getString(CURRENT_USER);
            loginTime = extras.getString(LOGIN_TIME);
            notificationId = extras.getInt("notification_id");

        }
        Log.i("Profile onCreate", "notification_id: " + notificationId);

        // Connect with firebase
        usersRef = FirebaseDatabase.getInstance().getReference("FinalProject").child("FinalProjectUsers");
        usersListener = new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (savedInstanceState == null) {
                    username = dataSnapshot.child("username").getValue(String.class);
                    email = dataSnapshot.child("email").getValue(String.class);
                    petName = dataSnapshot.child("petName").getValue(String.class);
                    petType = dataSnapshot.child("petType").getValue(String.class);
                    password = dataSnapshot.child("password").getValue(String.class);

                    // Set the input field default data using the current user's info from the database -- Yutong
                    username_input.setText(username);
                    password_input.setText(password);
                    email_input.setText(email);
                    petname_input.setText(petName);
                    if (Objects.equals(petType, "dog")) {
                        dog_button.setChecked(true);
                    } else {
                        cat_button.setChecked(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        usersRef.child(currentUser).addListenerForSingleValueEvent(usersListener);

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
//                        if (message.receiverId.equals(currentUser) && messageTime.after(Timestamp.valueOf(loginTime))) {

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
        Log.i("ProfileActivity", "receive notification " + notificationId);


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
            usersRef.removeEventListener(usersListener);
            messagesRef.removeEventListener(messagesChildEventListener);

            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickSaveUpdates(View view) {

        // save updates to database and then navigate back to home screen
        if (!isAllFieldsFilled()) {
            Toast.makeText(getApplicationContext(), "Please fill in all fields!", Toast.LENGTH_SHORT).show();
        } else {
            // save the updates to database
            updateChangedFields();
            Toast.makeText(ProfileActivity.this, "Saved profile updates.",
                    Toast.LENGTH_LONG).show();
            // pass the current user's username to entry activity, in case it's getting updated
            Intent intent = new Intent(ProfileActivity.this, ProjectEntryActivity.class);
//            // close all activities in the call stack and bring it to the top
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            intent.putExtra(CURRENT_USER, currentUser);
            // Save log in time to intent
            intent.putExtra(LOGIN_TIME, loginTime);
            intent.putExtra("notification_id", notificationId);

            usersRef.removeEventListener(usersListener);
            messagesRef.removeEventListener(messagesChildEventListener);

            startActivity(intent);
        }
    }

    private boolean isAllFieldsFilled() {
        return username_input.getText() != null && password_input.getText() != null
                && petname_input.getText() != null && email_input.getText() != null
                && (cat_button.isChecked() || dog_button.isChecked())
                && !username_input.getText().toString().trim().isEmpty()
                && !petname_input.getText().toString().trim().isEmpty()
                && !email_input.getText().toString().trim().isEmpty()
                && !password_input.getText().toString().trim().isEmpty();
    }

    private void updateChangedFields() {
        String usernameOnSave = Objects.requireNonNull(username_input.getText()).toString().trim();
        String petNameOnSave = Objects.requireNonNull(petname_input.getText()).toString().trim();
        String passwordOnSave = Objects.requireNonNull(password_input.getText()).toString().trim();
        String emailOnSave = Objects.requireNonNull(email_input.getText()).toString().trim();
        String petTypeOnSave = dog_button.isChecked() ? "dog" : "cat";
        usersRef.child(currentUser).child("username")
                .setValue(usernameOnSave);

        usersRef.child(currentUser).child("petName")
                .setValue(petNameOnSave);

        usersRef.child(currentUser).child("password")
                .setValue(passwordOnSave);

        usersRef.child(currentUser).child("email")
                .setValue(emailOnSave);

        usersRef.child(currentUser).child("petType")
                .setValue(petTypeOnSave);
    }

    public void onClickLogOut(View view) {
        Intent intent = new Intent(ProfileActivity.this, ProjectStartActivity.class);
//        // close all the activities in the call stack above ShareActivity and bring it to
//        // the top of the call stack
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        usersRef.removeEventListener(usersListener);
        messagesRef.removeEventListener(messagesChildEventListener);

        finish();
        startActivity(intent);
        Toast.makeText(ProfileActivity.this, "The user is successfully logged out.",
                Toast.LENGTH_LONG).show();

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(USERNAME, String.valueOf(username_input.getText()));
        outState.putString(PASSWORD, String.valueOf(password_input.getText()));
        outState.putString(EMAIL, String.valueOf(email_input.getText()));
        String petType_input = dog_button.isChecked() ? "dog" : "cat";
        outState.putString(PET_TYPE, petType_input);
        outState.putString(PET_NAME, String.valueOf(petname_input.getText()));
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // restore edited status
        username = savedInstanceState.getString(USERNAME);
        password = savedInstanceState.getString(PASSWORD);
        email = savedInstanceState.getString(EMAIL);
        petType = savedInstanceState.getString(PET_TYPE);
        petName = savedInstanceState.getString(PET_NAME);
        username_input.setText(username);
        password_input.setText(password);
        email_input.setText(email);
        petname_input.setText(petName);
        if (petType.equals("dog")) {
            dog_button.setChecked(true);
        } else {
            cat_button.setChecked(true);
        }
    }

}
