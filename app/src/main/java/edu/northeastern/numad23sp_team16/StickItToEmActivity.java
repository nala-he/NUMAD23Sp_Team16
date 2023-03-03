package edu.northeastern.numad23sp_team16;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class StickItToEmActivity extends AppCompatActivity {

    private final String CURRENT_USER = "CURRENT_USER";
    private String currentUser;
    private TextView currentlyLoggedIn;
    private RecyclerView recyclerView;
    private ArrayList<Sticker> stickerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stick_it_to_em);

        // Retrieve currently logged in user
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentUser = extras.getString(CURRENT_USER);
        }

        // Set title for currently logged in user
        currentlyLoggedIn = findViewById(R.id.currentUserTitle);
        currentlyLoggedIn.setText("Currently Logged In: " + currentUser);

        //display 4 stickers in recyclerview for user to send
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