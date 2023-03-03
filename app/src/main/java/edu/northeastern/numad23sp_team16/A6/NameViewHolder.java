package edu.northeastern.numad23sp_team16.A6;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.northeastern.numad23sp_team16.R;

public class NameViewHolder extends RecyclerView.ViewHolder {
    TextView name;

    public NameViewHolder(@NonNull View itemView) {
        // Call super class for constructor
        super(itemView);

        // Store references to translated name text view
        this.name = itemView.findViewById(R.id.translatedNameText);
    }

    // Pass data to be bound and put into text view
    public void bindThisData(Name nameToBind) {
        // Set the translated name to the name text view of the view holder
        this.name.setText(nameToBind.getName());
    }
}
