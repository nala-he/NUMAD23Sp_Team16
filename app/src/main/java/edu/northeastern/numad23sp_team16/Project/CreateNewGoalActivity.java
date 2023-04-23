package edu.northeastern.numad23sp_team16.Project;


import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import edu.northeastern.numad23sp_team16.R;
import edu.northeastern.numad23sp_team16.models.Goal;
import edu.northeastern.numad23sp_team16.models.Icon;
import edu.northeastern.numad23sp_team16.models.Message;

public class CreateNewGoalActivity extends AppCompatActivity {
    private static final String TAG = "CreateNewGoalActivity";
    private String channelId = "notification_channel_0";

    private Toolbar toolbar;
    private RecyclerView iconRecyclerView;
    private IconAdapter iconAdapter;
    private List<Icon> iconsList;
    private EditText editGoalName;
    private EditText editReminderMessage;
    private EditText editReminderTime;
    private static String DEFAULT_REMINDER_MESSAGE = "Keep going, you've got this!";
    private int selectedHour;
    private int selectedMinute;
    private EditText editStartDate, editEndDate;
    private EditText editMemo;
    // Format calendar date
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy", Locale.US);
    private Boolean isReminderDialogOpen = false;
    private Boolean isReminderTimeDialogOpen = false;
    private TimePickerDialog reminderTimeDialog;
    private Dialog reminderDialog;
    private DatePickerDialog startDatePicker;
    private DatePickerDialog endDatePicker;
    private Boolean isStartDateCalendarOpen = false;
    private Boolean isEndDateCalendarOpen = false;
    private Boolean isReminderInfoDialogOpen = false;
    private Boolean isPriorityInfoDialogOpen= false;
    private AlertDialog reminderInfoDialog;
    private ImageView reminderQuestionMark;
    private ImageView priorityQuestionMark;
    private AlertDialog priorityInfoDialog;

    // Firebase database
    private DatabaseReference mDatabase;
    private static final String CURRENT_USER = "CURRENT_USER";
    private final String LOGIN_TIME = "LOGIN_TIME";
    private String currentUser;
    private String loginTime;

    // For orientation changes
    private static final String GOAL_NAME = "GOAL_NAME";
    private static final String GOAL_ICON = "GOAL_ICON";
    private static final String GOAL_REMINDER = "GOAL_REMINDER";
    private static final String GOAL_REMINDER_MESSAGE = "GOAL_REMINDER_MESSAGE";
    private static final String GOAL_REMINDER_HOUR = "GOAL_REMINDER_HOUR";
    private static final String GOAL_REMINDER_MINUTE = "GOAL_REMINDER_MINUTE";
    private static final String GOAL_START = "GOAL_START";
    private static final String GOAL_END = "GOAL_END";
    private static final String GOAL_PRIORITY = "GOAL_PRIORITY";
    private static final String GOAL_MEMO = "GOAL_MEMO";
    private static final String REMINDER_DIALOG_OPEN = "REMINDER_DIALOG_OPEN";
    private static final String UNSAVED_REMINDER_MESSAGE = "UNSAVED_REMINDER_MESSAGE";
    private static final String REMINDER_TIME_DIALOG_OPEN = "REMINDER_TIME_DIALOG_OPEN";
    private static final String UNSAVED_HOUR = "UNSAVED_HOUR";
    private static final String UNSAVED_MINUTE = "UNSAVED_MINUTE";
    private static final String START_DATE_DIALOG = "START_DATE_DIALOG";
    private static final String UNSAVED_START_YEAR = "UNSAVED_START_YEAR";
    private static final String UNSAVED_START_MONTH = "UNSAVED_START_MONTH";
    private static final String UNSAVED_START_DAY = "UNSAVED_START_DAY";
    private static final String END_DATE_DIALOG = "END_DATE_DIALOG";
    private static final String UNSAVED_END_YEAR = "UNSAVED_END_YEAR";
    private static final String UNSAVED_END_MONTH = "UNSAVED_END_MONTH";
    private static final String UNSAVED_END_DAY = "UNSAVED_END_DAY";
    private static final String REMINDER_INFO_DIALOG_OPEN = "REMINDER_INFO_DIALOG_OPEN";
    private static final String PRIORITY_INFO_DIALOG_OPEN = "PRIORITY_INFO_DIALOG_OPEN";

    // New goal values
    private String goalName;
    private Icon selectedIcon;
    private Boolean reminderOn = false;
    private String reminderMessage = DEFAULT_REMINDER_MESSAGE; // default reminder message on first create
    private int reminderHour;
    private int reminderMinute;
    private Calendar startDate = Calendar.getInstance();
    private Calendar endDate = Calendar.getInstance();
    private int priority = 1; // default priority is low (1)
    private String memo;
    private DatabaseReference messagesRef;
    private ChildEventListener messagesChildEventListener;
    private Map<Integer, Integer> dogHealth;
    private Map<Integer, Integer> catHealth;
    private int notificationId;

    private final int PERMISSION_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_goal);
//        createNotificationChannel();
        Date date = new Date();
        Timestamp currentTime = new Timestamp(date.getTime());

        // Get currently logged in user and login time
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentUser = extras.getString(CURRENT_USER);
            loginTime = extras.getString(LOGIN_TIME);
            notificationId = extras.getInt("notification_id");
        }
        Log.i("CreateNewGoalActivity onCreate", "notification_id: " + notificationId);

        editGoalName = findViewById(R.id.text_goal_name);
        editMemo = findViewById(R.id.text_memo);
        reminderQuestionMark = findViewById(R.id.reminder_question_mark);
        priorityQuestionMark = findViewById(R.id.priority_question_mark);

        // Set custom action bar with back button
        toolbar = findViewById(R.id.create_goal_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

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

        // Map pet health images to health status
        assignPetHealthImages();

        // Connect to firebase database
        mDatabase = FirebaseDatabase.getInstance().getReference("FinalProject");

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
        Log.i("CreateGoalActivity", "receive notification " + notificationId);


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

    // Show start date picker dialog
    private void showStartDatePickerDialog(int selectedYear, int selectedMonth, int selectedDay) {
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

        startDatePicker = new DatePickerDialog(CreateNewGoalActivity.this, date,
                selectedYear, selectedMonth, selectedDay);
        startDatePicker.show();
        isStartDateCalendarOpen = true;

        startDatePicker.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                isStartDateCalendarOpen = false;
            }
        });
    }

    // Choose goal start date
    private void selectStartDate() {
        editStartDate = findViewById(R.id.text_start_date);

        // Display date picker when click on input for start date
        editStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStartDatePickerDialog(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH),
                        startDate.get(Calendar.DAY_OF_MONTH));
            }
        });
    }



    // Show end date picker dialog
    private void showEndDatePickerDialog(int selectedYear, int selectedMonth, int selectedDay) {
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

        endDatePicker = new DatePickerDialog(CreateNewGoalActivity.this, date,
                selectedYear, selectedMonth, selectedDay);
        endDatePicker.show();
        isEndDateCalendarOpen = true;

        endDatePicker.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                isEndDateCalendarOpen = false;
            }
        });
    }


    // Choose goal end date
    private void selectEndDate() {
        editEndDate = findViewById(R.id.text_end_date);

        // Display date picker when click on input for end date
        editEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEndDatePickerDialog(endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH),
                        endDate.get(Calendar.DAY_OF_MONTH));
            }
        });
    }

    private void formatDate(EditText dateToEdit, Calendar date) {
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
        reminderDialog = new Dialog(CreateNewGoalActivity.this);

        // No title on dialog
        reminderDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Do not allow user to exit by clicking outside of dialog
        reminderDialog.setCancelable(false);

        // Set and show dialog to custom reminder dialog
        reminderDialog.setContentView(R.layout.custom_set_reminder_dialog);
        reminderDialog.show();
        isReminderDialogOpen = true;

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
                setReminderTime(selectedHour, selectedMinute);
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
                isReminderDialogOpen = false;
            }
        });

        // Cancel button
        Button cancelButton = reminderDialog.findViewById(R.id.button_set_reminder_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reminderDialog.dismiss();
                selectedHour = reminderHour;
                selectedMinute = reminderMinute;
                isReminderDialogOpen = false;
            }
        });
    }

    // Time picker dialog
    private void setReminderTime(int showHour, int showMinute) {
        // Set listener for selected times
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                selectedHour = hour;
                selectedMinute = minute;

                // Display the selected hour and minute
                editReminderTime.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
            }
        };

        // Create time picker
        reminderTimeDialog = new TimePickerDialog(CreateNewGoalActivity.this,
                android.R.style.Theme_Holo_Light_Dialog, onTimeSetListener,
                showHour, showMinute, false);

        // Set title
        reminderTimeDialog.setTitle("Set reminder time:");

        // Show time picker dialog
        reminderTimeDialog.show();
        isReminderTimeDialogOpen = true;

        reminderTimeDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                isReminderTimeDialogOpen = false;
            }
        });
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
                isReminderInfoDialogOpen = false;
            }
        });

        // Create and show dialog
        reminderInfoDialog = reminderDialog.create();
        reminderInfoDialog.show();
        isReminderInfoDialogOpen = true;
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
                isPriorityInfoDialogOpen = false;
            }
        });

        // Create and show priority info dialog
        priorityInfoDialog = infoDialog.create();
        priorityInfoDialog.show();
        isPriorityInfoDialogOpen = true;
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


    // Create unique goal id: goal + time + userId
    private String createUniqueGoalId() {
        String time = String.valueOf(System.currentTimeMillis());
        return ("goal" + time + currentUser);
    }

    public void saveNewGoal(View view) {
        // Retrieve inputted goal name
        goalName = editGoalName.getText().toString();

        // Retrieve selected icon
        selectedIcon = iconAdapter.getSelectedIcon();
        String iconName = getResources().getResourceEntryName(selectedIcon.getIconId());

        // Retrieve inputted memo
        memo = editMemo.getText().toString();

        if (goalName.isEmpty() || goalName.trim().isEmpty()) {
            // Do not allow user to save if goal name is empty
            Toast.makeText(CreateNewGoalActivity.this, "Please provide a goal name before saving",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (endDate.getTime().before(startDate.getTime())) {
            // Do not allow user to save if end date is before start date
            Toast.makeText(CreateNewGoalActivity.this,
                    "End date must be the same or after the start date", Toast.LENGTH_LONG).show();
            return;
        }

        // All requirements met - create new goal instance, save to database, and navigate back to home page
        Goal newGoal;
        if (reminderOn) {
            // Reminders turned on
            Log.d(TAG, "saveNewGoal: Goal Name: " + goalName + ", Icon: " + iconName
                    + ", Reminders: " + reminderOn + " - " + reminderMessage + " - "
                    + String.format("%02d:%02d", reminderHour, reminderMinute) + ", "
                    + dateFormat.format(startDate.getTime()) + " - "+ dateFormat.format(endDate.getTime())
                    + ", Priority: " + priority + ", Memo: " + memo);

            // Create new goal instance
            newGoal = new Goal(currentUser, goalName, iconName, reminderOn,
                    reminderMessage, reminderHour, reminderMinute,
                    dateFormat.format(startDate.getTime()), dateFormat.format(endDate.getTime()),
                    priority, memo);
            // send this reminder at designated time between startDate and endDate-Yuan
            sendReminder(reminderMessage, reminderHour, reminderMinute, dateFormat.format(startDate.getTime()), dateFormat.format(endDate.getTime()));
        }
        else {
            // Reminders turned off
            Log.d(TAG, "saveNewGoal: Goal Name: " + goalName + ", Icon: " + iconName
                    + ", Reminders: " + reminderOn + ", " + dateFormat.format(startDate.getTime())
                    + " - " + dateFormat.format(endDate.getTime()) + ", Priority: " + priority
                    + ", Memo: " + memo);

            // Create new goal instance
            newGoal = new Goal(currentUser, goalName, iconName, reminderOn,
                    dateFormat.format(startDate.getTime()), dateFormat.format(endDate.getTime()),
                    priority, memo);
        }

        // Add new goal to database with goal id as unique identifier
        //mDatabase.child("Goals").child(createUniqueGoalId()).setValue(newGoal);
        //Yuan-I want to change it to:
        mDatabase.child("FinalGoals").child(currentUser).child(createUniqueGoalId()).setValue(newGoal);

        // Navigate to home screen
        Toast.makeText(CreateNewGoalActivity.this, "Saved!", Toast.LENGTH_LONG).show();

        // Pass currently logged in user and log in time back
        Intent homeIntent = new Intent(CreateNewGoalActivity.this, ProjectEntryActivity.class);

        homeIntent.putExtra(CURRENT_USER, currentUser);
        homeIntent.putExtra(LOGIN_TIME, loginTime);
        homeIntent.putExtra("notification_id", notificationId);

        messagesRef.removeEventListener(messagesChildEventListener);

        startActivity(homeIntent);
    }
    //send reminder-Yuan
    private void sendReminder(String reminderMessage, int reminderHour, int reminderMinute, String startDateStr, String endDateStr) {
        String currentDateStr = getCurrentDateStr();
        Log.d("Time-cur-start-end",currentDateStr + " - " + startDateStr + " - " + endDateStr);
        if(currentDateStr.compareTo(startDateStr) >= 0 && currentDateStr.compareTo(endDateStr) <= 0 ){

            //an intent to launch reminder notification
            Intent intent = new Intent(this, MyReminder.class);
            intent.putExtra("reminder_message", reminderMessage);

            Log.d(TAG,"reminder_message/reminderHour/reminderMinute: " +reminderMessage +"reminderHour:"+ reminderHour + "reminderMinute:"+ reminderMinute );

            //wrap the intent
//            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            // Changed getActivity to getBroadcast (Yutong), update flag to allow changes to data from extras (Macee)
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                    intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

//            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


            //the PendingIntent will be launched when the alarm goes off
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, reminderHour); // Set the hour at which the reminder should be sent
            calendar.set(Calendar.MINUTE, reminderMinute); // Set the minute at which the reminder should be sent
            calendar.set(Calendar.SECOND, 0); // Set the second at which the reminder should be sent
            long alarmTime = calendar.getTimeInMillis();

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);

            //original way is triggered once only, change it to be triggered daily
//            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime, AlarmManager.INTERVAL_DAY, pendingIntent);


        }
    }

    // Pass currently logged in user and log in time back when swipe back
    @Override
    public void onBackPressed() {
        Intent homeIntent = new Intent(CreateNewGoalActivity.this, ProjectEntryActivity.class);
        // Add currently logged in user and log in time to intent
        homeIntent.putExtra(CURRENT_USER, currentUser);
        homeIntent.putExtra(LOGIN_TIME, loginTime);
        homeIntent.putExtra("notification_id", notificationId);

        setResult(Activity.RESULT_OK, homeIntent);
        messagesRef.removeEventListener(messagesChildEventListener);

        super.onBackPressed();
    }

    // Pass currently logged in user and log in time back when click on triangle back
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the custom action bar's back button
            case android.R.id.home:
                Intent homeIntent = new Intent(CreateNewGoalActivity.this, ProjectEntryActivity.class);
                // Add currently logged in user and log in time to intent
                homeIntent.putExtra(CURRENT_USER, currentUser);
                homeIntent.putExtra(LOGIN_TIME, loginTime);
                homeIntent.putExtra("notification_id", notificationId);

                setResult(Activity.RESULT_OK, homeIntent);
                messagesRef.removeEventListener(messagesChildEventListener);

                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Dismiss any dialogs to avoid leakage
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (reminderDialog != null && reminderDialog.isShowing()) {
            reminderDialog.dismiss();
            isReminderDialogOpen = false;
        }
        if (reminderTimeDialog != null && reminderTimeDialog.isShowing()) {
            reminderTimeDialog.dismiss();
            isReminderTimeDialogOpen = false;
        }
        if (startDatePicker != null && startDatePicker.isShowing()) {
            startDatePicker.dismiss();
            isStartDateCalendarOpen = false;
        }
        if (endDatePicker != null && endDatePicker.isShowing()) {
            endDatePicker.dismiss();
            isEndDateCalendarOpen = false;
        }
        if (reminderInfoDialog != null && reminderInfoDialog.isShowing()) {
            reminderInfoDialog.dismiss();
            isReminderInfoDialogOpen = false;
        }
        if (priorityInfoDialog != null && priorityInfoDialog.isShowing()) {
            priorityInfoDialog.dismiss();
            isPriorityInfoDialogOpen = false;
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        // Store goal name
        outState.putString(GOAL_NAME, editGoalName.getText().toString());

        // Store selected icon
        outState.putInt(GOAL_ICON, iconAdapter.getSelectedIconPosition());

        // Store reminder info
        outState.putBoolean(GOAL_REMINDER, reminderOn);
        outState.putString(GOAL_REMINDER_MESSAGE, reminderMessage);
        outState.putInt(GOAL_REMINDER_HOUR, reminderHour);
        outState.putInt(GOAL_REMINDER_MINUTE, reminderMinute);
        outState.putInt(UNSAVED_HOUR, selectedHour);
        outState.putInt(UNSAVED_MINUTE, selectedMinute);

        // Store whether reminder info dialog is open
        outState.putBoolean(REMINDER_INFO_DIALOG_OPEN, isReminderInfoDialogOpen);

        // Store whether reminder dialog is open
        outState.putBoolean(REMINDER_DIALOG_OPEN, isReminderDialogOpen);
        if (isReminderDialogOpen) {
            outState.putString(UNSAVED_REMINDER_MESSAGE, editReminderMessage.getText().toString());
            outState.putInt(UNSAVED_HOUR, selectedHour);
            outState.putInt(UNSAVED_MINUTE, selectedMinute);
            Log.d(TAG, "onSaveInstanceState: " + selectedHour + ":" + selectedMinute);
        }

        // Store whether reminder time dialog is open
        outState.putBoolean(REMINDER_TIME_DIALOG_OPEN, isReminderTimeDialogOpen);

        // Store start/end date
        outState.putString(GOAL_START, dateFormat.format(startDate.getTime()));
        outState.putString(GOAL_END, dateFormat.format(endDate.getTime()));

        // Store whether start date dialog open
        outState.putBoolean(START_DATE_DIALOG, isStartDateCalendarOpen);
        if (isStartDateCalendarOpen) {
            DatePicker datePicker = startDatePicker.getDatePicker();
            outState.putInt(UNSAVED_START_YEAR, datePicker.getYear());
            outState.putInt(UNSAVED_START_MONTH, datePicker.getMonth());
            outState.putInt(UNSAVED_START_DAY, datePicker.getDayOfMonth());
        }

        // Store whether end date dialog open
        outState.putBoolean(END_DATE_DIALOG, isEndDateCalendarOpen);
        if (isEndDateCalendarOpen) {
            DatePicker datePicker = endDatePicker.getDatePicker();
            outState.putInt(UNSAVED_END_YEAR, datePicker.getYear());
            outState.putInt(UNSAVED_END_MONTH, datePicker.getMonth());
            outState.putInt(UNSAVED_END_DAY, datePicker.getDayOfMonth());
        }

        // Store whether priority info dialog is open
        outState.putBoolean(PRIORITY_INFO_DIALOG_OPEN, isPriorityInfoDialogOpen);

        // Store priority
        outState.putInt(GOAL_PRIORITY, priority);

        // Store memo
        outState.putString(GOAL_MEMO, editMemo.getText().toString());

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Restore goal name
        goalName = savedInstanceState.getString(GOAL_NAME);

        // Restore selected icon
        iconAdapter.setSelectedIconPosition(savedInstanceState.getInt(GOAL_ICON));

        // Restore reminder info
        reminderOn = savedInstanceState.getBoolean(GOAL_REMINDER);
        reminderMessage = savedInstanceState.getString(GOAL_REMINDER_MESSAGE);
        reminderHour = savedInstanceState.getInt(GOAL_REMINDER_HOUR);
        reminderMinute = savedInstanceState.getInt(GOAL_REMINDER_MINUTE);

        // Restore reminder info dialog if it was open
        isReminderInfoDialogOpen = savedInstanceState.getBoolean(REMINDER_INFO_DIALOG_OPEN);
        if (isReminderInfoDialogOpen) {
            displayReminderInfo(reminderQuestionMark);
        }

        // Restore reminder dialog if it was open
        isReminderDialogOpen = savedInstanceState.getBoolean(REMINDER_DIALOG_OPEN);
        isReminderTimeDialogOpen = savedInstanceState.getBoolean(REMINDER_TIME_DIALOG_OPEN);
        selectedHour = savedInstanceState.getInt(UNSAVED_HOUR);
        selectedMinute = savedInstanceState.getInt(UNSAVED_MINUTE);

        Log.d(TAG, "onRestoreInstanceState: selected" + selectedHour + ":" + selectedMinute);
        Log.d(TAG, "onRestoreInstanceState: reminder" + reminderHour + ":" + reminderMinute);

        if (isReminderDialogOpen) {
            showReminderDialog();

            // Restore any unsaved data for reminder message and time
            editReminderMessage.setText(savedInstanceState.getString(UNSAVED_REMINDER_MESSAGE));
            editReminderTime.setText(String.format("%02d:%02d", selectedHour, selectedMinute));

            Log.d(TAG, "onRestoreInstanceState: selected" + selectedHour + ":" + selectedMinute);
            Log.d(TAG, "onRestoreInstanceState: reminder" + reminderHour + ":" + reminderMinute);


            // Restore reminder time dialog if it was open
            if (isReminderTimeDialogOpen) {
                Log.d(TAG, "onRestoreInstanceState: here!");
                Log.d(TAG, "onRestoreInstanceState: " + selectedHour + ":" + selectedMinute);
                setReminderTime(selectedHour, selectedMinute);
            }
        }

        // Restore start/end date
        try {
            startDate.setTime(dateFormat.parse(savedInstanceState.getString(GOAL_START)));
            endDate.setTime(dateFormat.parse(savedInstanceState.getString(GOAL_END)));
        } catch (ParseException e) {
            Log.d(TAG, "onRestoreInstanceState: Error with restoring goal start/end date");
        }

        // Restore start date picker dialog if it was open + restore last selected date
        isStartDateCalendarOpen = savedInstanceState.getBoolean(START_DATE_DIALOG);
        if (isStartDateCalendarOpen) {
            // Restore date picker dialog for start date with last selected date
            int year = savedInstanceState.getInt(UNSAVED_START_YEAR);
            int month = savedInstanceState.getInt(UNSAVED_START_MONTH);
            int day = savedInstanceState.getInt(UNSAVED_START_DAY);
            showStartDatePickerDialog(year, month, day);
        }

        // Restore end date picker dialog if it was open + restore last selected date
        isEndDateCalendarOpen = savedInstanceState.getBoolean(END_DATE_DIALOG);
        if (isEndDateCalendarOpen) {
            // Restore date picker dialog for end date with last selected date
            int year = savedInstanceState.getInt(UNSAVED_END_YEAR);
            int month = savedInstanceState.getInt(UNSAVED_END_MONTH);
            int day = savedInstanceState.getInt(UNSAVED_END_DAY);
            showEndDatePickerDialog(year, month, day);
        }

        // Restore priority info dialog if it was open
        isPriorityInfoDialogOpen = savedInstanceState.getBoolean(PRIORITY_INFO_DIALOG_OPEN);
        if (isPriorityInfoDialogOpen) {
            displayPriorityInfo(priorityQuestionMark);
        }

        // Restore priority
        priority = savedInstanceState.getInt(GOAL_PRIORITY);

        // Restore memo
        memo = savedInstanceState.getString(GOAL_MEMO);
    }
    private String getCurrentDateStr() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy", Locale.US);
        String currentDateStr = dateFormat.format(new Date());
        return currentDateStr;
    }

    public void createNotificationChannel() {
        // This must be called early because it must be called before a notification is sent.
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notification Name";
            String description = "Notification Channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);

            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}