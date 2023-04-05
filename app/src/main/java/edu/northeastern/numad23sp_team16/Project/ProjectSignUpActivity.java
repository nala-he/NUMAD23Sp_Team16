package edu.northeastern.numad23sp_team16.Project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import edu.northeastern.numad23sp_team16.R;
//This is the User model for final project
import edu.northeastern.numad23sp_team16.models.User;

public class ProjectSignUpActivity extends AppCompatActivity {
    private EditText usernameInputText;
    private EditText emailInputText;
    private EditText passwordInputText;
    private EditText petNameInputText;
    private RadioGroup radioGroup;
    private DatabaseReference usersRef;

    private Button buttonSave;

    String username;
    String password;
    String email;
    String petName;
    String whichPet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_sign_up);
        //customize tool bar
        Toolbar toolbar = findViewById(R.id.signup_act_toolbar);
        setSupportActionBar(toolbar);
        //click back button
        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        usernameInputText = findViewById(R.id.username_input_s);
        passwordInputText = findViewById(R.id.password_input_s);
        petNameInputText = findViewById(R.id.petname_input_s);
        emailInputText = findViewById(R.id.email_input_s);
        radioGroup = findViewById(R.id.pet_radio_group2);
        buttonSave = findViewById(R.id.save_profile_btn);
        //deal with screen rotation
        if (savedInstanceState != null) {
            usernameInputText.setText(savedInstanceState.getString("USERNAME"));
            passwordInputText.setText(savedInstanceState.getString("PASSWORD"));
            emailInputText.setText(savedInstanceState.getString("EMAIL"));
            petNameInputText.setText(savedInstanceState.getString("PETNAME"));
            int checkedRadioButtonId = savedInstanceState.getInt("checkedRadioButtonId");
            if (checkedRadioButtonId != -1) {
                radioGroup.check(checkedRadioButtonId);
            }
        }
        //get all the info from the sign up activity, save them in firebase
        username = Objects.requireNonNull(usernameInputText.getText()).toString();
        password = Objects.requireNonNull(passwordInputText.getText()).toString();
        email = Objects.requireNonNull(emailInputText.getText()).toString();
        whichPet = String.valueOf(radioGroup.getCheckedRadioButtonId());//getCheckedRadioButtonId() return -1 if radio button is not selected
        petName = Objects.requireNonNull(petNameInputText.getText()).toString();
        // Check if required fields are not empty
        if(checkAllFieldsFilled(username,password,email,whichPet,petName)) {
            //click save button, call saveSignUpInfo2Firebase()
            buttonSave.setOnClickListener(v -> saveSignUpInfo2Firebase());
        }

    }
    private void saveSignUpInfo2Firebase() {
        //get users reference, getReference("FinalProject") to distinguish from A8 data
        usersRef = FirebaseDatabase.getInstance().getReference("FinalProject").child("FinalProjectUsers");
        //check if the email has been used in the database
        Query query = usersRef.orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Email is already in use, show error message
                    Toast.makeText(getApplicationContext(), "This email has already been used.", Toast.LENGTH_SHORT).show();
                } else {
                    // email is not in use, save user to database
                    User user = new User(email, username, password, whichPet, petName);
                    //add a child node with email as a unique key
                    usersRef.child(email).setValue(user);
                    // show success message
                    Toast.makeText(getApplicationContext(), "Sign up successfully!", Toast.LENGTH_SHORT).show();
                    // go to login activity
                    startActivity(new Intent(ProjectSignUpActivity.this, ProjectLoginActivity.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Toast.makeText(getApplicationContext(), "Error checking email.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private boolean checkAllFieldsFilled(String username,String password,String email,String whichPet,String petName) {
        if (email.isEmpty() || username.isEmpty() || password.isEmpty() || petName.isEmpty()) {
            // Show error message
            Toast.makeText(getApplicationContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (whichPet.equals("-1")) {
            // No radio button selected, show reminder message
            Toast.makeText(getApplicationContext(), "Please select a pet.", Toast.LENGTH_SHORT).show();
            return false;

        }
        return true;
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