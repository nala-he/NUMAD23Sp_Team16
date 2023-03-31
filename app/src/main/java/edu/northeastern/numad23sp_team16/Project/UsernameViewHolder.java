package edu.northeastern.numad23sp_team16.Project;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.northeastern.numad23sp_team16.A8.Sticker;
import edu.northeastern.numad23sp_team16.R;

public class UsernameViewHolder extends RecyclerView.ViewHolder{
    public TextView usernameView;
    public View view;

    public UsernameViewHolder(@NonNull View itemView) {
        super(itemView);
        this.view = itemView;
        this.usernameView = itemView.findViewById(R.id.username_text);
    }

    @SuppressLint("SetTextI18n")
    public void bindThisData(Username theItemToBind) {
        this.usernameView.setText(theItemToBind.getName());
    }
}
