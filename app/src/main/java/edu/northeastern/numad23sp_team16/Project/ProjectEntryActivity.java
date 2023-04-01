package edu.northeastern.numad23sp_team16.Project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import edu.northeastern.numad23sp_team16.R;

// TODO: to be merged or replaced with the login/sign in page activity created by Yuan
public class ProjectEntryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_entry);
    }

    public void startProfileActivity(View view) {
        startActivity(new Intent(ProjectEntryActivity.this, ProfileActivity.class));
    }

    public void startShareActivity(View view) {
        startActivity(new Intent(ProjectEntryActivity.this, ShareActivity.class));
    }

    public void startProgressActivity(View view) {
        startActivity(new Intent(ProjectEntryActivity.this, ProgressActivity.class));
    }
}