package edu.northeastern.numad23sp_team16.Project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import edu.northeastern.numad23sp_team16.R;
import edu.northeastern.numad23sp_team16.models.User;

public class ProfileActivity extends AppCompatActivity {
    private final String CURRENT_USER = "CURRENT_USER";
    private String username;
    private DatabaseReference usersRef;
    private String email;
    private String password;
    private String petType;
    private String petName;
    private TextInputEditText username_input;
    private TextInputEditText password_input;
    private TextInputEditText email_input;
    private TextInputEditText petname_input;
    private RadioButton dog_button;
    private RadioButton cat_button;
    // current logged-in user's userId in the database
    private String currentUser;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // customize action bar back button and title
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar_layout);
        actionBar.setHomeAsUpIndicator(R.drawable.back_button);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#D9D9D9")));
        actionBar.setDisplayHomeAsUpEnabled(true);

        // set user profile values to textInputEditText
        username_input = findViewById(R.id.username_input);
        password_input = findViewById(R.id.password_input);
        email_input = findViewById(R.id.email_input);
        petname_input = findViewById(R.id.petname_input);
        dog_button = findViewById(R.id.dog_radio_button);
        cat_button = findViewById(R.id.cat_radio_button);

        // Retrieve currently logged in user -- Yutong
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentUser = extras.getString(CURRENT_USER);
        }
        // Connect with firebase
        usersRef = FirebaseDatabase.getInstance().getReference("FinalProject").child("FinalProjectUsers");
        usersRef.child(currentUser).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        username = dataSnapshot.child("username").getValue(String.class);
                        email = dataSnapshot.child("email").getValue(String.class);
                        petName = dataSnapshot.child("petName").getValue(String.class);
                        petType = dataSnapshot.child("petType").getValue(String.class);
                        password = dataSnapshot.child("password").getValue(String.class);

                        // Set the input field default data using the current user's info from the database -- Yutong
                        username_input.setText(username);
                        password_input.setText(password);
                        email_input.setText(email);
                        petname_input.setText(petName);
                        dog_button.setChecked(Objects.equals(petType, "dog"));
                        cat_button.setChecked(Objects.equals(petType, "cat"));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
//                new ChildEventListener() {
//                    @Override
//                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                        User user = snapshot.getValue(User.class);
//                        if (user != null && user.getUsername().equals(username)) {
//                            email = user.getEmail();
//                            petName = user.getPetName();
//                            petType = user.getPetType();
//                            password = user.getPassword();
//
//                            // Set the input field default data using the current user's info from the database -- Yutong
//                            username_input.setText(username);
//                            password_input.setText(password);
//                            email_input.setText(email);
//                            petname_input.setText(petName);
//                            dog_button.setChecked(Objects.equals(petType, "dog"));
//                            cat_button.setChecked(Objects.equals(petType, "cat"));
//                        }
//                    }
//
//                    @Override
//                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//                    }
//
//                    @Override
//                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//
//                    }
//
//                    @Override
//                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                }
        );


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

    public void onClickSaveUpdates(View view) {
        Toast.makeText(ProfileActivity.this, "Saved profile updates.",
                Toast.LENGTH_LONG).show();

        // TODO: save updates to database and then navigate back to home screen
        if (!isAllFieldsFilled()) {
            Toast.makeText(getApplicationContext(), "Please fill in all fields!", Toast.LENGTH_SHORT).show();
        } else {
            // save the updates to database
            updateChangedFields();

            // pass the current user's username to entry activity, in case it's getting updated
            Intent intent = new Intent(ProfileActivity.this, ProjectEntryActivity.class);
            intent.putExtra(CURRENT_USER, currentUser);
            startActivity(intent);
        }
    }

    private boolean isAllFieldsFilled() {
        return username_input.getText() != null && password_input.getText() != null
                && petname_input.getText() != null && email_input.getText() != null
                && (cat_button.isChecked() || dog_button.isChecked())
                && !username_input.getText().toString().trim().isEmpty()
                && !petname_input.getText().toString().trim().isEmpty()
                && !email_input.getText().toString().trim().isEmpty()
                && !password_input.getText().toString().trim().isEmpty();
    }

    private void updateChangedFields() {
        String usernameOnSave = Objects.requireNonNull(username_input.getText()).toString().trim();
        String petNameOnSave = Objects.requireNonNull(petname_input.getText()).toString().trim();
        String passwordOnSave = Objects.requireNonNull(password_input.getText()).toString().trim();
        String emailOnSave = Objects.requireNonNull(email_input.getText()).toString().trim();
        String petTypeOnSave = dog_button.isChecked() ? "dog" : "cat";
        if (!username.equals(usernameOnSave)) {
            usersRef.child(currentUser).child("username")
                    .setValue(usernameOnSave);
        }
        if (!petName.equals(petNameOnSave)) {
            usersRef.child(currentUser).child("petName")
                    .setValue(petNameOnSave);
        }
        if (!password.equals(passwordOnSave)) {
            usersRef.child(currentUser).child("password")
                    .setValue(passwordOnSave);
        }
        if (!email.equals(emailOnSave)) {
            usersRef.child(currentUser).child("email")
                    .setValue(emailOnSave);
        }
        if (!petType.equals(petTypeOnSave)) {
            usersRef.child(currentUser).child("petType")
                    .setValue(petTypeOnSave);
        }
    }

}