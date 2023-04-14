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
//        HashMap<String, Integer> iconMap = new HashMap<>();
//        iconMap.put("Self-Care", R.drawable.icon_self_care);
//        iconMap.put("Social", R.drawable.icon_social);
//        iconMap.put("Productivity", R.drawable.icon_productivity);
//        iconMap.put("Health", R.drawable.icon_health);
//        iconMap.put("Hydration", R.drawable.icon_hydration);
//        iconMap.put("Language", R.drawable.icon_language);
//        iconMap.put("Growth", R.drawable.icon_growth);
//        iconMap.put("Finances", R.drawable.icon_finances);
//        iconMap.put("Reading", R.drawable.icon_reading);
//        //get the int id of icons
//        iconImageView.setImageResource(iconMap.get(goal.getIcon()));
        switch (goal.getIcon()){
            case "Self-Care" :
                iconImageView.setImageResource(R.drawable.icon_self_care);
                break;
            case "Social" :
                iconImageView.setImageResource(R.drawable.icon_social);
                break;
            case "Productivity" :
                iconImageView.setImageResource(R.drawable.icon_productivity);
                break;
            case "Health" :
                iconImageView.setImageResource(R.drawable.icon_health);
                break;
            case "Hydration" :
                iconImageView.setImageResource(R.drawable.icon_hydration);
                break;
            case "Language" :
                iconImageView.setImageResource(R.drawable.icon_language);
                break;
            case "Growth" :
                iconImageView.setImageResource(R.drawable.icon_growth);
                break;
            case "Finances" :
                iconImageView.setImageResource(R.drawable.icon_finances);
                break;
            case "Reading" :
                iconImageView.setImageResource(R.drawable.icon_reading);
                break;

        }
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

