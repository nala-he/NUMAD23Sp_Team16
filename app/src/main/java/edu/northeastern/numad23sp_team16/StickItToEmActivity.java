package edu.northeastern.numad23sp_team16;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import edu.northeastern.numad23sp_team16.models.Message;
import edu.northeastern.numad23sp_team16.models.User;

public class StickItToEmActivity extends AppCompatActivity {

//    private static final String CHANNEL_ID = "ch";
    private final String CURRENT_USER = "CURRENT_USER";
    private final String RECEIVER = "RECEIVER";
    private final String STICKER = "STICKER";

    private String currentUser;
    private String recipient;
    private int stickerId;
    private TextView currentlyLoggedIn;
    //text to remind user to tap
    private TextView textView;
    private RecyclerView recyclerView;
    private ArrayList<Sticker> stickerList;
    private List<String> userList;

    // We will retrieve the signup user list from the database instead of hardcoding
    //    public final String[] users =  {"Yuan", "Yutong", "Macee"};
//    FirebaseStorage storage;

    public DatabaseReference mDatabase;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stick_it_to_em);
        recyclerView = findViewById(R.id.recyclerView);
        currentlyLoggedIn = findViewById(R.id.currentUserTitle);
        textView = findViewById(R.id.textView);
//        storage = FirebaseStorage.getInstance();


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

        // Connect with firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // initialize an empty userList to store our signup users from realtime database
        userList = new ArrayList<>();

        mDatabase.child("users")
                .addChildEventListener(
                        new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                User user = dataSnapshot.getValue(User.class);
                                if (user != null && !userList.contains(user.username)) {
                                    userList.add(user.username);
                                }
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                User user = dataSnapshot.getValue(User.class);
                                if (user != null && !userList.contains(user.username)) {
                                    userList.add(user.username);
                                }
                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {
                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(getApplicationContext()
                                        , "DBError: " + databaseError, Toast.LENGTH_SHORT).show();
                            }
                        }
                );


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

                    String[] users = new String[userList.size()];
                    for (int i = 0; i < userList.size(); i++) {
                        users[i] = userList.get(i);
                    }

                    //the code inside the lambda expression will be executed when the user selects an item from the list in the dialog
                    builder.setItems(users, (dialog, which) -> {
                        // which user is selected
                        recipient = users[which];
                        stickerId = stickerList.get(position).getStickerId();

                        // Continue to RealtimeDatabaseActivity - passing currently logged in user, recipient, and
                        // selected sticker id to send message to the database and send notification
                        Intent intent = new Intent(StickItToEmActivity.this, RealtimeDatabaseActivity.class);
                        intent.putExtra(CURRENT_USER, currentUser);
                        intent.putExtra(RECEIVER, recipient);
                        intent.putExtra(STICKER, stickerId);
                        startActivity(intent);
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