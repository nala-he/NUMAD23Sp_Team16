package edu.northeastern.numad23sp_team16;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StickItToEmActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "ch";
    private final String CURRENT_USER = "CURRENT_USER";
    private String currentUser;
    private TextView currentlyLoggedIn;
    //text to remind user to tap
    private TextView textView;
    private RecyclerView recyclerView;
    private ArrayList<Sticker> stickerList;
    public final String[] users =  {"Yuan", "Yutong", "Macee"};

    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stick_it_to_em);
        recyclerView = findViewById(R.id.recyclerView);
        currentlyLoggedIn = findViewById(R.id.currentUserTitle);
        textView = findViewById(R.id.textView);
        storage = FirebaseStorage.getInstance();


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
                // which position is tapped?
                int position = rv.getChildAdapterPosition(rv.findChildViewUnder(e.getX(), e.getY()));
                // Check if the tapped item is an image item
                if (position != RecyclerView.NO_POSITION) {
                    // Show the popup dialog with the list of users
                    AlertDialog.Builder builder = new AlertDialog.Builder(StickItToEmActivity.this);
                    builder.setTitle("Select a recipient to send this sticker:");
                    //the code inside the lambda expression will be executed when the user selects an item from the list in the dialog
                    builder.setItems(users, (dialog, which) -> {
                        // which user is selected
                        String recipient = users[which];
                        // send the image to the selected user using Firebase
                        //TODO:replace the following method with database logic,recipient is the name(String),stickerList.get(position) is the sticker,eg.new Sticker(R.drawable.giraffe),currentUser is the sender
                        //sendImageToFirebase(recipient, stickerList.get(position));
                    });
                    builder.show();
                    return true;
                }
                return false;
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
        outState.putString("remind", text);
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
            String text = savedInstanceState.getString("remind");
            textView.setText(text);
        }
    }
    /**
    //send image to firebase storage,then save the downloadURL, add this url to an intent when the image is sent to recipient for display
    //TODO:recipient receives the stickerUrl, display this in an imageView
    private void sendImageToFirebase(String recipient, Sticker image) {
        Drawable drawable = ContextCompat.getDrawable(this, image.getStickerId());
        //convert the drawable to a Bitmap and compress it to a byte array
        assert drawable != null;
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageData = baos.toByteArray();
        //upload the byte array to Firebase storage using the putBytes()
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("images/"+image.getStickerId());
        UploadTask uploadTask = imageRef.putBytes(imageData);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Get the download URL of the file
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String stickerUrl = uri.toString();
                // use download URL to send a notification to user and start a new activity in the receiver end to display the image
                //TODO:I created ReceiveStickerActivity for the user to receive the image. This activity can be changed according to your design
                //Intent intent = new Intent(this, ReceiveStickerActivity.class);
               //intent.putExtra("stickerUrl", stickerUrl);
              //intent.putExtra("recipient", recipient);
                //TODO: how to send this intent to the target user?
            });
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // File upload failed
            }
        });
    }
**/



}