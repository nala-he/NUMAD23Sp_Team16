package edu.northeastern.numad23sp_team16;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class StickItToEmActivity extends AppCompatActivity {

    private final String CURRENT_USER = "CURRENT_USER";
    private String currentUser;
    private TextView currentlyLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stick_it_to_em);

        // Retrieve currently logged in user
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentUser = extras.getString(CURRENT_USER);
        }

        // Set title for currently logged in user
        currentlyLoggedIn = findViewById(R.id.currentUserTitle);
        currentlyLoggedIn.setText("Currently Logged In: " + currentUser);
    }
}