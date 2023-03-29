package edu.northeastern.numad23sp_team16;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.numad23sp_team16.models.Icon;

public class IconAdapter extends RecyclerView.Adapter<IconViewHolder> {

    Context context;
    List<Icon> icons;

    public IconAdapter(Context context, List<Icon> icons) {
        this.context = context;
        this.icons = icons;
    }

    @NonNull
    @Override
    public IconViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create view holder based on icon_view.xml
        return new IconViewHolder(LayoutInflater.from(context).inflate(R.layout.icon_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull IconViewHolder holder, int position) {
        // Bind specific icon to views in view holder
        holder.iconName.setText(icons.get(position).getIconName());
        holder.icon.setImageResource(icons.get(position).getIconId());
    }

    @Override
    public int getItemCount() {
        return icons.size();
    }
}
