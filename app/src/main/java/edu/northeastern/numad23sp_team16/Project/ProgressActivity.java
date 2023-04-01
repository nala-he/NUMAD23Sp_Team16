package edu.northeastern.numad23sp_team16.Project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import edu.northeastern.numad23sp_team16.R;

public class ProgressActivity extends AppCompatActivity {

    TextView petName;
    ImageView petImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        // Get pet name and image from view
        petName = findViewById(R.id.progress_pet_name);
        petImage = findViewById(R.id.progress_pet_image);

        // TODO: replace with pet name and image from database
        // Set pet name and image for current user
        petName.setText("Juni");
        petImage.setImageResource(R.drawable.dog_small);
    }
}