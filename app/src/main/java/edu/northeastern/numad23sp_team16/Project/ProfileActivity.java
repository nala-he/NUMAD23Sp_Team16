package edu.northeastern.numad23sp_team16.Project;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RadioButton;

import com.google.android.material.textfield.TextInputEditText;

import edu.northeastern.numad23sp_team16.R;

public class ProfileActivity extends AppCompatActivity {

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

        //TODO: change to realtime data later
        username_input.setText("FirstUser");
        password_input.setText("FirstUser");
        email_input.setText("firstUser@gmail.com");
        petname_input.setText("Juni");
    }

    // this event will enable the back function to the back button on press in customized action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}