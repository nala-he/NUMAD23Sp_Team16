package edu.northeastern.numad23sp_team16.Project;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.northeastern.numad23sp_team16.R;
import edu.northeastern.numad23sp_team16.models.Goal;
import edu.northeastern.numad23sp_team16.models.Message;

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
    float percentageOfProgress;
    int checkedCount = 0;
    int invalidGoalCount = 0;

    // use the currentUser variable for the userId value -- Yutong
    ////get userId of currentUser
    //private String userId;

    private static final String TAG = "SendStatusActivity";
    private String channelId = "notification_channel_0";
    private int notificationId = 0;
    private final int PERMISSION_REQUEST_CODE = 0;
    private DatabaseReference messagesRef;
    private ChildEventListener messagesChildEventListener;

    private final String CURRENT_USER = "CURRENT_USER";
    private final String LOGIN_TIME = "LOGIN_TIME";

    private String currentUser;
    private String userId;
    private String loginTime;


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
        // Get a reference to the "goals" node in the database
        goalsRef = FirebaseDatabase.getInstance().getReference("FinalProject").child("Goals");

        
        // replace the userId variable with the name currentUser -- Yutong
        Query query = goalsRef.orderByChild("userId").equalTo(currentUser);

        // Add a ValueEventListener to the query
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Reset checkedCount,otherwise,checkedCount will be repeatedly added when loading view
                checkedCount = 0;
                invalidGoalCount = 0;
                //filter goals for current user
                List<Goal> filteredGoals = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Goal goal = snapshot.getValue(Goal.class);
                    if (goal != null) {
                        Log.d("Goal", "Goal: " + goal.getGoalName() + goal.getIcon() + ","+ goal.getPriority());
                        filteredGoals.add(goal);
                        if(goal.getIsCheckedForToday() == 1 && goal.getUserId().equals(userId)){
                            checkedCount++;
                            Log.d("checkedCount", String.valueOf(checkedCount));
                        }

                            //goals not started or expired
                        try {
                            if(hasExpired(goal) || isNotStarted(goal)){
                                invalidGoalCount ++;
                            }
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }


                    }
                }
                // Use the checkedCount value as needed
                Log.d("checkedCount", "Checked count for user " + userId + ": " + checkedCount);

                //initialize goal options
                options = new FirebaseRecyclerOptions.Builder<Goal>().setQuery(query, Goal.class).build();
                //instantiate adapter
                adapter = new GoalAdapter(options);

                adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                    @Override
                    public void onChanged() {
                        updateProgressPercentage(adapter,checkedCount,invalidGoalCount);
                    }

                    @Override
                    public void onItemRangeChanged(int positionStart, int itemCount) {updateProgressPercentage(adapter,checkedCount,invalidGoalCount);}

                    @Override
                    public void onItemRangeInserted(int positionStart, int itemCount) {updateProgressPercentage(adapter,checkedCount,invalidGoalCount);}

                    @Override
                    public void onItemRangeRemoved(int positionStart, int itemCount) {updateProgressPercentage(adapter,checkedCount,invalidGoalCount);}
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

        });


        // TODO: change the hardcoded heartCount to user's pet heartCount from database
        int heartCount = 8;
        // receive the status notification if happen to be the currently logged in user
        // initialize messagesRef from firebase database
        messagesRef = FirebaseDatabase.getInstance().getReference("FinalProject").child("FinalProjectMessages");

        // Create new child event listener for messages
        messagesChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String s) {
                Log.i("ProjectEntry", "currentUser in line 71: " + currentUser);
                Log.i("ProjectEntry", "loginTime in line 72: " + loginTime);

                Message message = snapshot.getValue(Message.class);
                if (message != null) {
                    Timestamp messageTime = Timestamp.valueOf(message.timeStamp);
                    //Log.i("ProjectEntryActivity", " currentUser: " + currentUser +
                    //      " message time: " + messageTime + " login time: " + loginTime);
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
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors here
                Log.e("ProjectEntryActivity", "Error retrieving goals: " + databaseError.getMessage());
            }
        };
        
        messagesRef.addChildEventListener(messagesChildEventListener);
    }

    //get the isChecked sum in db, update percentage of progress
    public void updateProgressPercentage(FirebaseRecyclerAdapter<Goal, GoalViewHolder> adapter, int checkedCount, int invalidGoalCount) {
        int allGoalsDisplayedInRC = adapter.getItemCount();
        percentageOfProgress = (adapter.getItemCount() > 0) ? ((float) checkedCount / (allGoalsDisplayedInRC - invalidGoalCount) * 100) : 0;
        bar.setProgress((int) percentageOfProgress);
        //progressIndicator.setText("Today's goal completion " + (int) percentageOfProgress + "%");
        //this function is called ten times if there're ten item views in rc, checkedCount needs to be reset to 0 during each loading
        Log.d("progress", "Today's goal completion " + checkedCount + " / " + adapter.getItemCount());
        Log.d("progress",  "invalidCount: "+ invalidGoalCount);
        //TODO:
        progressIndicator.setText("Today's goal completion " + (int) percentageOfProgress + "%");
    }


    @Override
    protected void onStart() {
        super.onStart();

    }


    public void startProfileActivity(View view) {
        // Pass the current user info to Profile activity -- Yutong
        Intent intent = new Intent(ProjectEntryActivity.this, ProfileActivity.class);
        intent.putExtra(CURRENT_USER, currentUser);
        intent.putExtra(LOGIN_TIME, loginTime);
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
        // Pass the current user info to Create New Goal activity -- Yutong
        Intent intent = new Intent(ProjectEntryActivity.this, CreateNewGoalActivity.class);
        intent.putExtra(CURRENT_USER, currentUser);
        intent.putExtra(LOGIN_TIME, loginTime);
        startActivity(intent);
    }

    public void startProgressActivity(View view) {
        // Pass the current user info to Progress activity -- Yutong
        Intent intent = new Intent(ProjectEntryActivity.this, ProgressActivity.class);
        intent.putExtra(CURRENT_USER, currentUser);
        intent.putExtra(LOGIN_TIME, loginTime);
        startActivity(intent);
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

}