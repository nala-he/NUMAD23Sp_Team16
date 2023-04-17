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
import android.widget.Toast;

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
        Date sDate = null;
        Date eDate = null;
        Date currentDate =null;
        String startDateStr ;
        String endDateStr;
        String currentDateStr;
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
        try {
            holder.bind(model);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        // Get the ID of the goal object,update isCheckedForToday later
        String goalId = getRef(position).getKey();
        //get goal to display name and calculate days for popup dialog
        Goal goal = getItem(position);
        // Retrieve the start date and duration of the goal
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy", Locale.US);
        currentDateStr = dateFormat.format(new Date());
        try {
            sDate = dateFormat.parse(goal.getStartDate());
            eDate = dateFormat.parse(goal.getEndDate());
            currentDate = dateFormat.parse(currentDateStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        //can't clock in
        if(sDate.compareTo(currentDate) >0 || eDate.compareTo(currentDate) <0) {
            //not started or expired marked as grey
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.grey));
            //set background unclickable for clockin
            if(sDate.compareTo(currentDate) >0 || eDate.compareTo(currentDate) <0){
                //start in the future,//passed end date
                holder.itemView.setClickable(false);
                Toast.makeText(holder.itemView.getContext(), "Invalid to clock in due to date.",Toast.LENGTH_SHORT).show();
            }
        } else {
            //can clock in, click the item view, a popup dialog should display
            holder.itemView.setOnClickListener(v -> {
                try {
                    showClockInDialog(holder,goal,goalId, position);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            });

        }
        //display green background by checking if it has been clocked in
        if (goal.getIsCheckedForToday() == 1 && goal.getLastCheckedInDate().equals(currentDateStr)) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
            holder.goalNameTextView.setPaintFlags(holder.goalNameTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

    }
    //show a dialog to let the user clock in
    //click yes,progress bar would be affected
    //click no(in case the user clicked yes by mistake and wants to withdraw), cancel the record,progress bar gets updated
    //The name of the goal and day would be displayed in the dialog as well.
    private void showClockInDialog(GoalViewHolder holder, Goal goal, String goalId,int position ) throws ParseException {
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
        // Calculate the duration between the two dates in milliseconds
        long durationInMillis = eDate.getTime() - sDate.getTime();
        int diffInDaysFromStartToEnd = (int) TimeUnit.DAYS.convert(durationInMillis, TimeUnit.MILLISECONDS) + 1;
        // Calculate the current day from startDate
        long diffInMillies = currentDate.getTime() - sDate.getTime();
        int diffInDaysFromStartToNow = (int)TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) + 1;
        if(sDate.compareTo(currentDate) <=0 && eDate.compareTo(currentDate) >=0) {
            //start from past/today and end in today/future
            goalNameAndDay.setText( goal.getGoalName() +" "+" "+ diffInDaysFromStartToNow + "/" + diffInDaysFromStartToEnd +" day" );
            //clock in for today, background turns to green with a strike-through line
            lastCheckedInDate = currentDateStr;
            yesButton.setOnClickListener(v -> {
                //to save in the db whether the item view has been changed to green,isCheckedForToday = 1->checked
                    if (goal.getIsCheckedForToday() == 0 || (goal.getIsCheckedForToday() == 1 && !goal.getLastCheckedInDate().equals(currentDateStr))) {
                        isCheckedForToday = 1;
                        lastCheckedInDate = currentDateStr;
                        goalRef = FirebaseDatabase.getInstance().getReference("FinalProject").child("Goals").child(goalId);
                        //update this goal as checked in db
                        goalRef.child("isCheckedForToday").setValue(isCheckedForToday);
                        goalRef.child("lastCheckedInDate").setValue(lastCheckedInDate);
                    }
                dialog.dismiss();
            });
            noButton.setOnClickListener(v -> {
                    //to save in the db whether the item view has been changed to green,isCheckedForToday = 1->checked
                    if (goal.getIsCheckedForToday() == 1 && goal.getLastCheckedInDate().equals(currentDateStr)) {
                        isCheckedForToday = 0;
                        lastCheckedInDate = "";
                        goalRef = FirebaseDatabase.getInstance().getReference("FinalProject").child("Goals").child(goalId);
//                    holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.light_pink));
//                    textViewForGoal.setPaintFlags(textViewForGoal.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        //update this goal as checked in db
                        goalRef.child("isCheckedForToday").setValue(isCheckedForToday);
                        goalRef.child("lastCheckedInDate").setValue(lastCheckedInDate);

                    }
                dialog.dismiss();
            });
        }
    }

    @NonNull
    @Override
    public GoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.goal_item, parent, false);
        return new GoalViewHolder(view);
    }


}
