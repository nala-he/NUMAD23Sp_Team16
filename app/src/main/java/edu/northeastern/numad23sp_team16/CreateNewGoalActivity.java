package edu.northeastern.numad23sp_team16;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.numad23sp_team16.models.Icon;

public class CreateNewGoalActivity extends AppCompatActivity {
    private static final String TAG = "CreateNewGoalActivity";

    private Toolbar toolbar;
    private RecyclerView iconRecyclerView;
    private IconAdapter iconAdapter;
    private List<Icon> iconsList;
    private EditText editGoalName;

    private String goalName;
    private Icon selectedIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_goal);

        editGoalName = findViewById(R.id.text_goal_name);

        // Set custom action bar with back button
        toolbar = findViewById(R.id.create_goal_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Add all icons to icons list for recycler view
        createListOfIcons();

        // Set recycler view to display icons for goal categories
        iconRecyclerView = findViewById(R.id.icons_recycler_view);
        iconAdapter = new IconAdapter(getApplicationContext(), iconsList);
        iconRecyclerView.setAdapter(iconAdapter);

        // Dynamically calculate number of columns depending on size of screen
        iconRecyclerView.setLayoutManager(new GridLayoutManager(CreateNewGoalActivity.this,
                calculateNumberColumns(getApplicationContext())));

    }

    public int calculateNumberColumns(Context context) {
        // Calculate number of columns to display icons depending on screen size
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = (displayMetrics.widthPixels / displayMetrics.density) - 20;
        int numberOfColumns = (int) (dpWidth / 75);
        return numberOfColumns;
    }

    public List<Icon> createListOfIcons() {
        // Create list of icon categories
        iconsList = new ArrayList<>();
        iconsList.add(new Icon(R.drawable.icon_self_care, "Self-Care"));
        iconsList.add(new Icon(R.drawable.icon_growth, "Growth"));
        iconsList.add(new Icon(R.drawable.icon_social, "Social"));
        iconsList.add(new Icon(R.drawable.icon_productivity, "Productivity"));
        iconsList.add(new Icon(R.drawable.icon_health, "Health"));
        iconsList.add(new Icon(R.drawable.icon_hydration, "Hydration"));
        iconsList.add(new Icon(R.drawable.icon_finances, "Finances"));
        iconsList.add(new Icon(R.drawable.icon_language, "Language"));
        iconsList.add(new Icon(R.drawable.icon_reading, "Reading"));

        return iconsList;
    }

    public void displayReminderInfo(View view) {
        // Create alert dialog with info regarding reminders
        AlertDialog.Builder reminderDialog = new AlertDialog.Builder(CreateNewGoalActivity.this);

        // Customize dialog to display info about turning reminders on/off
        reminderDialog.setTitle("Daily Reminders");
        reminderDialog.setMessage("Turn it on to set the time for daily reminders, " +
                "turn it off if you don't want any reminders.");

        // Dismiss dialog with OK button
        reminderDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Create and show dialog
        reminderDialog.create().show();
    }

    public void saveNewGoal(View view) {
        // Retrieve inputted goal name
        goalName = editGoalName.getText().toString();

        // Retrieve selected icon
        selectedIcon = iconAdapter.getSelectedIcon();
        String iconName = selectedIcon.getIconName();

        // TODO: navigate back to home screen with new goal info
        Log.d(TAG, "saveNewGoal: Goal Name: " + goalName + ", Icon: " + iconName);
    }
}