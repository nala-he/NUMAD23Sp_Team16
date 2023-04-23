package edu.northeastern.numad23sp_team16.A8;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.northeastern.numad23sp_team16.R;

public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.StickerViewHolder> {

   private static ArrayList<Sticker> stickerList;

   public StickerAdapter(ArrayList<Sticker> stickerList){
       this.stickerList = stickerList;

   }

    public static Sticker getItem(int position) {
       return stickerList.get(position);
    }

    public static class StickerViewHolder extends RecyclerView.ViewHolder{
       ImageView imageView;

       public StickerViewHolder(View itemView){
           super(itemView);
           imageView = itemView.findViewById(R.id.sticker_view);
       }
   }
        @NonNull
        @Override
        public StickerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sticker_view,parent,false);
            return new StickerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull StickerAdapter.StickerViewHolder holder, int position) {
            Sticker sticker = stickerList.get(position);
            //bind each sticker with its view holder
            holder.imageView.setImageResource(sticker.getStickerId());
        }

        @Override
        public int getItemCount() {
            return stickerList.size();
        }

}
