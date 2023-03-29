package edu.northeastern.numad23sp_team16;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.numad23sp_team16.models.Icon;

public class CreateNewGoalActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView iconRecyclerView;
    private IconAdapter iconAdapter;
    private List<Icon> iconsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_goal);

        // Set custom action bar with back button
        toolbar = findViewById(R.id.create_goal_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Add icons to icons list for recycler view
        iconsList = new ArrayList<>();
        iconsList.add(new Icon(R.drawable.icon_self_care, "Self-Care"));
        iconsList.add(new Icon(R.drawable.icon_growth, "Growth"));
        iconsList.add(new Icon(R.drawable.icon_social, "Social"));
        iconsList.add(new Icon(R.drawable.icon_productivity, "Productivity"));
        iconsList.add(new Icon(R.drawable.icon_health, "Health"));
        iconsList.add(new Icon(R.drawable.icon_hydration, "Hydration"));
        iconsList.add(new Icon(R.drawable.icon_finances, "Finances"));
        iconsList.add(new Icon(R.drawable.icon_language, "Language"));
        iconsList.add(new Icon(R.drawable.icon_reading, "Reading"));


        // Set recycler view to display icons for goal categories
        iconRecyclerView = findViewById(R.id.icons_recycler_view);
        iconRecyclerView.setLayoutManager(new GridLayoutManager(CreateNewGoalActivity.this, 5));
        iconAdapter = new IconAdapter(getApplicationContext(), iconsList);
        iconRecyclerView.setAdapter(iconAdapter);
    }
}