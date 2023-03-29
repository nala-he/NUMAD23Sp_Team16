package edu.northeastern.numad23sp_team16;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class GoalForItActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_for_it);
    }

    // TODO: remove temp button for creating new goal and associate onClick from Yuan's part to this method
    public void startCreateNewGoalActivity(View view) {
        startActivity(new Intent(GoalForItActivity.this, CreateNewGoalActivity.class));
    }
}