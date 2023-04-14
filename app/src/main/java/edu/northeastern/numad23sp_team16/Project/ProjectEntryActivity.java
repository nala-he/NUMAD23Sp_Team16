package edu.northeastern.numad23sp_team16.Project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import edu.northeastern.numad23sp_team16.R;

// TODO: to be merged or replaced with the login/sign in page activity created by Yuan
public class ProjectEntryActivity extends AppCompatActivity {

    private static final String CURRENT_USER = "CURRENT_USER";
    private String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_entry);

        // Get currently logged in user
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentUser = extras.getString(CURRENT_USER);
        }
    }

    public void startProfileActivity(View view) {
        startActivity(new Intent(ProjectEntryActivity.this, ProfileActivity.class));
    }

    public void startShareActivity(View view) {
        startActivity(new Intent(ProjectEntryActivity.this, ShareActivity.class));
    }

    // TODO: associate method to onClick for creating new goal button in home screen by Yuan
    public void startCreateNewGoalActivity(View view) {
        // Pass currently logged in user to create new goal
        Intent createGoalIntent = new Intent(ProjectEntryActivity.this,
                CreateNewGoalActivity.class);
        createGoalIntent.putExtra(CURRENT_USER, currentUser);
        startActivity(createGoalIntent);
    }

    public void startProgressActivity(View view) {
        startActivity(new Intent(ProjectEntryActivity.this, ProgressActivity.class));
    }

    // Receive currently logged in user from child activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data != null) {
                currentUser = data.getStringExtra(CURRENT_USER);
            }
        }
    }
}