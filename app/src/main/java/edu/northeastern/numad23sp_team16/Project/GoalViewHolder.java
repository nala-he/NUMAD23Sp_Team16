package edu.northeastern.numad23sp_team16.Project;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

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

    public void bind(Goal goal) {
        goalNameTextView.setText(goal.getGoalName());
        iconImageView.setImageResource(goal.getIcon());
//        priorityImageView.setImageResource(goal.getPriority());//1,2,3
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
}

