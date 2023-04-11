package edu.northeastern.numad23sp_team16.Project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;

import edu.northeastern.numad23sp_team16.R;
import edu.northeastern.numad23sp_team16.models.User;

public class ProjectLoginActivity extends AppCompatActivity {
    private EditText usernameInput;
    private EditText passwordInput;
    private String password;
    //use firebaseAuth for authentication
    private DatabaseReference mDatabase;
    private Button btnLogin;
    private String username;
    private DatabaseReference usersRef;

    private final String CURRENT_USER = "CURRENT_USER";
    private final String LOGIN_TIME = "LOGIN_TIME";

    private String currentUser;
    private Timestamp loginTime;


    public ProjectLoginActivity() {
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_login);
        //customize tool bar
        Toolbar toolbar = findViewById(R.id.login_act_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        usernameInput = findViewById(R.id.edittext_username);
        passwordInput = findViewById(R.id.edittext_password);
        btnLogin = findViewById(R.id.button_login);

        // get the login time
        Date date = new Date();
        loginTime = new Timestamp(date.getTime());

        // Initialize Firebase Database
        //mDatabase = FirebaseDatabase.getInstance().getReference("FinalProject")
        usersRef = FirebaseDatabase.getInstance().getReference("FinalProject").child("FinalProjectUsers");
        //deal with screen rotation
        if(savedInstanceState!=null){
            usernameInput.setText(savedInstanceState.getString("USERNAME"));
            passwordInput.setText(savedInstanceState.getString("PASSWORD"));
        }
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = usernameInput.getText().toString().trim();
                password = passwordInput.getText().toString().trim();
                Log.d("entered username",username);
                Log.d("entered password",password);

                if (username.isEmpty()) {
                    usernameInput.setError("Please enter username");
                    return;
                }

                if (password.isEmpty()) {
                    passwordInput.setError("Please enter password");
                    return;
                }

                // Retrieve the user from the Firebase Realtime Database using the entered username
                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        boolean userExist = false;
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            if (Objects.requireNonNull(data.getValue(User.class)).getUsername().equals(username)) {
                                // User exists, check if the password is correct
                                String storedPassword = Objects.requireNonNull(data.getValue(User.class)).getPassword();

                                Log.d("password stored in database:", storedPassword);
                                userExist = true;
                                if (password.equals(storedPassword)) {
                                    Toast.makeText(ProjectLoginActivity.this,
                                            "Authentication successful.", Toast.LENGTH_SHORT).show();

                                    // Save the currently logged in username and pass it across the app -- Yutong
                                    Intent intent = new Intent(ProjectLoginActivity.this,
                                            ProjectEntryActivity.class);
                                    // pass the user id
                                    currentUser = data.getKey();
                                    intent.putExtra(CURRENT_USER, currentUser);
                                    intent.putExtra(LOGIN_TIME, loginTime.toString());
                                    startActivity(intent);

                                } else {
                                    // Password incorrect
                                    Toast.makeText(ProjectLoginActivity.this, "Incorrect password.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        if (!userExist) {
                            // User does not exist
                            Toast.makeText(ProjectLoginActivity.this, "User does not exist.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle database error
                    }
                });
            }
        });

    }

    // this event will enable the back function to the back button on press in customized action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("USERNAME",username);
        outState.putString("PASSWORD", password);
    }
}