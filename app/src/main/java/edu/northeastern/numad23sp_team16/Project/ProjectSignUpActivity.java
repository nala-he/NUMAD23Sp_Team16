package edu.northeastern.numad23sp_team16.Project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import edu.northeastern.numad23sp_team16.R;
import edu.northeastern.numad23sp_team16.models.User;

public class ProjectSignUpActivity extends AppCompatActivity {
    private EditText usernameInputText;
    private EditText emailInputText;
    private EditText passwordInputText;
    private EditText petNameInputText;
    private RadioGroup radioGroup;
    private RadioButton dog;
    private RadioButton cat;
    private DatabaseReference usersRef;

    private Button buttonSave;

    private String username;
    private String password;
    private String email;
    private String petName;
    private String whichPet;

    private static int userIdCounter = 1;
    private String userId;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_sign_up);

        // customize action bar back button and title
//        //customize tool bar
//        Toolbar toolbar = findViewById(R.id.signup_act_toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar_layout);
        actionBar.setHomeAsUpIndicator(R.drawable.back_button);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#D9D9D9")));
        actionBar.setDisplayHomeAsUpEnabled(true);
        // change action bar title
        AppCompatTextView title = findViewById(R.id.navbar_title);
        title.setText("Create a New Account");

        usernameInputText = findViewById(R.id.username_input_s);
        emailInputText = findViewById(R.id.email_input_s);
        passwordInputText= findViewById(R.id.password_input_s);
        petNameInputText = findViewById(R.id.petname_input_s);
        radioGroup = findViewById(R.id.pet_radio_group2);
        dog = findViewById(R.id.dog_radio_button2);
        cat = findViewById(R.id.cat_radio_button2);
        buttonSave = findViewById(R.id.save_profile_btn);
        //TODO:deal with screen rotation
//        if (savedInstanceState != null) {
//            usernameInputText.setText(savedInstanceState.getString("USERNAME"));
//            passwordInputText.setText(savedInstanceState.getString("PASSWORD"));
//            emailInputText.setText(savedInstanceState.getString("EMAIL"));
//            petNameInputText.setText(savedInstanceState.getString("PETNAME"));
//            int checkedRadioButtonId = savedInstanceState.getInt("checkedRadioButtonId");
//            if (checkedRadioButtonId != -1) {
//                radioGroup.check(checkedRadioButtonId);
//            }
//        }
        //check all fields filled and save into database
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkAllFieldsFilled()){
                    saveSignUpInfo2Firebase();
                } else {
                    Toast.makeText(getApplicationContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                }

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

    private void saveSignUpInfo2Firebase() {

        //get users reference, getReference("FinalProject") to distinguish from A8 data
        usersRef = FirebaseDatabase.getInstance().getReference("FinalProject").child("FinalProjectUsers");
        //check if the username has been used in the database
        Query query = usersRef.orderByChild("username").equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Username is already in use, show error message
                    Toast.makeText(getApplicationContext(), "This username has already been used.", Toast.LENGTH_SHORT).show();
                } else {
                    // username is not in use, save user to database
                    User user = new User(email, username, password, whichPet, petName);

//                    //add a child node with username as a unique key, can't use email as key because of "@"
//                    usersRef.child(username).setValue(user);

                    // add a child node to use the time as part of the unique user id, format "user16082271023" -- Yutong
                    String time = String.valueOf(System.currentTimeMillis()/1000);
                    userId = "user" + time + userIdCounter++;
                    usersRef.child(userId).setValue(user);

                    // show success message
                    Toast.makeText(getApplicationContext(), "Sign up successfully!", Toast.LENGTH_SHORT).show();

                    // go to login activity
                    startActivity(new Intent(ProjectSignUpActivity.this, ProjectLoginActivity.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Toast.makeText(getApplicationContext(), "Error checking username.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private boolean checkAllFieldsFilled() {
        //get all the info from the sign up activity, save them in firebase
//        username = Objects.requireNonNull(usernameInputText.getText()).toString();
//        password = Objects.requireNonNull(passwordInputText.getText()).toString();
//        email = Objects.requireNonNull(emailInputText.getText()).toString();
        username = usernameInputText.getText().toString().trim();
        password = passwordInputText.getText().toString().trim();
        email = emailInputText.getText().toString().trim();
        // Revise whichPet or petType data from id into "dog" or "cat" string
        if (radioGroup.getCheckedRadioButtonId() == -1) {
            whichPet = "-1";
        } else {
            whichPet = String.valueOf(radioGroup.getCheckedRadioButtonId())
                    .equals(String.valueOf(dog)) ? "dog" : "cat";
        }
//        whichPet = String.valueOf(radioGroup.getCheckedRadioButtonId());//getCheckedRadioButtonId() return -1 if radio button is not selected
        petName = petNameInputText.getText().toString().trim();
        if (email.isEmpty() || username.isEmpty() || password.isEmpty() || petName.isEmpty() || whichPet.equals("-1")) {
            // Show error message
            Toast.makeText(getApplicationContext(), "Please fill in all fields!", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("checkedRadioButtonId", radioGroup.getCheckedRadioButtonId());
        outState.putString("USERNAME", Objects.requireNonNull(usernameInputText.getText()).toString());
        outState.putString("PASSWORD", Objects.requireNonNull(passwordInputText.getText()).toString());
        outState.putString("EMAIL", Objects.requireNonNull(emailInputText.getText()).toString());
        outState.putString("PETNAME", Objects.requireNonNull(petNameInputText.getText()).toString());
    }

}