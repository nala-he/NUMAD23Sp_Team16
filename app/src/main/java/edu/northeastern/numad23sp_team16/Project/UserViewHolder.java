package edu.northeastern.numad23sp_team16.Project;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;

import edu.northeastern.numad23sp_team16.R;

public class UserViewHolder extends RecyclerView.ViewHolder {
    public TextView name;
    public UserViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.username_text);
    }


}
