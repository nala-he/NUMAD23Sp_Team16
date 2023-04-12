package edu.northeastern.numad23sp_team16.Project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import edu.northeastern.numad23sp_team16.R;
import edu.northeastern.numad23sp_team16.models.Goal;

public class GoalAdapter extends FirebaseRecyclerAdapter<Goal, GoalViewHolder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public GoalAdapter(@NonNull FirebaseRecyclerOptions<Goal> options) {
        super(options);
    }


    @Override
    protected void onBindViewHolder(@NonNull GoalViewHolder holder, int position, @NonNull Goal model) {
        // Bind the goal data to the view holder
        holder.bind(model);
    }

    @NonNull
    @Override
    public GoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.goal_item, parent, false);
        return new GoalViewHolder(view);
    }


}
