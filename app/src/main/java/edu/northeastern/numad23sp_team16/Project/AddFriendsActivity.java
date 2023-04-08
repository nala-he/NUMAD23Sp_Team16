package edu.northeastern.numad23sp_team16.Project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.northeastern.numad23sp_team16.R;
import edu.northeastern.numad23sp_team16.models.Friend;
import edu.northeastern.numad23sp_team16.models.User;

public class AddFriendsActivity extends AppCompatActivity {

    private RecyclerView userListRecyclerView;
    private UsernameAdapter usernameAdapter;
    private List<Username> usersList;
    private List<Username> friendsList;
    private List<String> friendIdsList = new ArrayList<>();

    private final String FRIENDS_LIST = "FRIENDS_LIST";
    private final String CURRENT_USER = "CURRENT_USER";
    private String currentUser;
    private DatabaseReference usersRef;
    private DatabaseReference friendsRef;


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

        // TODO: obtain friendsList from firebase database
        // initialize usersRef and friendsRef from firebase database
        usersRef = FirebaseDatabase.getInstance().getReference("FinalProject").child("FinalProjectUsers");
        friendsRef = FirebaseDatabase.getInstance().getReference("FinalProject").child("FinalProjectFriends");

//        // obtain friendsList from ShareActivity, needs to be replaced with data from firebase later
//        Bundle bundle= getIntent().getExtras();
//        if (bundle != null) {
//            ArrayList<Username> preList = bundle.getParcelableArrayList(FRIENDS_LIST);
//            for (Username each : preList) {
//                if (friendsList.stream().noneMatch(e -> e.getName().equals(each.getName()))) {
//                    friendsList.add(each);
//                }
//            }
//        }

        // get friendIdsList data from firebase
        getFriendIdsListData();

        // get usersList data and update the previously selected friends status
        getUsersListData();

        // initialize views
        userListRecyclerView = findViewById(R.id.userlist_recyclerview);
        usernameAdapter = new UsernameAdapter(usersList);
        userListRecyclerView.setAdapter(usernameAdapter);
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
                        friendsList.add(username);
                    }
                }
                if (friendsList.size() == 0) {
                    Toast.makeText(AddFriendsActivity.this, "Did not add any friends.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(AddFriendsActivity.this, "Saved selected users as friends.",
                            Toast.LENGTH_LONG).show();
                }

                // TODO: save friends list for the user in the firebase database
                for (Username each : friendsList) {

                }

                Intent intent = new Intent(AddFriendsActivity.this, ShareActivity.class);
                // close all the activities in the call stack above ShareActivity and bring it to
                // the top of the call stack
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // pass the friendsList to the ShareActivity, need to be replaced with data from firebase later
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(FRIENDS_LIST, (ArrayList<? extends Parcelable>) friendsList);
                intent.putExtras(bundle);

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

    // TODO: obtain friendsList from firebase database
    private void getFriendIdsListData() {
        // Connect with firebase
        friendsRef.addChildEventListener(
                new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            if (Objects.requireNonNull(data.getValue(Friend.class)).getCurrentUserId().equals(currentUser)) {
                                String friendId = Objects.requireNonNull(data.getValue(Friend.class)).getFriendId();
                                friendIdsList.add(friendId);
                            }
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void getUsersListData() {
        usersList = new ArrayList<>();
        // Connect with firebase
        usersRef.addChildEventListener(
                new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            String userId = Objects.requireNonNull(data.getKey());
                            String userName = Objects.requireNonNull(data.getValue(User.class)).getUsername();
                            Username nameItem = new Username(userName);
                            // check if the user is already a friend
                            if (friendIdsList != null
                                    && friendIdsList.stream().anyMatch(each -> each.equals(userId))) {
                                nameItem.setSelected(true);
                            }
                            if (!nameItem.isSelected()) {
                                usersList.add(nameItem);
                            }
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );
//        String[] names = new String[] {"Jack", "Steve", "Jane", "Martha", "Ines", "Lindsey", "Ryan",
//        "Brady"};
//        for (String name : names) {
//            Username user = new Username(name);
//            if (friendsList != null
//                    && friendsList.stream().anyMatch(each -> each.getName().equals(name))) {
//                user.setSelected(true);
//            }
//            if (!user.isSelected()) {
//                usersList.add(user);
//            }
//        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        // store usersList selected status
        if (usersList.size() != 0) {
            for (Username user : usersList) {
                outState.putBoolean(user.getName(), user.isSelected());
            }
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // restore usersList selected status
        if (usersList.size() != 0) {
            for (Username user: usersList) {
                boolean isSelected = savedInstanceState.getBoolean(user.getName());
                user.setSelected(isSelected);
            }
        }
    }
}