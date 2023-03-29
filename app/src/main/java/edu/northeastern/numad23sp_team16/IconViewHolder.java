package edu.northeastern.numad23sp_team16;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class IconViewHolder extends RecyclerView.ViewHolder {

    ImageView icon;
    TextView iconName;
    ImageView checkmark;

    public IconViewHolder(@NonNull View itemView) {
        super(itemView);

        // Get references to views
        icon = itemView.findViewById(R.id.icon_image);
        iconName = itemView.findViewById(R.id.icon_name);
        checkmark = itemView.findViewById(R.id.checkmark);
    }
}
