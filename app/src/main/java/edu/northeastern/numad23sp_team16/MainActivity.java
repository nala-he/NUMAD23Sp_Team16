package edu.northeastern.numad23sp_team16;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import edu.northeastern.numad23sp_team16.A6.WebServiceActivity;
import edu.northeastern.numad23sp_team16.receivednotification.RealtimeDatabaseActivity;


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

    // test the received notification feature, to be revised or deleted later
    public void startRealtimeDatabaseActivity(View view) {
        startActivity(new Intent(MainActivity.this, RealtimeDatabaseActivity.class));
    }
}