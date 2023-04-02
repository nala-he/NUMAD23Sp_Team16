package edu.northeastern.numad23sp_team16.Project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM - yyyy", Locale.getDefault());
    private List<Long> completedGoalsDates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        // Set custom action bar with back button
        toolbar = findViewById(R.id.progress_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Get pet name and image from view
        petName = findViewById(R.id.progress_pet_name);
        petImage = findViewById(R.id.progress_pet_image);

        // TODO: replace with pet name and image from database
        // Set pet name and image for current user
        petName.setText("Juni");
        petImage.setImageResource(R.drawable.dog_small);

        // TODO: get user's pet's overall health and replace number
        petHealth = (int) Math.round(72.5 / DENOMINATOR);

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
        startActivity(new Intent(ProgressActivity.this, SendStatusActivity.class));
    }

    public void setCalendar() {
        // Set date selected to current date
        calendarHistory.setDateSelected(CalendarDay.today(), true);

        // TODO: get days of the month that the user completed all daily goals

        calendarHistory.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

                DayDecorator dayDecorator= new DayDecorator(date);
                widget.addDecorator(dayDecorator);
                widget.invalidateDecorators();

            }
        });
    }

//    public void setCalendar() {
//        // Set first day of week to sunday
//        calendarHistory.setFirstDayOfWeek(Calendar.SUNDAY);
//
//        completedGoalsDates = new ArrayList<>();
//        completedGoalsDates.add(1680368815L); // 4/1/23
//
//        // Add all dates the user completed all daily goals to calendar
//        for (int i = 0; i < completedGoalsDates.size(); i ++) {
//            Event completedDay = new Event(R.color.pastel_pink, completedGoalsDates.get(i),
//                    "Completed all daily goals");
//            calendarHistory.addEvent(completedDay);
//        }
//
//        // Set listener to handle click events
//        calendarHistory.setListener(new CompactCalendarView.CompactCalendarViewListener() {
//            @Override
//            public void onDayClick(Date dateClicked) {
//
//            }
//
//            @Override
//            public void onMonthScroll(Date firstDayOfNewMonth) {
//                Log.d(TAG, "Month was scrolled to: " + firstDayOfNewMonth);
//            }
//        });
//    }
}