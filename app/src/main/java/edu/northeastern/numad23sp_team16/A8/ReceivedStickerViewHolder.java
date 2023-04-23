package edu.northeastern.numad23sp_team16.A8;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.northeastern.numad23sp_team16.R;

public class ReceivedStickerViewHolder extends RecyclerView.ViewHolder{

    ImageView sticker;
    TextView username;
    TextView date;

    public ReceivedStickerViewHolder(@NonNull View itemView) {
        super(itemView);

        // Store references to sticker image, username, and date
        this.sticker = itemView.findViewById(R.id.sticker_image);
        this.username = itemView.findViewById(R.id.sender_name);
        this.date = itemView.findViewById(R.id.sent_on);
    }
}
