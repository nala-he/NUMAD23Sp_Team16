package com.ebookfrenzy.numad23sp_team16;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
