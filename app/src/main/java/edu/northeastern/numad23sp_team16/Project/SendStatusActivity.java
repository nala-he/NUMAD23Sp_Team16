package edu.northeastern.numad23sp_team16.Project;

import androidx.annotation.NonNull;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.northeastern.numad23sp_team16.R;
import edu.northeastern.numad23sp_team16.models.Friend;
import edu.northeastern.numad23sp_team16.models.User;

public class SendStatusActivity extends AppCompatActivity {
    private List<Username> friendsList = new ArrayList<>();
    private List<String> friendIdsList = new ArrayList<>();

    private List<Username> receiverList = new ArrayList<>();

    private final String FRIENDS_LIST = "FRIENDS_LIST";
    private final String CURRENT_USER = "CURRENT_USER";
    private String currentUser;
    private RecyclerView friendListRecyclerView;
    private UsernameAdapter friendListAdapter;
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
        title.setText("Send Pet Status to Friends");

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
//        Bundle bundle = getIntent().getExtras();
//        if (bundle != null) {
//            ArrayList<Username> preList = bundle.getParcelableArrayList(FRIENDS_LIST);
//            for (Username each : preList) {
//                if (friendsList.stream().noneMatch(e -> e.getName().equals(each.getName()))) {
//                    friendsList.add(each);
//                }
//            }
//        }

        // initialize views
        friendListRecyclerView = findViewById(R.id.userlist_recyclerview);
        friendListRecyclerView.setLayoutManager(new LinearLayoutManager(SendStatusActivity.this));
        TextView direction = findViewById(R.id.share_direction);
        direction.setText(R.string.send_my_pet_status_to_selected_friends);
        TextView userListTitle = findViewById(R.id.user_list_title);
        userListTitle.setText("List of Friends");

        Button addButton = findViewById(R.id.add_selected_friends_button);
        addButton.setVisibility(View.INVISIBLE);
        Button sendButton = findViewById(R.id.send_status_button);
        sendButton.setText(R.string.send_my_pet_status_to_friends);

        // get friends' ids from firebase
        getFriendIdsListData();
        // get friends' names based on ids from firebase
        getFriendNameList();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Username username : friendsList) {
                    if (username.isSelected()) {
                        receiverList.add(username);
                    }
                }
                // TODO: send user's pet status to selected friends from friendsList
                if (receiverList.size() != 0) {
                    Toast.makeText(SendStatusActivity.this, "Sent pet status to selected friends.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SendStatusActivity.this, "Did not select any friends.",
                            Toast.LENGTH_LONG).show();
                }

                Intent intent = new Intent(SendStatusActivity.this, ShareActivity.class);
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

    // obtain friends data from firebase database
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
                                friendIdsList.add(friendId);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void getFriendNameList() {
        // Connect with firebase
        usersRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (String userId : friendIdsList) {
                            String name = snapshot.child(userId).child("username").getValue(String.class);
                            Username friend = new Username(name, userId);
                            friendsList.add(friend);
                        }
                        friendListAdapter = new UsernameAdapter(friendsList);
                        friendListRecyclerView.setAdapter(friendListAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        // store friendsList selected status
        if (friendsList.size() != 0) {
            for (Username friend : friendsList) {
                outState.putBoolean(friend.getName(), friend.isSelected());
            }
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // restore friendsList selected status
        if (friendsList.size() != 0) {
            for (Username friend: friendsList) {
                boolean isSelected = savedInstanceState.getBoolean(friend.getName());
                friend.setSelected(isSelected);
            }
        }
    }
}