package edu.northeastern.numad23sp_team16.Project;

import android.app.AlertDialog;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import edu.northeastern.numad23sp_team16.R;
import edu.northeastern.numad23sp_team16.models.Goal;

public class GoalAdapter extends FirebaseRecyclerAdapter<Goal, GoalViewHolder> {

        private int isCheckedForToday;
        //if lastCheckedInDate!=currentDate,isCheckedForToday=0
        private String lastCheckedInDate;
        DatabaseReference goalRef;
        //click yes button to update background
        RelativeLayout layoutForItemView;
        TextView textViewForGoal;
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
        // Get the ID of the goal object,update isCheckedForToday later
        String goalId = getRef(position).getKey();
        //get goal to display name and calculate days for popup dialog
        Goal goal = getItem(position);
        layoutForItemView = (RelativeLayout) holder.itemView;
        textViewForGoal = layoutForItemView.findViewById(R.id.goal_textview);
        //click the item view, a popup dialog should display
        holder.itemView.setOnClickListener(v -> {
            try {
                showClockInDialog(holder,goal,goalId);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });
    }
    //show a dialog to let the user clock in
    //click yes,progress bar would be affected
    //click no(in case the user clicked yes by mistake and want to withdraw), cancel the record,progress bar gets updated
    //The name of the goal+ day would be displayed as well.
    private void showClockInDialog(GoalViewHolder holder, Goal goal, String goalId ) throws ParseException {
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
        ImageView closeImageView =relativeLayout.findViewById(R.id.close_button);

        LinearLayout btnLinearLayout = relativeLayout.findViewById(R.id.btn_linear_layout);
        Button yesButton =btnLinearLayout.findViewById(R.id.yes_button);
        Button noButton =btnLinearLayout.findViewById(R.id.no_button);

        //dismiss the dialog
        closeImageView.setOnClickListener(v -> dialog.dismiss());

        //current day
        // Retrieve the start date and duration of the goal
        String startDate = goal.getStartDate();
        String endDate = goal.getEndDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy", Locale.US);

        Date sDate = dateFormat.parse(startDate);
        Date eDate = dateFormat.parse(endDate);
        // Calculate the duration between the two dates in milliseconds
        long durationInMillis = eDate.getTime() - sDate.getTime();
        //+1
        int diffInDaysFromStartToEnd = (int) TimeUnit.DAYS.convert(durationInMillis, TimeUnit.MILLISECONDS) + 1;

        // Calculate the current day from startDate
        Date currentDate = new Date(); // or use System.currentTimeMillis() for better performance
        //long diffInMillies = Math.abs(currentDate.getTime() - sDate.getTime());
        long diffInMillies = currentDate.getTime() - sDate.getTime();
        int diffInDaysFromStartToNow = (int)TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        if(diffInDaysFromStartToNow < 0){
            //start from today and end in the future
            goalNameAndDay.setText("Start date for this goal has not come yet.");
        } else if(diffInDaysFromStartToNow == 0 && diffInDaysFromStartToEnd!=0) {
            //start from today and end in today
            goalNameAndDay.setText( goal.getGoalName() +" "+" "+ (diffInDaysFromStartToNow+1) + "/" + diffInDaysFromStartToEnd );
        } else if(diffInDaysFromStartToNow == 0) {
            //start from today and end in today
            goalNameAndDay.setText( goal.getGoalName() +" "+" "+  "1 / 1" );
        } else {
            // display goal+ day
            goalNameAndDay.setText( goal.getGoalName() +" "+" "+ diffInDaysFromStartToNow + "/" + diffInDaysFromStartToEnd );
        }
        if(diffInDaysFromStartToNow >= 0) {
            //clock in for today, background turns to green with a strike-through line
            yesButton.setOnClickListener(v -> {

                        //to save in the db whether the item view has been changed to green,isCheckedForToday = 1->checked
                        if (isCheckedForToday == 0) {
                            isCheckedForToday = 1;
                            lastCheckedInDate = dateFormat.format(currentDate);
                            goalRef = FirebaseDatabase.getInstance().getReference("FinalProject").child("Goals").child(goalId);
                            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
                            textViewForGoal.setPaintFlags(textViewForGoal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            //update this goal as not checked
                            goalRef.child("isCheckedForToday").setValue(isCheckedForToday);
                            goalRef.child("lastCheckedInDate").setValue(lastCheckedInDate);
                            // Notify the adapter that the data has changed
                            notifyDataSetChanged();
                        }

                        dialog.dismiss();
            });
            noButton.setOnClickListener(v -> dialog.dismiss());
        }

    }

    @NonNull
    @Override
    public GoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.goal_item, parent, false);
        return new GoalViewHolder(view);
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        // store the countOfGoals in the database and update it whenever there is a change
    }


}
