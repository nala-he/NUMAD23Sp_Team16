package edu.northeastern.numad23sp_team16.Project;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import edu.northeastern.numad23sp_team16.R;
import edu.northeastern.numad23sp_team16.models.Message;

// TODO: to be merged or replaced with the login/sign in page activity created by Yuan
public class ProjectEntryActivity extends AppCompatActivity {
    private static final String TAG = "SendStatusActivity";
    private String channelId = "notification_channel_0";
    private int notificationId = 0;
    private final int PERMISSION_REQUEST_CODE = 0;
    private DatabaseReference messagesRef;
    private ChildEventListener messagesChildEventListener;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy", Locale.US);
    private DatabaseReference mDatabase;
    private DatabaseReference petHealthRef;
    private float totalHealth;
    private int totalDays = 1;
    private ValueEventListener goalFinishedStatusPostListener;


    private static final String CURRENT_USER = "CURRENT_USER";
    private final String LOGIN_TIME = "LOGIN_TIME";
    private final String EVENT_LISTENER = "EVENT_LISTENER";

    private String currentUser;
    private String loginTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_entry);

        // Retrieve currently logged in user's id from the database and the logged in time -- Yutong
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentUser = extras.getString(CURRENT_USER);
            loginTime = extras.getString(LOGIN_TIME);
        }
        Log.i("ProjectEntry", "currentUser from bundle: " + currentUser);
        Log.i("ProjectEntry", "loginTime from bundle: " + loginTime);



        // Connect to firebase database
        mDatabase = FirebaseDatabase.getInstance().getReference("FinalProject");

        // Create reference to GoalFinishedStatus node in database
        DatabaseReference goalFinishedStatusRef = mDatabase.child("GoalFinishedStatus");

        // Get user's pet's health node from database and create listener
        petHealthRef = mDatabase.child("PetHealth")
                .child("health" + currentUser);

        // Get today's date with time of 0
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);
        Date currentDate = calendar.getTime();

        // Calculate and assign number of days it has been between current date and creation date
        petHealthRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String creationDate = dataSnapshot.child("creationDate").getValue(String.class);
                totalDays = calculateNumberOfDays(creationDate);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Database error retrieving creation date");
            }
        });

        // Create listener for changes to GoalFinishedStatus
        goalFinishedStatusPostListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalHealth = 0;

                // Iterate through GoalFinishedStatus nodes
                for (DataSnapshot data : snapshot.getChildren()) {
                    String user = data.child("userId").getValue(String.class);

                    // Check if goal finished status is associated with current user
                    if (Objects.equals(user, currentUser)) {
                        Log.d(TAG, "onDataChange: user " + user);
                        Log.d(TAG, "onDataChange: user current " + currentUser);

                        // Get the date
                        DataSnapshot dateMap = data.child("dateMap");
                        int year = dateMap.child("year").getValue(Integer.class);
                        int month = dateMap.child("month").getValue(Integer.class);
                        int day = dateMap.child("day").getValue(Integer.class);

                        // Convert stored date to Date object with time of 0
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month - 1, day);
                        calendar.set(Calendar.MILLISECOND, 0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.HOUR, 0);
                        Date date = calendar.getTime();

                        Log.d(TAG, "onDataChange: current date " + currentDate);
                        Log.d(TAG, "onDataChange: string date " + date);

                        // Only calculate into pet's health if not the current day
                        if (!currentDate.equals(date)) {

                            // Add to total health
                            totalHealth += data.child("percentageOfToday").getValue(Float.class);
                            Log.d(TAG, "onDataChange: total health " + totalHealth);

                            // Calculate average health from total health and number of days
                            float averageHealth = totalHealth / totalDays;
                            Log.d(TAG, "onDataChange: total days " + totalDays);

                            // Update average health for PetHealth node
                            petHealthRef.child("averageHealth").setValue(averageHealth);
                            Log.d(TAG, "onDataChange: averageHealth " + averageHealth);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Getting GoalFinishedStatus failed, log a message
                Log.w(TAG, "Error getting goal finished status from database");
            }
        };
        goalFinishedStatusRef.addValueEventListener(goalFinishedStatusPostListener);




        // TODO: change the hardcoded heartCount to user's pet heartCount from database
        int heartCount = 8;
        // receive the status notification if happen to be the currently logged in user
        // initialize messagesRef from firebase database
        messagesRef = FirebaseDatabase.getInstance().getReference("FinalProject").child("FinalProjectMessages");

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
                    if (message.receiverId.equals(currentUser) && messageTime.after(Timestamp.valueOf(loginTime))) {
                        // send and receive status message
                        Log.i("ProjectEntryActivity",
                                "receiverId: " + message.receiverId
                                        + " currentUser: " + currentUser
                                        + " sender: " + message.senderName);
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
        };

        messagesRef.addChildEventListener(messagesChildEventListener);
    }



    // Calculate total number of days between creation date and current day
    private int calculateNumberOfDays(String date) {
        // Convert creation date to Date object
        Date creationDate = null;
        try {
            creationDate = dateFormat.parse(date);
        } catch (ParseException e) {
            Log.d(TAG, "calculateNumberOfDays: Error parsing date");
        }

        Date currentDate = new Date();

        // Calculate duration between the current date and creation date
        long durationInMillis = currentDate.getTime() - creationDate.getTime();
        int differenceInDays = (int) TimeUnit.DAYS.convert(durationInMillis, TimeUnit.MILLISECONDS);
        Log.d(TAG, "calculateNumberOfDays: " + differenceInDays);

        if (differenceInDays == 0) {
            return 1;
        }
        return differenceInDays;
    }




    public void startProfileActivity(View view) {
        // Pass the current user info to Profile activity -- Yutong
        Intent intent = new Intent(ProjectEntryActivity.this, ProfileActivity.class);
        intent.putExtra(CURRENT_USER, currentUser);
        intent.putExtra(LOGIN_TIME, loginTime);
        // remove the event listener before going to the profile page in case that the user will log
        // out from the profile page
        messagesRef.removeEventListener(messagesChildEventListener);
        startActivity(intent);
    }

    public void startShareActivity(View view) {
        // Pass the current user info to Share activity -- Yutong
        Intent intent = new Intent(ProjectEntryActivity.this, ShareActivity.class);
        intent.putExtra(CURRENT_USER, currentUser);
        intent.putExtra(LOGIN_TIME, loginTime);
        startActivity(intent);
    }

    public void startCreateNewGoalActivity(View view) {
        // Pass currently logged in user to create new goal
        Intent createGoalIntent = new Intent(ProjectEntryActivity.this,
                CreateNewGoalActivity.class);
        createGoalIntent.putExtra(CURRENT_USER, currentUser);
        createGoalIntent.putExtra(LOGIN_TIME, loginTime);
        startActivity(createGoalIntent);
    }

    public void startProgressActivity(View view) {
        // Pass currently logged in user to progress page
        Intent progressIntent = new Intent(ProjectEntryActivity.this,
                ProgressActivity.class);
        progressIntent.putExtra(CURRENT_USER, currentUser);
        progressIntent.putExtra(LOGIN_TIME, loginTime);
        startActivity(progressIntent);
    }

    // Receive currently logged in user and login time from child activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data != null) {
                currentUser = data.getStringExtra(CURRENT_USER);
                loginTime = data.getStringExtra(LOGIN_TIME);
            }
        }
    }

    public void sendStatusMessage(String senderName, String petType, String petName, int heartCount) {

        // Build notification
        // Need to define a channel ID after Android Oreo
        int id = petType.equals("dog") ? R.drawable.dog_small : R.drawable.cat_small;
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // remove messages child event listener if user went back to log in page
        messagesRef.removeEventListener(messagesChildEventListener);

        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // remove messages child event listener
            messagesRef.removeEventListener(messagesChildEventListener);
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}