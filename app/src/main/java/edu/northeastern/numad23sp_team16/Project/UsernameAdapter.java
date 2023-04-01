package edu.northeastern.numad23sp_team16.Project;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import edu.northeastern.numad23sp_team16.R;

import java.util.List;

public class UsernameAdapter extends RecyclerView.Adapter<UsernameViewHolder> {
    private List<Username> usernameList;

    public UsernameAdapter(List<Username> usernameList) {
        this.usernameList = usernameList;
    }

    @Override
    public UsernameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_username_view, parent, false);
        return new UsernameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UsernameViewHolder holder, int position) {
        Username username = usernameList.get(position);
        holder.view.setBackgroundColor(username.isSelected() ? Color.parseColor("#FFECEC")
                : Color.parseColor("#F5F7FA"));
        holder.bindThisData(usernameList.get(position));
        holder.usernameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username.setSelected(!username.isSelected());
                holder.view.setBackgroundColor(username.isSelected() ? Color.parseColor("#FFECEC")
                        : Color.parseColor("#F5F7FA"));
            }
        });
    }

    @Override
    public int getItemCount() {
        return usernameList == null ? 0 : usernameList.size();
    }

}
