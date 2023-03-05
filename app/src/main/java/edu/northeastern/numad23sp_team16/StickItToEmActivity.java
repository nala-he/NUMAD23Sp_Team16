package edu.northeastern.numad23sp_team16;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Timestamp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import edu.northeastern.numad23sp_team16.models.Message;
import edu.northeastern.numad23sp_team16.models.User;

public class StickItToEmActivity extends AppCompatActivity {
    private static final String TAG = "StickItToEmActivity";

    private String channelId = "notification_channel_0";
    private int notificationId;

    private List<Message> receivedHistory;
    private ArrayList<Sticker> stickerCountList;
    private Map<String, Integer> sentStickersCount;

    // hardcoded for testing, needs to update later
    private static int messageId = 1;
    private final String CURRENT_USER = "CURRENT_USER";
//    private final String RECEIVER = "RECEIVER";
//    private final String STICKER = "STICKER";

    private String currentUser;
    private String recipient;
    private int stickerId;
    private TextView currentlyLoggedIn;
    //text to remind user to tap
    private TextView textView;
    private RecyclerView recyclerView;

    private RecyclerView stickerCountRecyclerView;

    private StickerCountAdapter stickerCountAdapter;
    private ArrayList<Sticker> stickerList;
    private List<String> userList;

    // We will retrieve the signup user list from the database instead of hardcoding
    //    public final String[] users =  {"Yuan", "Yutong", "Macee"};
//    FirebaseStorage storage;

    public DatabaseReference mDatabase;

    // keep track of when user logged in
    private Timestamp loginTime;
    // show dialog to choose a recipient
    Dialog dialog;
    //position in recyclerview to represent which sticker is tapped
    int position;
    //users in firebase
    String[] users;
    boolean isDialogOpen;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stick_it_to_em);
        recyclerView = findViewById(R.id.recyclerView);
        currentlyLoggedIn = findViewById(R.id.currentUserTitle);
        textView = findViewById(R.id.textView);

        // Login time
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date date = new Date();
        loginTime = new Timestamp(date.getTime());

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

        // set the recycler view for sent sticker counts history
        stickerCountRecyclerView = findViewById(R.id.sticker_count_list);
        stickerCountList = new ArrayList<>();
        stickerCountAdapter = new StickerCountAdapter(stickerCountList);
        stickerCountRecyclerView.setAdapter(stickerCountAdapter);
        stickerCountRecyclerView.setLayoutManager(new LinearLayoutManager(StickItToEmActivity.this));

        // Connect with firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // initialize an empty userList to store our signup users from realtime database
        userList = new ArrayList<>();

        notificationId = 0;
        receivedHistory = new ArrayList<>();
        sentStickersCount = new HashMap<>();

        createNotificationChannel();

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

        // Update the sticker in realtime
        mDatabase.child("messages")
                .addChildEventListener(
                        new ChildEventListener() {

                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                                showSticker(dataSnapshot);
                                getStickerCountAndHistory(dataSnapshot);

//                                Message message = dataSnapshot.getValue(Message.class);
//
//                                if (message != null
//                                        && Objects.equals(message.receiverName, loggedInUser)) {
//                                    sendNotification(message.senderName, message.stickerId);
//                                }
                                Log.e(TAG, "onChildAdded: dataSnapshot = " + dataSnapshot.getValue().toString());
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                                showSticker(dataSnapshot);
//                                Message message = dataSnapshot.getValue(Message.class);
//                                if (message != null
//                                        && Objects.equals(message.receiverName, loggedInUser)) {
//                                    sendNotification(message.senderName, message.stickerId);
//                                }
//                                Log.v(TAG, "onChildChanged: " + dataSnapshot.getValue().toString());
                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(TAG, "onCancelled:" + databaseError);
                                Toast.makeText(getApplicationContext()
                                        , "DBError: " + databaseError, Toast.LENGTH_SHORT).show();
                            }
                        }
                );

        tapSticker();

//        // initialize the two buttons for the history lists
//        Button countButton = (Button) findViewById(R.id.show_sticker_count_button);
//        Button historyButton = (Button) findViewById(R.id.show_history_button);
//        countButton.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                showStickerCount();
//            }
//        });
//        historyButton.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                showStickerHistory();
//            }
//        });
    }

    private void onSendSticker(DatabaseReference postRef,
                               String receiver, String sender, Integer sticker) {

        // add the time as part of the message id to avoid new message overwriting the previous message
        // with the same id
        String time = String.valueOf(System.currentTimeMillis()/1000);
        postRef.child("messages")
                .child("message" + time + messageId++)
                .setValue(new Message(receiver, sender, String.valueOf(sticker)));
        postRef
                .child("messages")
                .child("message" + time + messageId)
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {

                        Message message = mutableData.getValue(Message.class);

                        if (receiver == null || message == null) {
                            return Transaction.success(mutableData);
                        }

                        if (message.receiverName.equals(receiver)) {
                            message.stickerId = String.valueOf(sticker);
                            mutableData.setValue(message);
                        }

                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b,
                                           DataSnapshot dataSnapshot) {
                        Toast.makeText(getApplicationContext(), "Sticker sent to " + receiver,
                                Toast.LENGTH_LONG).show();
                        // Transaction completed
                        Log.d(TAG, "postTransaction:onComplete:" + databaseError);
                    }
                });
    }

    private void tapSticker() {
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                // which position is tapped?
                position = rv.getChildAdapterPosition(rv.findChildViewUnder(e.getX(), e.getY()));
                // Check if the tapped item is an image item
                if (position != RecyclerView.NO_POSITION) {
                    // Show the popup dialog with the list of users
                    showDialog();
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
    private void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(StickItToEmActivity.this);
        builder.setTitle("Select a recipient to send this sticker:");

        users = new String[userList.size()];
        for (int i = 0; i < userList.size(); i++) {
            users[i] = userList.get(i);
        }

        //the code inside the lambda expression will be executed when the user selects an item from the list in the dialog
        builder.setItems(users, (dialog, which) -> {
            // which user is selected
            recipient = users[which];
            stickerId = stickerList.get(position).getStickerId();

            // Send the new message containing sticker sending info to the Realtime Database
            onSendSticker(mDatabase, recipient, currentUser, stickerId);
        });
        // Save the state of the dialog
        isDialogOpen = true;
        //save the state into a bundle
        Bundle bundle = new Bundle();
        bundle.putStringArray("users",users);
        dialog = builder.create();
        dialog.setOnDismissListener(dialog -> isDialogOpen = false);
        dialog.show();




    }
    // moved from yutong's RealtimeDatabaseActivity.java
    private void getStickerCountAndHistory(DataSnapshot dataSnapshot) {
        Message message = dataSnapshot.getValue(Message.class);

//        stickerCountAdapter.notifyDataSetChanged();

        if (message != null) {
            // Convert message time to timestamp
            Timestamp messageTime = Timestamp.valueOf(message.timeStamp);

            // add sticker count to sentStickersCount map
            if (Objects.equals(message.senderName, currentUser)) {
                if (sentStickersCount.containsKey(message.stickerId)) {
                    Integer count = sentStickersCount.get(message.stickerId);
                    count += 1;
                    sentStickersCount.put(message.stickerId, count);
                } else {
                    sentStickersCount.put(message.stickerId, 1);
//                    stickerCountList.add(new Sticker(Integer.parseInt(message.stickerId), 1));
                }
            }

            Log.e(TAG, "sentStickersCount:" + sentStickersCount.toString());
            showStickerCount();

            if (Objects.equals(message.receiverName, currentUser) && messageTime.after(loginTime)) {

                // send notification to the specific receiver
                sendNotification(message.senderName, message.stickerId);

                // add the matched message to the history list
                receivedHistory.add(message);
            }

            Log.e(TAG, "receivedHistory:" + receivedHistory.toString());
        }
    }

    public void showStickerCount() {
        //TODO: Display how many of each kind of sticker a user sent
        stickerCountAdapter.notifyDataSetChanged();
        // clean the data of the sticker count list before adding new record
        stickerCountList.clear();
        sentStickersCount.forEach((id, num) -> stickerCountList.add(new Sticker(Integer.parseInt(id), num)));
    }

    public void showStickerHistory() {
        // TODO: Display history of stickers user has received (which sticker received, who sent it,
        //  when it was sent)
    }

    public void createNotificationChannel() {
        // This must be called early because it must be called before a notification is sent.
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notification Name";
            String description = "Notification Channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);

            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void sendNotification(String sender, String stickerId) {

        // Build notification
        // Need to define a channel ID after Android Oreo
        Bitmap myBitmap = BitmapFactory.decodeResource(getResources(), Integer.parseInt(stickerId));
        NotificationCompat.Builder notifyBuild = new NotificationCompat.Builder(this, channelId)
                //"Notification icons must be entirely white."
                .setSmallIcon(R.drawable.foo)
                .setContentTitle("You received a sticker from " + sender)
//                .setContentText("Subject")
                .setLargeIcon(myBitmap)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(myBitmap)
                        .bigLargeIcon(null))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                // hide the notification after its selected
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            // calling ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS}, 0);
        }

        notificationManager.notify(notificationId++, notifyBuild.build());

        // if only want to let the notification panel show the latest one notification, use this below
//        notificationManager.notify(notificationId, notifyBuild.build());

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            Log.v(TAG, "The user gave access.");
            Toast.makeText(this, "The user gave permission.", Toast.LENGTH_SHORT).show();

        } else {
            Log.e(TAG, "User denied permission.");
            // permission denied
            Toast.makeText(this, "The user denied permission.", Toast.LENGTH_SHORT).show();
        }
    }

    //save recyclerview state
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("recyclerViewState", recyclerView.getLayoutManager().onSaveInstanceState());
        outState.putInt("positionOfSticker",position);
        outState.putString("currentUser", currentUser);
        String text = textView.getText().toString();
        outState.putString("remind", text);
        // Save the state of the dialog if it is currently shown
        if (dialog != null && isDialogOpen) {
            outState.putBoolean("isDialogOpen", true);
            outState.putStringArray("users",users);
            dialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Dismiss the dialog if it is currently shown
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    //restore the state
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            Parcelable recyclerViewState = savedInstanceState.getParcelable("recyclerViewState");
            recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
            position = savedInstanceState.getInt("positionOfSticker");
            //restore username
            //String currentUser = savedInstanceState.getString("currentUser");
            currentlyLoggedIn.setText("Currently Logged In: "+ savedInstanceState.getString("currentUser"));
            //restore reminding text
            textView.setText(savedInstanceState.getString("remind"));
             //Restore the state of the dialog if it was previously shown
            users = savedInstanceState.getStringArray("users");
            if (savedInstanceState.getBoolean("isDialogOpen", false)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(StickItToEmActivity.this);
                builder.setTitle("Select a recipient to send this sticker:");

                users = savedInstanceState.getStringArray("users");
                for (int i = 0; i < userList.size(); i++) {
                    users[i] = userList.get(i);
                }

                //the code inside the lambda expression will be executed when the user selects an item from the list in the dialog
                builder.setItems(users, (dialog, which) -> {
                    // which user is selected
                    recipient = users[which];
                    stickerId = stickerList.get(position).getStickerId();

                    // Send the new message containing sticker sending info to the Realtime Database
                    onSendSticker(mDatabase, recipient, currentUser, stickerId);
                });
                dialog = builder.create();
                dialog.show();
            }


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