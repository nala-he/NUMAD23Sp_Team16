package edu.northeastern.numad23sp_team16;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import edu.northeastern.numad23sp_team16.A6.WebServiceActivity;
import edu.northeastern.numad23sp_team16.A8.LoginActivity;
import edu.northeastern.numad23sp_team16.Project.ProjectEntryActivity;
import edu.northeastern.numad23sp_team16.Project.ProjectStartActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startWebServiceActivity(View view) {
        startActivity(new Intent(MainActivity.this, WebServiceActivity.class));
    }

    public void startAboutActivity(View view) {
        startActivity(new Intent(MainActivity.this, AboutActivity.class));
    }

    public void startStickItToEmActivity(View view) {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

    public void startProjectActivity(View view) {
        startActivity(new Intent(MainActivity.this, ProjectEntryActivity.class));
    }
    //go to login/signup
    public void startProjectStartActivity(View view) {
        startActivity(new Intent(MainActivity.this, ProjectStartActivity.class));

    }
}