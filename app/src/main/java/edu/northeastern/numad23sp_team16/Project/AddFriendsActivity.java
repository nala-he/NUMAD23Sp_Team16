package edu.northeastern.numad23sp_team16.Project;

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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.northeastern.numad23sp_team16.R;

public class AddFriendsActivity extends AppCompatActivity {

    private RecyclerView userListRecyclerView;
    private UsernameAdapter usernameAdapter;
    private List<Username> usersList;
    private List<Username> friendsList = new ArrayList<>();
    private final String FRIENDS_LIST = "FRIENDS_LIST";


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

        // initialize views
        userListRecyclerView = findViewById(R.id.userlist_recyclerview);
        getUsersListData();
        usernameAdapter = new UsernameAdapter(usersList);
        userListRecyclerView.setAdapter(usernameAdapter);
        userListRecyclerView.setLayoutManager(new LinearLayoutManager(AddFriendsActivity.this));

        TextView userListTitle = findViewById(R.id.user_list_title);
        userListTitle.setText(R.string.list_of_users);

        Button addButton = findViewById(R.id.add_selected_friends_button);
        Button sendButton = findViewById(R.id.send_status_button);
        sendButton.setVisibility(View.INVISIBLE);
        addButton.setText(R.string.add_all_selected_users_as_friends);
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
                // pass the friendsList to the ShareActivity, need to be replaced with data from firebase later
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(FRIENDS_LIST, (ArrayList<? extends Parcelable>) friendsList);
                Intent intent = new Intent(AddFriendsActivity.this, ShareActivity.class);
                intent.putExtras(bundle);
                // close all the activities in the call stack above ShareActivity and bring it to
                // the top of the call stack
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

    private void getUsersListData() {
        usersList = new ArrayList<>();
        // TODO: obtain friendsList from firebase database

        // TODO: change the hardcoded users data to realtime data from firebase
        String[] names = new String[] {"Jack", "Steve", "Jane", "Martha", "Ines", "Lindsey", "Ryan",
        "Brady"};
        for (String name : names) {
            Username user = new Username(name);
            if (friendsList != null && friendsList.contains(user)) {
                user.setSelected(true);
            }
            usersList.add(user);
        }
    }
}