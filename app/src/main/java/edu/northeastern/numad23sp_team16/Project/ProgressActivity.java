package edu.northeastern.numad23sp_team16.Project;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import edu.northeastern.numad23sp_team16.R;
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
    private List<CalendarDay> completedGoalsDates;
    private TextView petHealthInfo;
    private Map<Integer, Integer> dogHealth;
    private Map<Integer, Integer> catHealth;
    private String petType;
    private float totalHealth = 0;
    private int totalDays = 1;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        // Get currently logged in user
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentUser = extras.getString(CURRENT_USER);
            loginTime = extras.getString(LOGIN_TIME);
        }

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
        DatabaseReference userRef = mDatabase.child("FinalProjectUsers").child(currentUser);
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

        // Set up calendar to view past history
        calendarHistory = findViewById(R.id.completion_history_calendar);

        // Set date selected to current date
        calendarHistory.setDateSelected(CalendarDay.today(), true);

        // Create reference to GoalFinishedStatus node in database
        DatabaseReference goalFinishedStatusRef = mDatabase.child("GoalFinishedStatus");

        // Get today's date
        Date currentDate = new Date();

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
                // Iterate through GoalFinishedStatus nodes
                for (DataSnapshot data : snapshot.getChildren()) {
                    // Check if goal finished status is associated with current user
                    if (Objects.equals(data.child("userId").getValue(String.class), currentUser)) {

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

                        // Parse date to Date object
                        String stringDate = month + "/" + day + "/" + year;
                        Date date = null;
                        try {
                            date = new SimpleDateFormat("MM/dd/YYYY").parse(stringDate);
                        } catch (ParseException e) {
                            Log.d(TAG, "onDataChange: error parsing date");
                        }

                        // Only calculate into pet's health if not the current day
                        if (currentDate != date) {
                            // Add to total health
                            totalHealth += data.child("percentageOfToday").getValue(Float.class);

                            // Calculate average health from total health and number of days
                            float averageHealth = totalHealth / totalDays;

                            // Update average health for PetHealth node
                            petHealthRef.child("averageHealth").setValue(averageHealth);
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

    // Calculate total number of days between creation date and current day
    private int calculateNumberOfDays(String date) {
        // Convert creation date to Date object
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy", Locale.US);
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

        return differenceInDays;
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

    // Navigate to share pet status with friends screen
    public void onSharePetStatus(View view) {
        // pass currently logged in user and log in time to Share activity
        Intent intent = new Intent(ProgressActivity.this, ShareActivity.class);
        // pass the current user id and login time
        intent.putExtra(CURRENT_USER, currentUser);
        intent.putExtra(LOGIN_TIME, loginTime);
        startActivity(intent);
    }

    public void updateCalendar(int year, int month, int day) {
        // Create calendar date
        CalendarDay date = CalendarDay.from(year, month, day);

        // Add pink dot to calendar for given date
        DayDecorator dayDecorator = new DayDecorator(date);
        calendarHistory.addDecorator(dayDecorator);
    }




    // Pass currently logged in user and log in time back when swipe back
    @Override
    public void onBackPressed() {
        Intent homeIntent = new Intent(ProgressActivity.this, ProjectEntryActivity.class);
        // Add currently logged in user and log in time to intent
        homeIntent.putExtra(CURRENT_USER, currentUser);
        homeIntent.putExtra(LOGIN_TIME, loginTime);
        setResult(Activity.RESULT_OK, homeIntent);
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
                setResult(Activity.RESULT_OK, homeIntent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}