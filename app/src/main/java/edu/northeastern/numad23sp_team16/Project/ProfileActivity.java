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

import java.util.Objects;

import edu.northeastern.numad23sp_team16.R;
import edu.northeastern.numad23sp_team16.models.User;

public class ProfileActivity extends AppCompatActivity {
    private final String CURRENT_USER = "CURRENT_USER";
    private String currentUser;
    private DatabaseReference usersRef;
    private String email;
    private String password;
    private String petType;
    private String petName;

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
        TextInputEditText username_input = findViewById(R.id.username_input);
        TextInputEditText password_input = findViewById(R.id.password_input);
        TextInputEditText email_input = findViewById(R.id.email_input);
        TextInputEditText petname_input = findViewById(R.id.petname_input);
        RadioButton dog_button = findViewById(R.id.dog_radio_button);
        RadioButton cat_button = findViewById(R.id.cat_radio_button);

        //TODO: change to realtime data later

        // Retrieve currently logged in user -- Yutong
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentUser = extras.getString(CURRENT_USER);
        }
        // Connect with firebase
        usersRef = FirebaseDatabase.getInstance().getReference("FinalProject").child("FinalProjectUsers");
        usersRef.addChildEventListener(
                new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        User user = snapshot.getValue(User.class);
                        if (user != null && user.getUsername().equals(currentUser)) {
                            email = user.getEmail();
                            petName = user.getPetName();
                            petType = user.getPetType();
                            password = user.getPassword();

                            // Set the input field default data using the current user's info from the database -- Yutong
                            username_input.setText(currentUser);
                            password_input.setText(password);
                            email_input.setText(email);
                            petname_input.setText(petName);
                            dog_button.setChecked(Objects.equals(petType, "dog"));
                            cat_button.setChecked(Objects.equals(petType, "cat"));
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
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

    //TODO: change ProjectEntryActivity to HomeActivity after merging with Yuan's code
    public void onClickSaveUpdates(View view) {
        Toast.makeText(ProfileActivity.this, "Saved profile updates.",
                Toast.LENGTH_LONG).show();
        // TODO: save updates and then navigate back to home screen
        startActivity(new Intent(ProfileActivity.this, ProjectEntryActivity.class));
    }
}