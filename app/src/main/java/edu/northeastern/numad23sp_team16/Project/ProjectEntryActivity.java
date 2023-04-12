package edu.northeastern.numad23sp_team16.Project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_entry);
        Intent intent = getIntent();
        //TODO:get current userId from Intent
        String userId = intent.getStringExtra("UserId");
        progressIndicator = findViewById(R.id.progress_bar);

        // test:set the progress to 50%
        //Todo:change the value to the proportion from progress page
        progressIndicator.setProgress(50);
        // Get a reference to the "goals" node in the database
        DatabaseReference goalsRef = FirebaseDatabase.getInstance().getReference("FinalProject").child("Goals");
        //initialize goal options
        options = new FirebaseRecyclerOptions.Builder<Goal>()
                        .setQuery(goalsRef.orderByChild("userId").equalTo(userId), Goal.class)
                        .build();
        //instantiate adapter
        adapter = new GoalAdapter(options);
        //initialize recyclerview
        recyclerView = findViewById(R.id.goal_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        //addChildEventListener to the database reference for the "goals" node, whenever a goal
        //is added for current user, the adapter get informed and update ui
        goalsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //get new goal
                Goal goal = snapshot.getValue(Goal.class);
                //Is the goal current user's? If so, inform adaptor.
                if(goal.getUserId().equals(userId)){
                    //a new Goal is added to the Firebase Realtime Database, it will be automatically displayed in the RecyclerView
                    adapter.notifyDataSetChanged();
                    //TODO: need test, no need to manually update the RecyclerView
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
        });



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