package com.ebookfrenzy.numad23sp_team16;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NameAdapter extends RecyclerView.Adapter<NameViewHolder> {

    // Reference to list of translated names
    private final List<Name> names;

    // Stored internal context
    private final Context context;

    public NameAdapter(List<Name> names, Context context) {
        this.names = names;
        this.context = context;
    }

    @NonNull
    @Override
    public NameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create view holder to hold the names in our list and pass it our item view
        return new NameViewHolder(LayoutInflater.from(context).inflate(R.layout.item_view, null));
    }

    @Override
    public void onBindViewHolder(@NonNull NameViewHolder holder, int position) {
        // Bind data when view holder is first used/recycled

        // Pass name object to view holder to bind translated name
        holder.bindThisData(names.get(position));
    }

    @Override
    public int getItemCount() {
        return names.size();
    }
}
