package edu.northeastern.numad23sp_team16;


import android.app.ActionBar;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
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
    private EditText editReminderMessage;
    private EditText editReminderTime;

    private String goalName;
    private Icon selectedIcon;
    private Boolean reminderOn = false;
    private static String DEFAULT_REMINDER_MESSAGE = "Keep going, you've got this!";
    private String reminderMessage;
    private String reminderTime;
    private int reminderHour, reminderMinute;

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

        // Determine whether reminders turned on or off
        setReminder();
    }

    public void setReminder() {
        // Get reminder switch from screen
        SwitchCompat reminderSwitch = findViewById(R.id.reminder_switch);
        reminderSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reminderSwitch.isChecked()) {
                    // Reminders turned on
                    reminderOn = true;

                    // Display setting reminders dialog
                    showReminderDialog();

                } else {
                    // Reminders turned off
                    reminderOn = false;
                }
            }
        });
    }

    private void showReminderDialog() {
        // Create custom dialog for setting daily reminders
        Dialog reminderDialog = new Dialog(CreateNewGoalActivity.this);

        // No title on dialog
        reminderDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Do not allow user to exit by clicking outside of dialog
        reminderDialog.setCancelable(false);

        // Set and show dialog to custom reminder dialog
        reminderDialog.setContentView(R.layout.custom_set_reminder_dialog);
        reminderDialog.show();

        // Initialize contents of dialog
        // Get reminder message
        editReminderMessage = reminderDialog.findViewById(R.id.text_reminder_message);
        if (reminderMessage == null) {
            // Set reminder message to default if first time
            editReminderMessage.setText(DEFAULT_REMINDER_MESSAGE);
        } else {
            editReminderMessage.setText(reminderMessage);
        }

        // Get reminder time
        editReminderTime = reminderDialog.findViewById(R.id.text_reminder_time);
        editReminderTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Prompt user with time picker dialog
                setReminderTime();
            }
        });

        // Save button
        Button saveButton = reminderDialog.findViewById(R.id.button_set_reminder_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save all inputted info
                reminderMessage = editReminderMessage.getText().toString();
                reminderTime = editReminderTime.getText().toString();
                reminderDialog.dismiss();
            }
        });

        // Cancel button
        Button cancelButton = reminderDialog.findViewById(R.id.button_set_reminder_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reminderDialog.dismiss();
            }
        });
    }

    // Time picker dialog
    private void setReminderTime() {
        // Set listener for selected times
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
                // Get the selected hour and minute
                reminderHour = selectedHour;
                reminderMinute = selectedMinute;
            }
        };

        // Create time picker
        TimePickerDialog reminderTimeDialog = new TimePickerDialog(CreateNewGoalActivity.this,
                android.R.style.Theme_Holo_Light_Dialog, onTimeSetListener,
                reminderHour, reminderMinute, false);

        // Set title
        reminderTimeDialog.setTitle("Set reminder time:");

        // Show time picker dialog
        reminderTimeDialog.show();
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

        // TODO: navigate back to home screen with new goal info (create new goal instance)
        if (reminderOn) {
            // Reminders turned on
            Log.d(TAG, "saveNewGoal: Goal Name: " + goalName + ", Icon: " + iconName
                    + ", Reminders: " + reminderOn + " - " + reminderMessage + " - "
                    + reminderTime);
        }
        else {
            // Reminders turned off
            Log.d(TAG, "saveNewGoal: Goal Name: " + goalName + ", Icon: " + iconName
                    + ", Reminders: " + reminderOn);
        }
    }
}