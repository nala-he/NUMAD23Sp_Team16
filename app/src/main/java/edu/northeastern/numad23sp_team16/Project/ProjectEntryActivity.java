package edu.northeastern.numad23sp_team16.Project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.sql.Timestamp;

import edu.northeastern.numad23sp_team16.R;

// TODO: to be merged or replaced with the login/sign in page activity created by Yuan
public class ProjectEntryActivity extends AppCompatActivity {
    private final String CURRENT_USER = "CURRENT_USER";
    private final String LOGIN_TIME = "LOGIN_TIME";

    private String currentUser;
    private String loginTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_entry);

        // Retrieve currently logged in user -- Yutong
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentUser = extras.getString(CURRENT_USER);
            loginTime = extras.getString(LOGIN_TIME);
        }
    }

    public void startProfileActivity(View view) {
        // Pass the current user info to Profile activity -- Yutong
        Intent intent = new Intent(ProjectEntryActivity.this, ProfileActivity.class);
        intent.putExtra(CURRENT_USER, currentUser);
        startActivity(intent);
    }

    public void startShareActivity(View view) {
        // Pass the current user info to Share activity -- Yutong
        Intent intent = new Intent(ProjectEntryActivity.this, ShareActivity.class);
        intent.putExtra(CURRENT_USER, currentUser);
        intent.putExtra(LOGIN_TIME, loginTime);
        startActivity(intent);
    }

    // TODO: associate method to onClick for creating new goal button in home screen by Yuan
    public void startCreateNewGoalActivity(View view) {
        // Pass the current user info to Create New Goal activity -- Yutong
        Intent intent = new Intent(ProjectEntryActivity.this, CreateNewGoalActivity.class);
        intent.putExtra(CURRENT_USER, currentUser);
        startActivity(intent);
    }

    public void startProgressActivity(View view) {
        // Pass the current user info to Progress activity -- Yutong
        Intent intent = new Intent(ProjectEntryActivity.this, ProgressActivity.class);
        intent.putExtra(CURRENT_USER, currentUser);
        startActivity(intent);
    }
}