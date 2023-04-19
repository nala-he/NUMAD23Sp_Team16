package edu.northeastern.numad23sp_team16.Project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.northeastern.numad23sp_team16.R;

public class ProgressActivity extends AppCompatActivity {
    private final String CURRENT_USER = "CURRENT_USER";
    private final String LOGIN_TIME = "LOGIN_TIME";

    private String currentUser;
    private String loginTime;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        // Set custom action bar with back button
        toolbar = findViewById(R.id.progress_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Retrieve currently logged in user -- Yutong
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentUser = extras.getString(CURRENT_USER);
            loginTime = extras.getString(LOGIN_TIME);
        }
        Log.i("ProgressActivity", "currentUser: " + currentUser);


        // Get pet name and image from view
        petName = findViewById(R.id.progress_pet_name);
        petImage = findViewById(R.id.progress_pet_image);

        // TODO: replace with pet name and image from database
        // Set pet name and image for current user
        petName.setText("Juni");
        petImage.setImageResource(R.drawable.dog_small);

        // TODO: get user's pet's overall health and replace number
        petHealth = Math.round((float) 5 / DENOMINATOR);

        // Recycler view to show hearts (pet health)
        petHealthRecyclerView = findViewById(R.id.progress_pet_health);
        listOfHearts = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            listOfHearts.add((Integer) R.drawable.heart);
        }

        // Set text below hearts to remind user what health condition their pet is in
        petHealthInfo = findViewById(R.id.pet_health_info);
        if (petHealth == 10) {
            // 10 hearts
            petHealthInfo.setText("Your pet is in the best health condition! Keep it up!") ;
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

        // Set adapter for pet health recycler view
        heartAdapter = new HeartAdapter(getApplicationContext(), listOfHearts, petHealth);
        petHealthRecyclerView.setAdapter(heartAdapter);

        // Set layout of hearts in recycler view
        petHealthRecyclerView.setLayoutManager(new GridLayoutManager(ProgressActivity.this, 5));

        // Set up calendar to view past history
        calendarHistory = findViewById(R.id.completion_history_calendar);
        setCalendar();
    }

    // Use this function to enable the currentUser and loginTime data to be passed to the
    // previous activity when the back button is clicked -- Yutong
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Navigate to share pet status with friends screen
    public void onSharePetStatus(View view) {
        Intent intent = new Intent(ProgressActivity.this, ShareActivity.class);
        // pass the current user id and login time
        intent.putExtra(CURRENT_USER, currentUser);
        intent.putExtra(LOGIN_TIME, loginTime);
        startActivity(intent);
    }

    public void setCalendar() {
        // Set date selected to current date
        calendarHistory.setDateSelected(CalendarDay.today(), true);

        //  get the days that the user completed all daily goals from database
        completedGoalsDates = new ArrayList<>();
//        completedGoalsDates.add(CalendarDay.from(2023, 4, 1)); // April 1, 2023

        getDaysAllCompletedFromDB(completedGoalsDates);

        // Add dots to calendar on dates the user completed all daily goals
//        for (int i = 0; i < completedGoalsDates.size(); i++) {
//            DayDecorator dayDecorator = new DayDecorator(completedGoalsDates.get(i));
//            calendarHistory.addDecorator(dayDecorator);
//        }
    }
    //This is added by Yuan to get the progress data from db.-Yuan
    private void getDaysAllCompletedFromDB(List<CalendarDay> completedGoalsDates) {
        DatabaseReference GoalFinishedStatusRef = FirebaseDatabase.getInstance().getReference("FinalProject").child("GoalFinishedStatus").child("userId");
        Query daysGoalAllFinishedQuery = GoalFinishedStatusRef.orderByChild("userId").equalTo(currentUser);
        daysGoalAllFinishedQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    // Loop through the results of the query
                    for (DataSnapshot goalSnapshot : snapshot.getChildren()) {
                        GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<>() {};
                        Map<String, Object> values = goalSnapshot.getValue(genericTypeIndicator);
                        Map<String, Integer> dateMap = (Map<String, Integer>) values.get("dateMap");
                        // Use the dateMap data as needed
                        int day = dateMap.get("day");
                        Log.d("day:", String.valueOf(day));
                        int month = dateMap.get("month");
                        int year = dateMap.get("year");
                        completedGoalsDates.add(CalendarDay.from(year, month, day));
                        Log.d("which day all goals finished: " , day+"/"+month+"/"+year);
                    }
                } else {
                    Log.d("database","No days info related to the user found in the db.");
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Add dots to calendar on dates the user completed all daily goals
        for (int i = 0; i < completedGoalsDates.size(); i++) {
            DayDecorator dayDecorator = new DayDecorator(completedGoalsDates.get(i));
            calendarHistory.addDecorator(dayDecorator);
        }
    }
}