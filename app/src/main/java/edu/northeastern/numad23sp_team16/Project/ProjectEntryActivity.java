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
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;

import edu.northeastern.numad23sp_team16.R;
import edu.northeastern.numad23sp_team16.models.Goal;
import edu.northeastern.numad23sp_team16.models.Message;
import edu.northeastern.numad23sp_team16.models.PetHealth;
import edu.northeastern.numad23sp_team16.models.User;


public class ProjectEntryActivity extends AppCompatActivity {
    TextView progressIndicator;
    LinearProgressIndicator bar;
    //FirebaseRecyclerAdapter adapter
    FirebaseRecyclerAdapter<Goal, GoalViewHolder> adapter;
    //data
    FirebaseRecyclerOptions<Goal> options;
    //display goals
    RecyclerView recyclerView;
    DatabaseReference goalsRef;
    //float percentageOfProgress;

    Query query;
    int checkedCount = 0;
    int invalidGoalCount = 0;
    DatabaseReference GoalFinishedStatusRef;

    // use the currentUser variable for the userId value -- Yutong
    ////get userId of currentUser
    //private String userId;

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
    private DatabaseReference goalFinishedStatusRef;
    private ValueEventListener goalFinishedStatusPostListener;
    private Map<Integer, Integer> dogHealth;
    private Map<Integer, Integer> catHealth;
    private Date currentDate;


    private static final String CURRENT_USER = "CURRENT_USER";
    private ValueEventListener queryEventListener;
    private final String LOGIN_TIME = "LOGIN_TIME";
    private final String EVENT_LISTENER = "EVENT_LISTENER";

    private String currentUser;
    private String userId;
    private String loginTime;
    //I set this to boolean at the beginning,but it always shows true.So this is used to store percentage of progress for easier test
    //in the db. The problem is it keeps updating and all data are stored in the db when the recyclerview is being loaded.
    int percentageOfToday = 0;

    //TODO:
    int allGoalsThisUser = 0;
    int allGoalsWeight=0;
    int invalidGoalWeight = 0;
    int checkedCountWithWeight=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_entry);

        // Retrieve currently logged in user's id from the database and the logged in time -- Yutong
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentUser = extras.getString(CURRENT_USER);
            userId = extras.getString(CURRENT_USER);
            loginTime = extras.getString(LOGIN_TIME);
        }
        Log.i("ProjectEntry", "currentUser from bundle: " + currentUser);
        Log.i("ProjectEntry", "loginTime from bundle: " + loginTime);

        recyclerView = findViewById(R.id.goal_recycler_view);
        progressIndicator = findViewById(R.id.goal_finish_text_view);
        bar = findViewById(R.id.progress_bar);

        // Get a reference to the "goals" node of this user in the database
        goalsRef = FirebaseDatabase.getInstance().getReference("FinalProject").child("FinalGoals").child(userId);
//        query = goalsRef.orderByChild("endDate").startAt(getCurrentDateStr());
        //put the query in a different thread at line 121,origianl code from line 129-222
        HandlerThread handlerThread = new HandlerThread("MyHandlerThread");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                query = goalsRef.orderByChild("endDate").startAt(getCurrentDateStr());
                queryEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Reset checkedCount,otherwise,checkedCount will be repeatedly added when loading view
                        percentageOfToday = 0;
                        checkedCount = 0;
                        invalidGoalCount = 0;
                        allGoalsThisUser = 0;
                        allGoalsWeight = 0;
                        invalidGoalWeight = 0;
                        checkedCountWithWeight=0;
                        //filter goals for current user
                        List<Goal> filteredGoals = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            allGoalsThisUser = (int) dataSnapshot.getChildrenCount();
                            Goal goal = snapshot.getValue(Goal.class);
                            if (goal != null) {
                                Log.d("Goal", "Goal: " + goal.getGoalName() + goal.getIcon() + ","+ goal.getPriority());
                                //filteredGoals.add(goal);
                                // check the lastCheckedInDate variable if it exists
                                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy", Locale.US);
                                String currentDateStr= dateFormat.format(new Date());
                                allGoalsWeight += goal.getPriority();
//                        Log.d("ProjectEntryActivity", "allGoalsWeight line 142: " + allGoalsWeight);

                                if(goal.getIsCheckedForToday() == 1 && goal.getUserId().equals(userId)
                                        && goal.getLastCheckedInDate() != null
                                        && goal.getLastCheckedInDate().equals(currentDateStr)){
                                    checkedCount++;
                                    checkedCountWithWeight += goal.getPriority();
                                }

                                //goals not started or expired(no need to consider any more since we have filtered them out)
                                try {
                                    if(isNotStarted(goal)){
                                        invalidGoalCount ++;
                                        invalidGoalWeight += goal.getPriority();
                                    }
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                        // Use the checkedCount value as needed
                        Log.d("checkedCount", "Checked count for user / all goals" + userId + ": " + checkedCount+"/"+allGoalsThisUser);

                        //initialize goal options,add setLifecycleOwner to automatically listen to changes.setLifecycleOwner(ProjectEntryActivity.this)
                        options = new FirebaseRecyclerOptions.Builder<Goal>().setQuery(query, Goal.class).build();
                        //instantiate adapter
                        adapter = new GoalAdapter(options);

                        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                            @Override
                            public void onChanged() {
                                super.onChanged();
                                updateProgressPercentage(adapter,checkedCount,invalidGoalCount);
                            }

                            @Override
                            public void onItemRangeChanged(int positionStart, int itemCount) {
                                super.onChanged();
                                updateProgressPercentage(adapter,checkedCount,invalidGoalCount);
                            }

                            @Override
                            public void onItemRangeInserted(int positionStart, int itemCount) {
                                super.onChanged();
                                updateProgressPercentage(adapter,checkedCount,invalidGoalCount);
                            }

                            @Override
                            public void onItemRangeRemoved(int positionStart, int itemCount) {
                                super.onChanged();
                                updateProgressPercentage(adapter,checkedCount,invalidGoalCount);
                            }
                        });
                        //this is the key to solving the problem-2hs
                        adapter.startListening();
                        //initialize recyclerview
                        recyclerView.setLayoutManager(new LinearLayoutManager(ProjectEntryActivity.this));
                        recyclerView.setAdapter(adapter);
                        //update percentage of progress
                        updateProgressPercentage(adapter,checkedCount,invalidGoalCount);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ProjectEntryActivity.this,"database error",Toast.LENGTH_SHORT).show();
                    }

                };

                // Add a ValueEventListener to the query
                query.addValueEventListener(queryEventListener);

                //check thread
                Thread thread = Thread.currentThread();
                Looper looper = Looper.myLooper();

                Log.d(TAG, "Current thread: " + thread.getName());
                Log.d(TAG, "Current looper: " + looper.getThread().getName());
                //quit thread
                handlerThread.quit();

            }
        });

        //check thread
        Thread thread = Thread.currentThread();
        Log.d(TAG, "Current thread out of the query thread: " + thread.getName());

        // Map pet health images to health status
        assignPetHealthImages();

        // Connect to firebase database
        mDatabase = FirebaseDatabase.getInstance().getReference("FinalProject");

        // Create reference to GoalFinishedStatus node in database
        goalFinishedStatusRef = mDatabase.child("GoalFinishedStatus");

        // Get user's pet's health node from database and create listener
        petHealthRef = mDatabase.child("PetHealth")
                .child("health" + currentUser);

        // Get today's date with time of 0
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);
        currentDate = calendar.getTime();

        // Put the perHealthRef in a new thread (previous line 293-311)
        HandlerThread petHealthThread = new HandlerThread("PetHealthThread");
        petHealthThread.start();
        Handler petHealthHandler = new Handler(petHealthThread.getLooper());
        petHealthHandler.post(new Runnable() {
            @Override
            public void run() {
                // Calculate and assign number of days it has been between current date and creation date
                petHealthRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String creationDate = dataSnapshot.child("creationDate").getValue(String.class);
                        totalDays = calculateNumberOfDays(creationDate);

                        Log.d(TAG, "onDataChange: creation date being called");
                        Log.d(TAG, "onDataChange: total days returned " + totalDays);

                        // Create listener for changes to GoalFinishedStatus
                        listenForGoalFinishedStatus();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "onCancelled: Database error retrieving creation date");
                    }
                });
                petHealthThread.quit();
            }
        });

//        // Calculate and assign number of days it has been between current date and creation date
//        petHealthRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String creationDate = dataSnapshot.child("creationDate").getValue(String.class);
//                totalDays = calculateNumberOfDays(creationDate);
//
//                Log.d(TAG, "onDataChange: creation date being called");
//                Log.d(TAG, "onDataChange: total days returned " + totalDays);
//
//                // Create listener for changes to GoalFinishedStatus
//                listenForGoalFinishedStatus();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.d(TAG, "onCancelled: Database error retrieving creation date");
//            }
//        });

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

    private String getCurrentDateStr() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy", Locale.US);
        String currentDateStr = dateFormat.format(new Date());
        return currentDateStr;
    }

    //update percentage of progress
    public void updateProgressPercentage(FirebaseRecyclerAdapter<Goal, GoalViewHolder> adapter, int checkedCount, int invalidGoalCount) {
        // Reset the variable to 0,it keeps updating in a day when the user clocks in.Each day the user will have a node with a record, if percentageOfToday == 100, the goal is all finished on the day.
        float percentageOfProgress = 0;
        float weightedPercentage = 0;
        if(allGoalsThisUser!=invalidGoalCount){
             percentageOfProgress = (allGoalsThisUser > 0) ? ((float) checkedCount / (allGoalsThisUser - invalidGoalCount) * 100) : 0;
        }
        bar.setProgress((int) percentageOfProgress);
        //this function is called ten times if there're ten item views in rc, checkedCount needs to be reset to 0 during each loading
        Log.d("progress", "Today's goal completion " + checkedCount + " / " + allGoalsThisUser);
        progressIndicator.setText("Today's goal completion " + (int) percentageOfProgress + "%");
        //Update in the db if the user has finished all goals today
        GoalFinishedStatusRef = FirebaseDatabase.getInstance().getReference("FinalProject").child("GoalFinishedStatus");
        Log.d("percentageOfProgress", "percentageOfProgress = " + percentageOfProgress);
        //This is the old version to store the percentage without considering priority.
       //caculate weighted percentage to store in the db
        if(allGoalsWeight != invalidGoalWeight){
            //This is the new version to store the percentage considering priority.
            weightedPercentage = (float)checkedCountWithWeight / (allGoalsWeight - invalidGoalWeight)*100;
            //Log.d("ProjectEntryActivity", "allGoalsWeight line 297: " + allGoalsWeight);
            //Log.d("ProjectEntryActivity", "checkedCountWithWeight line 297: " + checkedCountWithWeight);

            percentageOfToday = (int)weightedPercentage;
            // changed allGoalsWeight to checkedCountWithWeight at line 310 log msg part -- Yutong
            Log.d("checkedCountWithWeight / (allGoalsWeight - invalidGoalWeight)",checkedCountWithWeight +"/("+allGoalsWeight+" - "  +invalidGoalWeight+")" );

            //store date in the dateMap for easier access to add in the calendar,Which needs integer value.This is why the day,month,year value are set to int, not String
            Map<String, Integer> dateMap = getTheDay();
            storeInDB(percentageOfToday,userId,dateMap);
        }


    }

    private void storeInDB(int percentageOfToday, String userId, Map<String, Integer> dateMap) {
        // Create a new map to hold the date values and boolean value
        Map<String, Object> values = new HashMap<>();
        values.put("dateMap", dateMap);
        values.put("percentageOfToday", percentageOfToday);
        values.put("userId", userId);
        //must leave out "/", otherwise,it will be displayed as different nodes
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMddyy", Locale.US);
        String currentDateStr = dateFormat.format(new Date());
        //To make sure each user has a node for each day.
        String key =currentDateStr + userId;
        // percentage for the same day would be overwritten, only one final result is stored.
        GoalFinishedStatusRef.child(key).setValue(values);
    }




    private void listenForGoalFinishedStatus() {
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

                        // Only calculate into pet's health if not the current day and before current day
                        if (!currentDate.equals(date) && date.before(currentDate)) {
                            Log.d(TAG, "onDataChange: currentDate in if" + currentDate);
                            Log.d(TAG, "onDataChange: string date in if" + date);

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
        // remove messages child event listener
        messagesRef.removeEventListener(messagesChildEventListener);
        // remove query event listener -- Yutong
        query.removeEventListener(queryEventListener);

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
        // Pass currently logged in user and login time to create new goal
        Intent createGoalIntent = new Intent(ProjectEntryActivity.this,
                CreateNewGoalActivity.class);
        createGoalIntent.putExtra(CURRENT_USER, currentUser);
        createGoalIntent.putExtra(LOGIN_TIME, loginTime);
        startActivity(createGoalIntent);
    }

    public void startProgressActivity(View view) {
        // Pass currently logged in user and login time to progress page
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
                //userId = data.getStringExtra(CURRENT_USER);

                currentUser = data.getStringExtra(CURRENT_USER);
                loginTime = data.getStringExtra(LOGIN_TIME);
            }
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

    //Macee's solution for logging user out if date changes, so that activity restarts to get refreshed data when user logs back
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
            Intent intent = new Intent(ProjectEntryActivity.this, ProjectStartActivity.class);
            // close all activities in the call stack and bring it to the top
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
            Toast.makeText(ProjectEntryActivity.this, "You have been logged out. Please log in.",
                    Toast.LENGTH_LONG).show();

        }
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
        // remove query event listener -- Yutong
        query.removeEventListener(queryEventListener);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // remove messages child event listener
            messagesRef.removeEventListener(messagesChildEventListener);
            // remove query event listener -- Yutong
            query.removeEventListener(queryEventListener);
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private boolean hasExpired(Goal goal) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy", Locale.US);
        Date eDate = dateFormat.parse(goal.getEndDate());
        String currentDateStr = dateFormat.format(new Date());
        Date currentDate = dateFormat.parse(currentDateStr);
        return eDate.compareTo(currentDate)< 0;
    }

    private boolean isNotStarted(Goal goal) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy", Locale.US);
        Date sDate = dateFormat.parse(goal.getStartDate());
        String currentDateStr = dateFormat.format(new Date());
        Date currentDate = dateFormat.parse(currentDateStr);
        return sDate.compareTo(currentDate)> 0;
    }
    //store this dateMap in db for later display in progressActivity, store as int for later use
    private Map<String,Integer> getTheDay(){
        // Create a LocalDate object representing the current date
        LocalDate currentDate = LocalDate.now();
        // Retrieve the year, month, and day from the LocalDate object
        int year = currentDate.getYear();
        int month = currentDate.getMonthValue();
        int day = currentDate.getDayOfMonth();
        Map<String,Integer> dateMap = new HashMap<>();
        dateMap.put("year",year);
        dateMap.put("month",month);
        dateMap.put("day",day);
        return dateMap;
    }
}
