package edu.northeastern.numad23sp_team16.Project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import edu.northeastern.numad23sp_team16.R;

public class ProjectStartActivity extends AppCompatActivity {
    private Button btnLogin;
    private Button btnSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_start);

            //click to log in
            btnLogin = findViewById(R.id.button2);
            //click to sign up
            btnSignUp = findViewById(R.id.button3);
            //customize tool bar
            Toolbar toolbar = findViewById(R.id.start_act_toolbar);
            setSupportActionBar(toolbar);

            btnLogin.setOnClickListener(v -> startActivity(new Intent(this, ProjectLoginActivity.class)));
            btnSignUp.setOnClickListener(v -> startActivity(new Intent(this, ProjectSignUpActivity.class)));


    }
}