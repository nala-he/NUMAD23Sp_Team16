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
        goalNameTextView.setText(goal.goalName);
        iconImageView.setImageResource(goal.icon);
        priorityImageView.setImageResource(goal.priority);//1,2,3

    }
}

