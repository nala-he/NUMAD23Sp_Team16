package edu.northeastern.numad23sp_team16;

import static android.view.View.VISIBLE;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.numad23sp_team16.models.Icon;

public class IconAdapter extends RecyclerView.Adapter<IconViewHolder> {

    Context context;
    List<Icon> icons;
    int selectedIcon = 0; // first icon selected on first load

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

        // Check if selected or not to show/hide checkmark
        if (selectedIcon == position) {
            // Show checkmark if item is selected
            holder.checkmark.setVisibility(VISIBLE);
        } else {
            // Don't show checkmark if item isn't selected
            holder.checkmark.setVisibility(View.GONE);
        }

        // Set onClick listener for when icons are chosen to update selected icon
        holder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.checkmark.setVisibility(VISIBLE);
                if (selectedIcon != holder.getAdapterPosition()) {
                    notifyItemChanged(selectedIcon);
                    selectedIcon = holder.getAdapterPosition();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return icons.size();
    }

    public Icon getSelectedIcon() {
        return icons.get(selectedIcon);
    }
}
