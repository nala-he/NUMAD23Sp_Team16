package edu.northeastern.numad23sp_team16;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StickerCountAdapter extends RecyclerView.Adapter<StickerCountViewHolder> {
    private final ArrayList<Sticker> stickerCountList;

    public StickerCountAdapter(ArrayList<Sticker> stickerCountList) {
        this.stickerCountList = stickerCountList;
    }

    @Override
    public StickerCountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sticker_count_listitem, parent, false);
        return new StickerCountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StickerCountViewHolder holder, int position) {
        holder.bindThisData(stickerCountList.get(position));
    }

    @Override
    public int getItemCount() {
        return stickerCountList.size();
    }

}
