package edu.northeastern.numad23sp_team16.Project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.numad23sp_team16.R;

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
    private TextView calendarKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        // Set custom action bar with back button
        toolbar = findViewById(R.id.progress_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Get pet name and image from view
        petName = findViewById(R.id.progress_pet_name);
        petImage = findViewById(R.id.progress_pet_image);

        // TODO: replace with pet name and image from database
        // Set pet name and image for current user
        petName.setText("Juni");
        petImage.setImageResource(R.drawable.dog_small);

        // TODO: get user's pet's overall health and replace number
        petHealth = Math.round((float) 76 / DENOMINATOR);

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

        // Set up calendar to view past history
        calendarHistory = findViewById(R.id.completion_history_calendar);
        setCalendar();
    }

    // Navigate to share pet status with friends screen
    public void onSharePetStatus(View view) {
        startActivity(new Intent(ProgressActivity.this, ShareActivity.class));
    }

    public void setCalendar() {
        // Set date selected to current date
        calendarHistory.setDateSelected(CalendarDay.today(), true);

        // TODO: get the days that the user completed all daily goals from database
        completedGoalsDates = new ArrayList<>();
        completedGoalsDates.add(CalendarDay.from(2023, 4, 1)); // April 1, 2023
        completedGoalsDates.add(CalendarDay.from(2023, 3, 26)); // March 24, 2023
        completedGoalsDates.add(CalendarDay.from(2023, 3, 8)); // March 8, 2023

        // Add dots to calendar on dates the user completed all daily goals
        for (int i = 0; i < completedGoalsDates.size(); i++) {
            DayDecorator dayDecorator = new DayDecorator(completedGoalsDates.get(i));
            calendarHistory.addDecorator(dayDecorator);
        }
    }
}