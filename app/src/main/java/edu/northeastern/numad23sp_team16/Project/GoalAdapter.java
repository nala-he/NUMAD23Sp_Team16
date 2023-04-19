package edu.northeastern.numad23sp_team16.Project;

import android.app.AlertDialog;
import android.graphics.Paint;
import android.util.Log;
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
        private boolean isToastShown = false;
        AlertDialog dialog;
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
    protected void onBindViewHolder(@NonNull GoalViewHolder holder, int position, @NonNull Goal goal) {
        // Get the ID of the goal object,update isCheckedForToday later
        String goalId = getRef(position).getKey();

        // Bind the goal data to the view holder
        try {
            holder.bind(goal);
            if (isNotStarted(goal)) {
                holder.goalNameTextView.setText(goal.getGoalName() + ": (start soon)");
                //not started or expired marked as grey
                holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.grey));
                holder.itemView.setOnClickListener(v ->Toast.makeText(holder.itemView.getContext(), "Invalid to clock in. Not started.", Toast.LENGTH_SHORT).show());
            } else if (hasExpired(goal)) {
                holder.goalNameTextView.setText(goal.getGoalName() + ": (end date passed)");
                holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.grey));
                holder.itemView.setOnClickListener(v -> Toast.makeText(holder.itemView.getContext(), "Invalid to clock in. It has ended.", Toast.LENGTH_SHORT).show());
            } else {
                holder.goalNameTextView.setText(goal.getGoalName());
                //display green background by checking if it has been clocked in
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy", Locale.US);
                String currentDateStr= dateFormat.format(new Date());
                if (goal.getIsCheckedForToday() == 1 && goal.getLastCheckedInDate().equals(currentDateStr)) {
                    holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
                    holder.goalNameTextView.setPaintFlags(holder.goalNameTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
                // Set a click listener
                holder.itemView.setOnClickListener(v -> {
                    try {
                        showClockInDialog(holder,goal,goalId, position);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }
    

    //show a dialog to let the user clock in
    //click yes,progress bar would be affected
    //click no(in case the user clicked yes by mistake and wants to withdraw), cancel the record,progress bar gets updated
    //The name of the goal and day would be displayed in the dialog as well.
    private void showClockInDialog(GoalViewHolder holder, Goal goal, String goalId,int position ) throws ParseException {
        //update this everytime
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy", Locale.US);
        String currentDateStr= dateFormat.format(new Date());
        Log.d("currentDateStr",currentDateStr);
        try {
            sDate = dateFormat.parse(goal.getStartDate());
            Log.d("sDate: ", String.valueOf(sDate));
            eDate = dateFormat.parse(goal.getEndDate());
            Log.d("eDate: ", String.valueOf(eDate));
            currentDate = dateFormat.parse(currentDateStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        // Inflate dialog view using finish_goal_dialog.xml
        View dialogView = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.finish_goal_dialog, null);
        //build a dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
        builder.setView(dialogView);
        dialog = builder.create();
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
        int diffInDaysFromStartToNow = (int) TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) + 1;
            //start from past/today and end in today/future
            goalNameAndDay.setText( goal.getGoalName() +" "+" "+ diffInDaysFromStartToNow + "/" + diffInDaysFromStartToEnd +" day" );
            Log.d("text: ",goal.getGoalName() +" "+" "+ diffInDaysFromStartToNow + "/" + diffInDaysFromStartToEnd);

            yesButton.setOnClickListener(v -> {
                //to save in the db whether the item view has been changed to green,isCheckedForToday = 1->checked
                    if (goal.getIsCheckedForToday() == 0 || (goal.getIsCheckedForToday() == 1 && !goal.getLastCheckedInDate().equals(currentDateStr))) {
                        isCheckedForToday = 1;
                        lastCheckedInDate = currentDateStr;
                        goalRef = FirebaseDatabase.getInstance().getReference("FinalProject").child("Goals").child(goalId);
                        //update this goal as checked in db
                        goalRef.child("isCheckedForToday").setValue(isCheckedForToday);
                        goalRef.child("lastCheckedInDate").setValue(lastCheckedInDate);
//                        holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
//                        holder.goalNameTextView.setPaintFlags(holder.goalNameTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    }
                dialog.dismiss();
            });
            noButton.setOnClickListener(v -> {
                    //to save in the db whether the item view has been changed to green,isCheckedForToday = 1->checked
                    if (goal.getIsCheckedForToday() == 1 && goal.getLastCheckedInDate().equals(currentDateStr)) {
                        isCheckedForToday = 0;
                        lastCheckedInDate = "";
                        goalRef = FirebaseDatabase.getInstance().getReference("FinalProject").child("Goals").child(goalId);
                        //update this goal as checked in db
                        goalRef.child("isCheckedForToday").setValue(isCheckedForToday);
                        goalRef.child("lastCheckedInDate").setValue(lastCheckedInDate);
//                        holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.light_pink));
//                        holder.goalNameTextView.setPaintFlags(holder.goalNameTextView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);

                    }
                dialog.dismiss();
            });

    }

    @NonNull
    @Override
    public GoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.goal_item, parent, false);
        return new GoalViewHolder(view);
    }
    private boolean hasExpired(Goal goal) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy", Locale.US);
        Date eDate = dateFormat.parse(goal.getEndDate());
        String currentDateStr = dateFormat.format(new Date());
        Date currentDate = dateFormat.parse(currentDateStr);
        return eDate.compareTo(currentDate)< 0;
    }

    private boolean isNotStarted(Goal goal) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy", Locale.US);
        Date sDate = dateFormat.parse(goal.getStartDate());
        String currentDateStr = dateFormat.format(new Date());
        Date currentDate = dateFormat.parse(currentDateStr);
        return sDate.compareTo(currentDate)> 0;
    }


}
