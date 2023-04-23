package edu.northeastern.numad23sp_team16.Project;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import edu.northeastern.numad23sp_team16.A6.WebServiceActivity;
import edu.northeastern.numad23sp_team16.R;
import edu.northeastern.numad23sp_team16.models.Message;
import edu.northeastern.numad23sp_team16.models.PetHealth;
import edu.northeastern.numad23sp_team16.models.User;

public class ProgressActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView petName;
    private ImageView petImage;
    private RecyclerView petHealthRecyclerView;
    private HeartAdapter heartAdapter;
    private List<Integer> listOfHearts;
    private int petHealth;
    private static final int DENOMINATOR = 10;
    private MaterialCalendarView calendarHistory;
    private TextView petHealthInfo;
    private Map<Integer, Integer> dogHealth;
    private Map<Integer, Integer> catHealth;
    private String petType;

    // Firebase database
    private DatabaseReference mDatabase;
    private static final String CURRENT_USER = "CURRENT_USER";
    private final String LOGIN_TIME = "LOGIN_TIME";
    private String currentUser;
    private String loginTime;
    private User currentUserObject;
    private PetHealth currentUserPetHealth;
    private ValueEventListener userPostListener;
    private DatabaseReference petHealthRef;
    private ValueEventListener petHealthPostListener;
    private ValueEventListener goalFinishedStatusPostListener;
    private DatabaseReference goalFinishedStatusRef;
    private DatabaseReference userRef;

    private DatabaseReference messagesRef;
    private ChildEventListener messagesChildEventListener;

    private int notificationId = 1;

    private final int PERMISSION_REQUEST_CODE = 0;
    private String channelId = "notification_channel_0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        Date date = new Date();
        Timestamp currentTime = new Timestamp(date.getTime());

        // Get currently logged in user and login time
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentUser = extras.getString(CURRENT_USER);
            loginTime = extras.getString(LOGIN_TIME);
            notificationId = extras.getInt("notification_id");

        }
        Log.i("ProgressActivity onCreate", "notification_id: " + notificationId);

        // Set custom action bar with back button
        toolbar = findViewById(R.id.progress_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Get pet name and image from view
        petName = findViewById(R.id.progress_pet_name);
        petImage = findViewById(R.id.progress_pet_image);

        // Assign pet health images to pet health
        assignPetHealthImages();

        // Connect to firebase database
        mDatabase = FirebaseDatabase.getInstance().getReference("FinalProject");

        // Get user's attributes from database and create listener
        userRef = mDatabase.child("FinalProjectUsers").child(currentUser);

        userPostListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Get User object and use the values to update the UI
                currentUserObject = snapshot.getValue(User.class);

                // Set pet name for current user
                petName.setText(currentUserObject.getPetName());

                // Set pet type for current user
                petType = currentUserObject.getPetType();

                // Set pet image
                if (Objects.equals(currentUserObject.getPetType(), "dog")) {
                    // Assign appropriate dog image
                    petHealthImage(dogHealth);
                }
                else if (Objects.equals(currentUserObject.getPetType(), "cat")) {
                    // Assign appropriate cat image
                    petHealthImage(catHealth);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Getting User failed, log a message
                Log.w(TAG, "Error getting user from database");
            }
        };
        userRef.addValueEventListener(userPostListener);

        // Get user's pet's health node from database and create listener
        petHealthRef = mDatabase.child("PetHealth")
                .child("health" + currentUser);

        petHealthPostListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Get PetHealth object and use the average health value to update UI
                currentUserPetHealth = snapshot.getValue(PetHealth.class);

                // Set current user's pet's overall health
                if (currentUserPetHealth != null) {
                    float averageHealth = currentUserPetHealth.getAverageHealth();
                    petHealth = Math.round(averageHealth / DENOMINATOR);

                    Log.d(TAG, "onDataChange: PET HEALTH = " + petHealth);

                    // Update # of hearts in hearts recycler view adapter
                    heartAdapter.setNumberOfHearts(petHealth);

                    // Notify hearts recycler view adapter of change in pet health
                    heartAdapter.notifyDataSetChanged();

                    // Update message about pet health to user
                    petHealthMessage();

                    // Update pet image
                    if (Objects.equals(petType, "dog")) {
                        // Assign appropriate dog image
                        petHealthImage(dogHealth);
                    }
                    else if (Objects.equals(petType, "cat")) {
                        // Assign appropriate cat image
                        petHealthImage(catHealth);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Getting PetHealth failed, log a message
                Log.w(TAG, "Error getting pet's health from database");
            }
        };
        petHealthRef.addValueEventListener(petHealthPostListener);

        // Recycler view to show hearts (pet health)
        petHealthRecyclerView = findViewById(R.id.progress_pet_health);
        listOfHearts = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            listOfHearts.add((Integer) R.drawable.heart);
        }

        // Set adapter for pet health recycler view
        heartAdapter = new HeartAdapter(getApplicationContext(), listOfHearts, petHealth);
        petHealthRecyclerView.setAdapter(heartAdapter);

        // Set layout of hearts in recycler view
        petHealthRecyclerView.setLayoutManager(new GridLayoutManager(ProgressActivity.this, 5));

        // Provide message to user depending on health condition of pet
        petHealthMessage();

        // Create reference to GoalFinishedStatus node in database
        goalFinishedStatusRef = mDatabase.child("GoalFinishedStatus");

        // Create listener for changes to GoalFinishedStatus
        goalFinishedStatusPostListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                calendarHistory.removeDecorators();

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

                        // Add pink dot to calendar if percentage of completion is 100
                        if (data.child("percentageOfToday").getValue(Float.class) == 100) {

                            // Add pink dot to calendar if user completed all goals for that date
                            updateCalendar(year, month, day);
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


        // Set up calendar to view past history
        calendarHistory = findViewById(R.id.completion_history_calendar);

        // Set date selected to current date
        calendarHistory.setDateSelected(CalendarDay.today(), true);

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
        Log.i("ProgressActivity", "receive notification " + notificationId);


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

    @SuppressLint("SetTextI18n")
    private void petHealthMessage() {
        // Set text below hearts to remind user what health condition their pet is in
        petHealthInfo = findViewById(R.id.pet_health_info);
        if (petHealth == 10) {
            // 10 hearts
            petHealthInfo.setText("Your pet is in the best health condition! Keep it up!");

        } else if (petHealth >= 5 && petHealth < 10) {
            // 5-9 hearts
            petHealthInfo.setText("Keep up the good work! Try to bring your pet's health back up " +
                    "to 10 hearts!");
        } else if (petHealth >= 2 && petHealth < 5) {
            // 2-4 hearts
            petHealthInfo.setText("Uh oh...your pet's health is getting low. Remember to complete " +
                    "all of your daily goals to bring it back up!");
        } else if (petHealth == 1) {
            // 1 heart
            petHealthInfo.setText("Watch out! Your pet is in critical condition. Remember to complete " +
                    "all of your daily goals!");
        } else if (petHealth == 0) {
            // 0 hearts
            petHealthInfo.setText("Your pet has died. Complete your goals " +
                    "everyday to bring it back to life.");
        }
    }

    private void petHealthImage(Map<Integer, Integer> mappedPetImages) {
        // Set pet health image depending on pet's health condition and type of pet chosen
        if (petHealth == 10) {
            // 10 hearts
            petImage.setImageResource(mappedPetImages.get(10));

        } else if (petHealth >= 5 && petHealth < 10) {
            // 5-9 hearts
            petImage.setImageResource(mappedPetImages.get(5));

        } else if (petHealth >= 2 && petHealth < 5) {
            // 2-4 hearts
            petImage.setImageResource(mappedPetImages.get(2));

        } else if (petHealth == 1) {
            // 1 heart
            petImage.setImageResource(mappedPetImages.get(1));

        } else if (petHealth == 0) {
            // 0 hearts
            petImage.setImageResource(mappedPetImages.get(0));

        }
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

    // Navigate to share pet status with friends screen
    public void onSharePetStatus(View view) {
        // pass currently logged in user and log in time to Share activity
        Intent intent = new Intent(ProgressActivity.this, ShareActivity.class);
        // pass the current user id and login time
        intent.putExtra(CURRENT_USER, currentUser);
        intent.putExtra(LOGIN_TIME, loginTime);
        intent.putExtra("notification_id", notificationId);

//        // close all activities in the call stack and bring it to the top
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // remove listeners -- Yutong
        petHealthRef.removeEventListener(petHealthPostListener);
        goalFinishedStatusRef.removeEventListener(goalFinishedStatusPostListener);
        userRef.removeEventListener(userPostListener);
        messagesRef.removeEventListener(messagesChildEventListener);

        startActivity(intent);
    }

    public void updateCalendar(int year, int month, int day) {
        // Create calendar date
        CalendarDay date = CalendarDay.from(year, month, day);

        // Add pink dot to calendar for given date
        DayDecorator dayDecorator = new DayDecorator(date);
        calendarHistory.addDecorator(dayDecorator);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Get today's date with time of 0
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);
        Date currentDate = calendar.getTime();

        int year = Integer.parseInt(loginTime.substring(0, 4));
        int month = Integer.parseInt(loginTime.substring(5, 7));
        int day = Integer.parseInt(loginTime.substring(8, 10));

        // Convert login time to Date object with time of 0
        Log.d(TAG, "onResume: login time " + loginTime);
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR, 0);
        Date loginDate = cal.getTime();

        // Log user out if date has changed
        if (!currentDate.equals(loginDate)) {
            Intent intent = new Intent(ProgressActivity.this, ProjectStartActivity.class);
            // close all activities in the call stack and bring it to the top
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // remove listeners -- Yutong
            petHealthRef.removeEventListener(petHealthPostListener);
            goalFinishedStatusRef.removeEventListener(goalFinishedStatusPostListener);
            userRef.removeEventListener(userPostListener);
            messagesRef.removeEventListener(messagesChildEventListener);

            finish();
            startActivity(intent);
            Toast.makeText(ProgressActivity.this, "You have been logged out",
                    Toast.LENGTH_LONG).show();
        }
    }

    //This is added by Yuan to get the progress data from db.If the goal is all finished with finishStatus.getPercentageOfToday() == 100, the day is marked red.-Yuan
//    private void getDaysAllCompletedFromDB(List<CalendarDay> completedGoalsDates) {
//        DatabaseReference GoalFinishedStatusRef = FirebaseDatabase.getInstance().getReference("FinalProject").child("GoalFinishedStatus");
//        Query daysGoalAllFinishedQuery = GoalFinishedStatusRef.orderByChild("userId").equalTo(currentUser);
//        daysGoalAllFinishedQuery.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.exists()){
//                    for (DataSnapshot  goalSnapshot : snapshot.getChildren()) {
//                        FinishStatus finishStatus = goalSnapshot.getValue(FinishStatus.class);
//                        //add another condition to filter nodes with percentageOfToday == 100
//                        if(finishStatus.getPercentageOfToday() == 100){
//                            Map<String, Integer> dateMap = finishStatus.getDateMap();
//                            // Use the dateMap as needed
//                            int day = dateMap.get("day");
//                            int month = dateMap.get("month");
//                            int year = dateMap.get("year");
//                            Log.d("FinishStatus", "day: " + day + ", month: " + month + ", year: " + year);
//                            completedGoalsDates.add(CalendarDay.from(year, month, day));
//                        }
//
//                    }
//                    // Add dots to calendar on dates the user completed all daily goals
//                    for (int i = 0; i < completedGoalsDates.size(); i++) {
//                        DayDecorator dayDecorator = new DayDecorator(completedGoalsDates.get(i));
//                        calendarHistory.addDecorator(dayDecorator);
//                    }
//                } else {
//                    Log.d("database","No days info related to the user found in the db.");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    // Pass currently logged in user and log in time back when swipe back
    @Override
    public void onBackPressed() {
        Intent homeIntent = new Intent(ProgressActivity.this, ProjectEntryActivity.class);
        // Add currently logged in user and log in time to intent
        homeIntent.putExtra(CURRENT_USER, currentUser);
        homeIntent.putExtra(LOGIN_TIME, loginTime);
        homeIntent.putExtra("notification_id", notificationId);

        setResult(Activity.RESULT_OK, homeIntent);
        /// remove listeners -- Yutong
        petHealthRef.removeEventListener(petHealthPostListener);
        goalFinishedStatusRef.removeEventListener(goalFinishedStatusPostListener);
        userRef.removeEventListener(userPostListener);
        messagesRef.removeEventListener(messagesChildEventListener);

        super.onBackPressed();
    }

    // Pass currently logged in user and log in time back when click on triangle back
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the custom action bar's back button
            case android.R.id.home:
                Intent homeIntent = new Intent(ProgressActivity.this, ProjectEntryActivity.class);
                // Add currently logged in user and log in time to intent
                homeIntent.putExtra(CURRENT_USER, currentUser);
                homeIntent.putExtra(LOGIN_TIME, loginTime);
                homeIntent.putExtra("notification_id", notificationId);

                setResult(Activity.RESULT_OK, homeIntent);

                // remove listeners -- Yutong
                petHealthRef.removeEventListener(petHealthPostListener);
                goalFinishedStatusRef.removeEventListener(goalFinishedStatusPostListener);
                userRef.removeEventListener(userPostListener);
                messagesRef.removeEventListener(messagesChildEventListener);

                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}