package edu.northeastern.numad23sp_team16.Project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.northeastern.numad23sp_team16.R;
import edu.northeastern.numad23sp_team16.models.Friend;
import edu.northeastern.numad23sp_team16.models.User;

public class AddFriendsActivity extends AppCompatActivity {

    private RecyclerView userListRecyclerView;
    private UsernameAdapter usernameAdapter;
    private ArrayList<Username> usersList = new ArrayList<>();
    private List<String> newFriendIdsList = new ArrayList<>();
    private List<String> preFriendIdsList = new ArrayList<>();

    private final String FRIENDS_LIST = "FRIENDS_LIST";
    private final String CURRENT_USER = "CURRENT_USER";
    private String currentUser;
    private DatabaseReference usersRef;
    private DatabaseReference friendsRef;
    private static int friendIdCounter = 1;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_status);

        // customize action bar back button and title
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar_layout);
        actionBar.setHomeAsUpIndicator(R.drawable.back_button);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#D9D9D9")));
        actionBar.setDisplayHomeAsUpEnabled(true);
        // change action bar title
        AppCompatTextView title = findViewById(R.id.navbar_title);
        title.setText("Add New Friends");

        // Retrieve currently logged in user -- Yutong
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentUser = extras.getString(CURRENT_USER);
        }


        // initialize usersRef and friendsRef from firebase database
        usersRef = FirebaseDatabase.getInstance().getReference("FinalProject").child("FinalProjectUsers");
        friendsRef = FirebaseDatabase.getInstance().getReference("FinalProject").child("FinalProjectFriends");

        if (savedInstanceState == null) {
            // get preFriendIdsList data from firebase
            getFriendIdsListData();

            // get usersList data and update the previously selected friends status
            getUsersListData();
        }

        // initialize views
        userListRecyclerView = findViewById(R.id.userlist_recyclerview);
        userListRecyclerView.setLayoutManager(new LinearLayoutManager(AddFriendsActivity.this));


        TextView userListTitle = findViewById(R.id.user_list_title);
        userListTitle.setText(R.string.list_of_users);

        Button addButton = findViewById(R.id.add_selected_friends_button);
        Button sendButton = findViewById(R.id.send_status_button);
        sendButton.setVisibility(View.INVISIBLE);
        addButton.setText(R.string.add_friends);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Username username : usersList) {
                    if (username.isSelected()) {
                        newFriendIdsList.add(username.getUserId());
                    }
                }
                if (newFriendIdsList.size() == 0) {
                    Toast.makeText(AddFriendsActivity.this, "Did not add any friends.",
                            Toast.LENGTH_LONG).show();
                } else {
                    // TODO: save new friend items from newFriendIdsList to the firebase database
                    for (String each : newFriendIdsList) {
                        Friend newFriend = new Friend(currentUser, each);
                        // add a child node to use the time as part of the unique friend id, format "friend16082271023"
                        String time = String.valueOf(System.currentTimeMillis()/1000);
                        String friendId = "friend" + time + friendIdCounter++;
                        friendsRef.child(friendId).setValue(newFriend);
                    }
                    Toast.makeText(AddFriendsActivity.this, "Saved selected users as friends.",
                            Toast.LENGTH_LONG).show();
                }

                Intent intent = new Intent(AddFriendsActivity.this, ShareActivity.class);
                // close all the activities in the call stack above ShareActivity and bring it to
                // the top of the call stack
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                // pass the friendsList to the ShareActivity, need to be replaced with data from firebase later
//                Bundle bundle = new Bundle();
//                bundle.putParcelableArrayList(FRIENDS_LIST, (ArrayList<? extends Parcelable>) friendsList);
//                intent.putExtras(bundle);

                // pass the current user id back to share activity
                intent.putExtra(CURRENT_USER, currentUser);
                startActivity(intent);
            }
        });
    }

    // this event will enable the back function to the back button on press in customized action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // obtain preFriendIdsList from firebase database
    private void getFriendIdsListData() {
        // Connect with firebase
        friendsRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            if (Objects.equals(data.child("currentUserId")
                                    .getValue(String.class), currentUser)) {
                                String friendId = Objects.requireNonNull(data.child("friendId")
                                        .getValue(String.class));
                                preFriendIdsList.add(friendId);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void getUsersListData() {
        // Connect with firebase
        usersRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            String userId = Objects.requireNonNull(data.getKey());
                            String userName = Objects.requireNonNull(data.getValue(User.class)).getUsername();
                            Username nameItem = new Username(userName, userId);
                            // check if the user is the currentUser
                            if (userId.equals(currentUser)) {
                                continue;
                            }
                            // check if the user is already a friend in the preFriendIdsList from database
                            if (preFriendIdsList != null
                                    && preFriendIdsList.stream().anyMatch(each -> each.equals(userId))) {
                                nameItem.setSelected(true);
                            }
                            if (!nameItem.isSelected() && usersList.stream()
                                    .noneMatch(each -> each.getName().equals(nameItem.getName()))) {
                                usersList.add(nameItem);
                            }
                        }
                        usernameAdapter = new UsernameAdapter(usersList);
                        userListRecyclerView.setAdapter(usernameAdapter);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // store usersList selected status
        outState.putParcelableArrayList("USERS_LIST", usersList);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // restore usersList selected status
        usersList = savedInstanceState.getParcelableArrayList("USERS_LIST");
        getFriendIdsListData();
        getUsersListData();
    }
}