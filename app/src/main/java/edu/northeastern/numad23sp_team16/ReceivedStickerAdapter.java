package edu.northeastern.numad23sp_team16;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ReceivedStickerAdapter extends RecyclerView.Adapter<ReceivedStickerViewHolder> {

    private Context context;
    private ArrayList<Sticker> stickerList;

    public ReceivedStickerAdapter(Context context, ArrayList<Sticker> stickerList) {
        this.context = context;
        this.stickerList = stickerList;
    }

    @NonNull
    @Override
    public ReceivedStickerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ReceivedStickerViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return stickerList.size();
    }
}
