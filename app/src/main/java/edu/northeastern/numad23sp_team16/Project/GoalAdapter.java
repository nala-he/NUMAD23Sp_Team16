package edu.northeastern.numad23sp_team16.Project;

import android.app.AlertDialog;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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
        //get goal to display name and calculate days for popup dialog
        Goal goal = getItem(position);
        //click the item view, a popup dialog should display
        holder.itemView.setOnClickListener(v -> {
            showClockInDialog(holder,goal);
        });
    }
    //show a dialog to let the user clock in
    //click yes,progress bar would be affected
    //click no(in case the user clicked yes by mistake and want to withdraw), cancel the record,progress bar gets updated
    //The name of the goal+ day would be displayed as well.
    private void showClockInDialog(GoalViewHolder holder, Goal goal) {
        // Inflate dialog view using finish_goal_dialog.xml
        View dialogView = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.finish_goal_dialog, null);
        //build a dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();
        //get views in this dialogView
        RelativeLayout relativeLayout = (RelativeLayout)dialogView;
        TextView goalNameAndDay = relativeLayout.findViewById(R.id.description_textview);
        Button yesButton =relativeLayout.findViewById(R.id.yes_button);
        Button noButton =relativeLayout.findViewById(R.id.no_button);
        ImageView closeImageView =relativeLayout.findViewById(R.id.close_button);
        //clock in for today, background turns to green with a strike-through line
        yesButton.setOnClickListener(v -> {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
            RelativeLayout layout = (RelativeLayout)holder.itemView;
            TextView textView = layout.findViewById(R.id.goal_textview);
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            dialog.dismiss();
        });
        //cancel the record
        noButton.setOnClickListener(v -> {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.light_pink));
            RelativeLayout layout = (RelativeLayout)holder.itemView;
            TextView textView = layout.findViewById(R.id.goal_textview);
            textView.setPaintFlags(textView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);

        });
        //dismiss the dialog
        closeImageView.setOnClickListener(v -> dialog.dismiss());
        // display goal+ day
        goalNameAndDay.setText(""+ goal.getGoalName());

    }

    @NonNull
    @Override
    public GoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.goal_item, parent, false);
        return new GoalViewHolder(view);
    }


}
