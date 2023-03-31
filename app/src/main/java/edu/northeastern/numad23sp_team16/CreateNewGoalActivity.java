package edu.northeastern.numad23sp_team16;


import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    private static String DEFAULT_REMINDER_MESSAGE = "Keep going, you've got this!";
    private int selectedHour, selectedMinute;
    private EditText editStartDate, editEndDate;
    private EditText editMemo;

    // New goal values
    private String goalName;
    private Icon selectedIcon;
    private Boolean reminderOn = false;
    private String reminderMessage = DEFAULT_REMINDER_MESSAGE; // default reminder message on first create
    private int reminderHour, reminderMinute;
    private Calendar startDate = Calendar.getInstance();
    private Calendar endDate = Calendar.getInstance();
    private int priority = 1; // default priority is low (1)
    private String memo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_goal);

        editGoalName = findViewById(R.id.text_goal_name);
        editMemo = findViewById(R.id.text_memo);

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

        // Pick goal start date
        selectStartDate();

        // Pick goal end date
        selectEndDate();
    }

    // Choose goal start date
    private void selectStartDate() {
        editStartDate = findViewById(R.id.text_start_date);

        // Date picker
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                startDate.set(Calendar.YEAR, year);
                startDate.set(Calendar.MONTH, month);
                startDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                formatDate(editStartDate, startDate);
            }
        };

        // Display date picker when click on input for start date
        editStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(CreateNewGoalActivity.this, date,
                        startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH),
                        startDate.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    // Choose goal end date
    private void selectEndDate() {
        editEndDate = findViewById(R.id.text_end_date);

        // Date picker
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                endDate.set(Calendar.YEAR, year);
                endDate.set(Calendar.MONTH, month);
                endDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                formatDate(editEndDate, endDate);
            }
        };

        // Display date picker when click on input for start date
        editEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(CreateNewGoalActivity.this, date,
                        endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH),
                        endDate.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void formatDate(EditText dateToEdit, Calendar date) {
        String format = "MM/dd/yy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.US);
        dateToEdit.setText(dateFormat.format(date.getTime()));
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

                    // Display dialog to set reminder message and time
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


        // Get contents of dialog and initialize with saved/default data

        // Get reminder message
        editReminderMessage = reminderDialog.findViewById(R.id.text_reminder_message);
        editReminderMessage.setText(reminderMessage);

        // Get reminder time
        editReminderTime = reminderDialog.findViewById(R.id.text_reminder_time);
        editReminderTime.setText(String.format("%02d:%02d", reminderHour, reminderMinute));
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
                // Save new reminder message and reminder time
                reminderMessage = editReminderMessage.getText().toString();
                reminderHour = selectedHour;
                reminderMinute = selectedMinute;
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
            public void onTimeSet(TimePicker view, int hour, int minute) {
                selectedHour = hour;
                selectedMinute = minute;

                // Get the selected hour and minute
                editReminderTime.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
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

    public void displayPriorityInfo(View view) {
        // Create alert dialog with info regarding how priority works
        AlertDialog.Builder infoDialog = new AlertDialog.Builder(CreateNewGoalActivity.this);

        // Customize dialog to display info about turning how priority benefits/harms your pet more
        infoDialog.setTitle("Priority Levels");
        infoDialog.setMessage("Higher priority will have a bigger impact on your pet's health.");

        // Dismiss dialog with OK button
        infoDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Create and show dialog
        infoDialog.create().show();
    }

    // Determine which priority level is selected
    public void onPriorityClicked(View view) {
        // Check if the button was selected
        boolean checked = ((RadioButton) view).isChecked();

        // Check which priority was selected
        switch(view.getId()) {
            case R.id.low_priority:
                if (checked) {
                    priority = 1;
                }
                break;
            case R.id.medium_priority:
                if (checked) {
                    priority = 2;
                }
                break;
            case R.id.high_priority:
                if (checked) {
                    priority = 3;
                }
                break;
        }
    }

    public void saveNewGoal(View view) {
        // Retrieve inputted goal name
        goalName = editGoalName.getText().toString();

        // Retrieve selected icon
        selectedIcon = iconAdapter.getSelectedIcon();
        String iconName = selectedIcon.getIconName();

        // Retrieve inputted memo
        memo = editMemo.getText().toString();

        // Format calendar date
        SimpleDateFormat format1 = new SimpleDateFormat("MM/dd/yy", Locale.US);

        // TODO: Create new goal instance from input values and save to database
        if (reminderOn) {
            // Reminders turned on
            Log.d(TAG, "saveNewGoal: Goal Name: " + goalName + ", Icon: " + iconName
                    + ", Reminders: " + reminderOn + " - " + reminderMessage + " - "
                    + String.format("%02d:%02d", reminderHour, reminderMinute) + ", "
                    + format1.format(startDate.getTime()) + " - "+ format1.format(endDate.getTime())
                    + ", Priority: " + priority + ", Memo: " + memo);
        }
        else {
            // Reminders turned off
            Log.d(TAG, "saveNewGoal: Goal Name: " + goalName + ", Icon: " + iconName
                    + ", Reminders: " + reminderOn + ", " + format1.format(startDate.getTime())
                    + " - " + format1.format(endDate.getTime()) + ", Priority: " + priority
                    + ", Memo: " + memo);
        }

        // TODO: navigate to home screen created by Yuan
        startActivity(new Intent(CreateNewGoalActivity.this, GoalForItActivity.class));
    }
}