package edu.northeastern.numad23sp_team16.Project;

import static android.view.View.VISIBLE;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.numad23sp_team16.R;

public class HeartAdapter extends RecyclerView.Adapter<HeartViewHolder> {

    Context context;
    List<Integer> hearts;
    int numberOfHearts;

    public HeartAdapter(Context context, List<Integer> hearts, int numberOfHearts) {
        this.context = context;
        this.hearts = hearts;
        this.numberOfHearts = numberOfHearts;
    }

    @NonNull
    @Override
    public HeartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create view holder based on heart_item.xml
        return new HeartViewHolder(LayoutInflater.from(context).inflate(R.layout.heart_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HeartViewHolder holder, int position) {
        // Bind heart icon to views in view holder
        holder.heart.setImageResource(hearts.get(position));

        // Check whether to show heart (depends on overall pet health)
        if (position >= numberOfHearts) {
            // Don't show heart if health is lower than position in list
            holder.heart.setVisibility(View.GONE);
        } else {
            // Show heart if health is good enough
            holder.heart.setVisibility(VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return hearts.size();
    }

    public void setNumberOfHearts(int number) {
        this.numberOfHearts = number;
    }
}
