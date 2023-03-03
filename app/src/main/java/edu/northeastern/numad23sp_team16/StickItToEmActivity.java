package edu.northeastern.numad23sp_team16;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StickItToEmActivity extends AppCompatActivity {

    private final String CURRENT_USER = "CURRENT_USER";
    private String currentUser;
    private TextView currentlyLoggedIn;
    //text to remind user to tap
    private TextView textView;
    private RecyclerView recyclerView;
    private ArrayList<Sticker> stickerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stick_it_to_em);
        recyclerView = findViewById(R.id.recyclerView);
        currentlyLoggedIn = findViewById(R.id.currentUserTitle);
        textView = findViewById(R.id.textView);

        // Retrieve currently logged in user
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentUser = extras.getString(CURRENT_USER);
        }


            // Set title for currently logged in user
            // currentlyLoggedIn = findViewById(R.id.currentUserTitle);
            currentlyLoggedIn.setText("Currently Logged In: " + currentUser);

            //display 4 stickers in recyclerview for user to send
            stickerList = new ArrayList<>();
            //initialize stickerList with four stickers
            stickerList.add(new Sticker(R.drawable.giraffe));
            stickerList.add(new Sticker(R.drawable.lion));
            stickerList.add(new Sticker(R.drawable.gorilla));
            stickerList.add(new Sticker(R.drawable.hedgehog));
            //set the recycler view for stickers
            //recyclerView = findViewById(R.id.recyclerView);
            recyclerView.setAdapter(new StickerAdapter(stickerList));
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

        tapSticker();

    }

    private void tapSticker() {
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                // Get the position of the tapped item
                int position = rv.getChildAdapterPosition(rv.findChildViewUnder(e.getX(), e.getY()));

                // Check if the tapped item is an image item
                //&& StickerAdapter.getItemViewType(position) == IMAGE_VIEW_TYPE
                if (position != RecyclerView.NO_POSITION ) {
                    // Show the popup dialog with the list of users

                    AlertDialog.Builder builder = new AlertDialog.Builder(StickItToEmActivity.this);
                    builder.setTitle("Select a recipient");
                    final String[] users = {"Yuan", "Yutong", "Macee"}; // replace with your user list
                    builder.setItems(users, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Handle the user selection here
                            String selectedUser = users[which];
                            // Send the image to the selected user using Firebase
                            //TODO
                            sendImageToFirebase(selectedUser, StickerAdapter.getItem(position));
                        }
                    });
                    builder.show();
                    return true; // consume the event to prevent further actions
                }
                return false; // continue with other event handlers
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                // Not needed for this implementation
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
                // Not needed for this implementation
            }
        });

    }



    //save recyclerview state
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("recyclerViewState", recyclerView.getLayoutManager().onSaveInstanceState());
        String currentUser = currentlyLoggedIn.getText().toString();
        outState.putString("currentUser", currentUser);
        String text = textView.getText().toString();
        outState.putString("remind",text);
    }
    //TODO: why doesn't the state of textView get restored?? how to change??
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            Parcelable recyclerViewState = savedInstanceState.getParcelable("recyclerViewState");
            recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
            //restore username
            String currentUser = savedInstanceState.getString("currentUser");
            currentlyLoggedIn.setText(currentUser);
            //restore reminding text
            String text =savedInstanceState.getString("remind");
            textView.setText(text);
        }
    }
    //TODO:send image to firebase,then download to recipient
    private void sendImageToFirebase(String recipient, Sticker sticker) {

    }
    



}