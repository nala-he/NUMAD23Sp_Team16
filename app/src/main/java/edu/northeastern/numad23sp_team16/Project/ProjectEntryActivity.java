package edu.northeastern.numad23sp_team16.Project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import edu.northeastern.numad23sp_team16.R;

// TODO: to be merged or replaced with the login/sign in page activity created by Yuan
public class ProjectEntryActivity extends AppCompatActivity {
    LinearProgressIndicator progressIndicator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_entry);
        progressIndicator = findViewById(R.id.progress_bar);

        // test:set the progress to 50%
        //Todo:change the value to the proportion from progress page
        progressIndicator.setProgress(50);

        /**
        // Define the current value and maximum value of the number
        int currentValue = 5;
        int maxValue = 10;
        float proportion = (float) currentValue / (float) maxValue;

        // Set the progress of the LinearProgressIndicator based on the proportion
        progressIndicator.setProgress((int) (proportion * 100));
         **/


    }

    public void startProfileActivity(View view) {
        startActivity(new Intent(ProjectEntryActivity.this, ProfileActivity.class));
    }

    public void startShareActivity(View view) {
        startActivity(new Intent(ProjectEntryActivity.this, ShareActivity.class));
    }

    // TODO: associate method to onClick for creating new goal button in home screen by Yuan
    public void startCreateNewGoalActivity(View view) {
        startActivity(new Intent(ProjectEntryActivity.this, CreateNewGoalActivity.class));
    }

    public void startProgressActivity(View view) {
        startActivity(new Intent(ProjectEntryActivity.this, ProgressActivity.class));
    }
}