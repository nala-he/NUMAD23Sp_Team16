package edu.northeastern.numad23sp_team16.A8;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.numad23sp_team16.R;
import edu.northeastern.numad23sp_team16.A8.models.Message;

public class ReceivedStickerAdapter extends RecyclerView.Adapter<ReceivedStickerViewHolder> {

    private Context context;
    private List<Message> messageList;

    public ReceivedStickerAdapter(Context context, List<Message> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public ReceivedStickerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create view holder to display received stickers
        return new ReceivedStickerViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_received_sticker_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReceivedStickerViewHolder holder, int position) {
        // Get the message from list
        Message message = messageList.get(position);

        // Bind the data to view holder
        // Set sender name
        holder.username.setText(message.senderName);

        // Set date
        holder.date.setText(message.timeStamp);

        // Set sticker image
        Bitmap image = BitmapFactory.decodeResource(context.getResources(),
                Integer.parseInt(message.stickerId));
        holder.sticker.setImageBitmap(image);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}
