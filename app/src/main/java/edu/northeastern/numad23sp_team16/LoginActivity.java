package edu.northeastern.numad23sp_team16;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    private EditText username;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Display login dialog
        AlertDialog.Builder login = new AlertDialog.Builder(this);

        // Create layout inflater
        LayoutInflater inflater = getLayoutInflater();
        // Create view based on custom dialog
        View view = inflater.inflate(R.layout.login_dialog, null);

        // Get username and login button from dialog

    }
}