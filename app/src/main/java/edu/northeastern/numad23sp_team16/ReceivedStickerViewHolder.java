package edu.northeastern.numad23sp_team16;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReceivedStickerViewHolder extends RecyclerView.ViewHolder{

    private ImageView sticker;
    private TextView username;
    private TextView date;

    public ReceivedStickerViewHolder(@NonNull View itemView) {
        super(itemView);

        // Store references to sticker image, username, and date
        this.sticker = itemView.findViewById(R.id.sticker_image);
        this.username = itemView.findViewById(R.id.sender_name);
        this.date = itemView.findViewById(R.id.sent_on);
    }
}
