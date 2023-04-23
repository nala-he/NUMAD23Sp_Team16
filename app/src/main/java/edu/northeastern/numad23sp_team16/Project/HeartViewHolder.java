package edu.northeastern.numad23sp_team16.Project;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.northeastern.numad23sp_team16.R;

public class HeartViewHolder extends RecyclerView.ViewHolder {

    ImageView heart;

    public HeartViewHolder(@NonNull View itemView) {
        super(itemView);

        // Get reference to view
        heart = itemView.findViewById(R.id.grid_heart);
    }
}
