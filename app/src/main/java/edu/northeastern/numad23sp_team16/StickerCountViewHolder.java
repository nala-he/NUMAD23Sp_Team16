package edu.northeastern.numad23sp_team16;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StickerCountViewHolder extends RecyclerView.ViewHolder {
    public ImageView sticker;
    public TextView count;

    public StickerCountViewHolder(@NonNull View itemView) {
        super(itemView);
        this.sticker = itemView.findViewById(R.id.sent_sticker);
        this.count = itemView.findViewById(R.id.count);
    }

    @SuppressLint("SetTextI18n")
    public void bindThisData(Sticker theItemToBind) {
        this.sticker.setImageResource(theItemToBind.getStickerId());
        this.count.setText(theItemToBind.getStickerCount() + "");
    }
}
