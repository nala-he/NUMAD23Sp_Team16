package edu.northeastern.numad23sp_team16;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class StickerActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    //StickerAdapter stickerAdapter;
    ArrayList<Sticker> stickerList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker);
        stickerList = new ArrayList<>();
        //initialize stickerList with four stickers
        stickerList.add(new Sticker(R.drawable.giraffe));
        stickerList.add(new Sticker(R.drawable.lion));
        stickerList.add(new Sticker(R.drawable.gorilla));
        stickerList.add(new Sticker(R.drawable.hedgehog));
        //set the recycler view for stickers
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(new StickerAdapter(stickerList));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}