package edu.northeastern.numad23sp_team16.Project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.numad23sp_team16.R;
import edu.northeastern.numad23sp_team16.models.Goal;

// TODO: to be merged or replaced with the login/sign in page activity created by Yuan
public class ProjectEntryActivity extends AppCompatActivity {
    LinearProgressIndicator progressIndicator;
    //FirebaseRecyclerAdapter adapter
    FirebaseRecyclerAdapter<Goal, GoalViewHolder> adapter;
    //data
    FirebaseRecyclerOptions<Goal> options;
    //display goals
    RecyclerView recyclerView;
    DatabaseReference goalsRef;
    //TODO:For test
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_entry);
        Intent intent = getIntent();
        userId = intent.getStringExtra("UserId");
        recyclerView = findViewById(R.id.goal_recycler_view);
        progressIndicator = findViewById(R.id.progress_bar);
        //Todo:change the value to the proportion from progress page
        progressIndicator.setProgress(50);
        // Get a reference to the "goals" node in the database
        goalsRef = FirebaseDatabase.getInstance().getReference("FinalProject").child("Goals");
        // Show the progress bar
        Query query = goalsRef.orderByChild("userId").equalTo(userId);
        // Add a ValueEventListener to the query
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //filter goals for current user
                List<Goal> filteredGoals = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Goal goal = snapshot.getValue(Goal.class);
                    if (goal != null) {
                        Log.d("Goal", "Goal: " + goal.getGoalName() + goal.getIcon() + ","+ goal.getPriority());
                        filteredGoals.add(goal);
                    }
                }
                //initialize goal options
                options = new FirebaseRecyclerOptions.Builder<Goal>().setQuery(query, Goal.class).build();
                //instantiate adapter
                adapter = new GoalAdapter(options);
                //this is the key to solving the problem-2hs
                adapter.startListening();
                //initialize recyclerview
                recyclerView.setLayoutManager(new LinearLayoutManager(ProjectEntryActivity.this));
                recyclerView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors here
                Log.e("ProjectEntryActivity", "Error retrieving goals: " + databaseError.getMessage());
            }
        });
        tapGoalToClockIn();



    }
    //tap any goal item in recylerview to show a customized dialog(dialog.xml) to clock in for today's goal
    //if finished, the background color turns green with a strike through line
    //the dialog should display like on 5/100 day
    private void tapGoalToClockIn() {

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public void startProfileActivity(View view) {
        startActivity(new Intent(ProjectEntryActivity.this, ProfileActivity.class));
    }

    public void startShareActivity(View view) {
        startActivity(new Intent(ProjectEntryActivity.this, ShareActivity.class));
    }

    // TODO: associate method to onClick for creating new goal button in home screen by Yuan
    public void startCreateNewGoalActivity(View view) {
        startActivity(new Intent(ProjectEntryActivity.this, CreateNewGoalActivity.class));
    }

    public void startProgressActivity(View view) {
        startActivity(new Intent(ProjectEntryActivity.this, ProgressActivity.class));
    }



}