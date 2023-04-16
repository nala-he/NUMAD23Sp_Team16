package edu.northeastern.numad23sp_team16.Project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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

public class ProjectEntryActivity extends AppCompatActivity {
    TextView progressIndicator;
    LinearProgressIndicator bar;
    //FirebaseRecyclerAdapter adapter
    FirebaseRecyclerAdapter<Goal, GoalViewHolder> adapter;
    //data
    FirebaseRecyclerOptions<Goal> options;
    //display goals
    RecyclerView recyclerView;
    DatabaseReference goalsRef;
    float percentageOfProgress;
    int checkedCount = 0;

    private static final String CURRENT_USER = "CURRENT_USER";
    //get userId of currentUser
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_entry);
        
        // Get currently logged in user
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString(CURRENT_USER);
        }
        recyclerView = findViewById(R.id.goal_recycler_view);
        progressIndicator = findViewById(R.id.goal_finish_text_view);
        bar = findViewById(R.id.progress_bar);
        // Get a reference to the "goals" node in the database
        goalsRef = FirebaseDatabase.getInstance().getReference("FinalProject").child("Goals");
        // Show the progress bar
        Query query = goalsRef.orderByChild("userId").equalTo(userId);
        // Add a ValueEventListener to the query
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Reset checkedCount,otherwise,checkedCount will be repeatedly added when loading view
                checkedCount = 0;
                //filter goals for current user
                List<Goal> filteredGoals = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Goal goal = snapshot.getValue(Goal.class);
                    if (goal != null) {
                        Log.d("Goal", "Goal: " + goal.getGoalName() + goal.getIcon() + ","+ goal.getPriority());
                        filteredGoals.add(goal);
                        if(goal.getIsCheckedForToday() == 1 && goal.getUserId().equals(userId)){
                            checkedCount++;
                        }
                    }
                }
                // Use the checkedCount value as needed
                Log.d("checkedCount", "Checked count for user " + userId + ": " + checkedCount);

                //initialize goal options
                options = new FirebaseRecyclerOptions.Builder<Goal>().setQuery(query, Goal.class).build();
                //instantiate adapter
                adapter = new GoalAdapter(options);

                //Todo:change the value to the proportion from progress page,(GoalAdapter)adapter).getItemCount() returns all the goals for today
                // Register a listener on the adapter to calculate percentage when data is loaded, otherwise it's always NaN
                adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                    @Override
                    public void onChanged() {
                        updateProgressPercentage();
                    }

                    @Override
                    public void onItemRangeChanged(int positionStart, int itemCount) {
                        updateProgressPercentage();
                    }

                    @Override
                    public void onItemRangeInserted(int positionStart, int itemCount) {
                        updateProgressPercentage();
                    }

                    @Override
                    public void onItemRangeRemoved(int positionStart, int itemCount) {
                        updateProgressPercentage();
                    }
                });
                //this is the key to solving the problem-2hs
                adapter.startListening();
                //initialize recyclerview
                recyclerView.setLayoutManager(new LinearLayoutManager(ProjectEntryActivity.this));
                recyclerView.setAdapter(adapter);
                //update percentage of progress
                updateProgressPercentage();



            }
            //get the isChecked sum in db, update percentage of progress
            public void updateProgressPercentage() {
                percentageOfProgress = (adapter.getItemCount() > 0) ? ( (float)checkedCount / adapter.getItemCount() * 100) : 0;
                bar.setProgress((int) percentageOfProgress);
                progressIndicator.setText("Today's goal completion "+ (int)percentageOfProgress +"%");
                Log.d("progress", "Today's goal completion " + checkedCount + " / "+ adapter.getItemCount());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors here
                Log.e("ProjectEntryActivity", "Error retrieving goals: " + databaseError.getMessage());
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        adapter.stopListening();
//
//    }

    public void startProfileActivity(View view) {
        startActivity(new Intent(ProjectEntryActivity.this, ProfileActivity.class));
    }

    public void startShareActivity(View view) {
        startActivity(new Intent(ProjectEntryActivity.this, ShareActivity.class));
    }

    
    public void startCreateNewGoalActivity(View view) {
        // Pass currently logged in user to create new goal
        Intent createGoalIntent = new Intent(ProjectEntryActivity.this,
                CreateNewGoalActivity.class);
        createGoalIntent.putExtra(CURRENT_USER, userId);
        startActivity(createGoalIntent);
    }

    public void startProgressActivity(View view) {
        startActivity(new Intent(ProjectEntryActivity.this, ProgressActivity.class));
    }



    // Receive currently logged in user from child activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data != null) {
                userId = data.getStringExtra(CURRENT_USER);
            }
        }
    }
}