package edu.northeastern.numad23sp_team16.Project;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import edu.northeastern.numad23sp_team16.R;
import edu.northeastern.numad23sp_team16.models.Goal;


public class GoalViewHolder extends RecyclerView.ViewHolder {
    public TextView goalNameTextView;
    public ImageView iconImageView;
    public ImageView priorityImageView;


    public GoalViewHolder(View itemView) {
        super(itemView);
        goalNameTextView = itemView.findViewById(R.id.goal_textview);
        iconImageView = itemView.findViewById(R.id.left_image_view);
        priorityImageView = itemView.findViewById(R.id.right_image_view);
    }

    public void bind(Goal goal) throws ParseException {
        //bind goal name textview
        if(isNotStarted(goal)){
            goalNameTextView.setText(goal.getGoalName() + ": (start soon)");
        }else if(hasExpired(goal)){
            goalNameTextView.setText(goal.getGoalName() + ": (end date passed)");
        }else{
            goalNameTextView.setText(goal.getGoalName());
        }

        //bind icon imageview
        switch (goal.getIcon()){
            case "icon_self_care" :
                iconImageView.setImageResource(R.drawable.icon_self_care);
                break;
            case "icon_social" :
                iconImageView.setImageResource(R.drawable.icon_social);
                break;
            case "icon_productivity" :
                iconImageView.setImageResource(R.drawable.icon_productivity);
                break;
            case "icon_health" :
                iconImageView.setImageResource(R.drawable.icon_health);
                break;
            case "icon_hydration" :
                iconImageView.setImageResource(R.drawable.icon_hydration);
                break;
            case "icon_language" :
                iconImageView.setImageResource(R.drawable.icon_language);
                break;
            case "icon_growth" :
                iconImageView.setImageResource(R.drawable.icon_growth);
                break;
            case "icon_finances" :
                iconImageView.setImageResource(R.drawable.icon_finances);
                break;
            case "icon_reading" :
                iconImageView.setImageResource(R.drawable.icon_reading);
                break;
            default:
                priorityImageView.setImageResource(R.drawable.flag);
                break;

        }
        //bind priority imageview
        switch (goal.getPriority()) {
            case 1:
                priorityImageView.setImageResource(R.drawable.checked_low_priority);
                break;
            case 2:
                priorityImageView.setImageResource(R.drawable.checked_medium_priority);
                break;
            case 3:
                priorityImageView.setImageResource(R.drawable.checked_high_priority);
                break;
            default:
                priorityImageView.setImageResource(R.drawable.flag);
                break;
        }



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
        Date eDate = dateFormat.parse(goal.getEndDate());
        String currentDateStr = dateFormat.format(new Date());
        Date currentDate = dateFormat.parse(currentDateStr);
        return sDate.compareTo(currentDate)> 0;
    }
}

