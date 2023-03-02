package edu.northeastern.numad23sp_team16;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private EditText username;
    private Button loginButton;
    private Button closeButton;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Get Firebase Realtime Database instance
        database = FirebaseDatabase.getInstance().getReference();

        // Prompt user to log in
        login();
    }

    public void login() {
        // Set up login dialog
        AlertDialog.Builder login = new AlertDialog.Builder(this);

        // Create layout inflater
        LayoutInflater inflater = getLayoutInflater();
        // Create view based on custom dialog
        View view = inflater.inflate(R.layout.login_dialog, null);

        // Get username and login/close button from dialog
        username = view.findViewById(R.id.usernameInput);
        loginButton = view.findViewById(R.id.loginButton);
        closeButton = view.findViewById(R.id.closeButton);

        // Set view and ensure user cannot
        login.setView(view);
        login.setCancelable(false);

        // Create login dialog
        AlertDialog loginDialog = login.create();
        loginDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        loginDialog.show();


        // Click on login button - checks database for user
        // TODO: creates new user if username doesn't exist, logs in as user if user exists
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get username from input
                String user = username.getText().toString();

                Toast.makeText(getApplicationContext(), user, Toast.LENGTH_LONG).show();

                // Check database for user

                // Username exists - continue to StickItToEm activity as current user

                // Username doesn't exist - create new user and continue to StickItToEm activity
            }
        });

        // Click on close button - return to previous activity
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Return to previous activity
                loginDialog.dismiss();
                finish();
                return;
            }
        });

        // Handle back button press when user has not logged in
        loginDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    loginDialog.dismiss();
                    finish();
                }
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}