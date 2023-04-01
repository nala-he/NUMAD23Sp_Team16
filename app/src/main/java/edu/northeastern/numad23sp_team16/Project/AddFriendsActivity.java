package edu.northeastern.numad23sp_team16.Project;

import androidx.annotation.NonNull;
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

import edu.northeastern.numad23sp_team16.A8.models.User;
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

        // TODO: obtain friendsList from firebase database
        // obtain friendsList from ShareActivity, needs to be replaced with data from firebase later
        Bundle bundle= getIntent().getExtras();
        if (bundle != null) {
            ArrayList<Username> preList = bundle.getParcelableArrayList(FRIENDS_LIST);
            for (Username each : preList) {
                if (friendsList.stream().noneMatch(e -> e.getName().equals(each.getName()))) {
                    friendsList.add(each);
                }
            }
        }
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

    private void getUsersListData() {
        usersList = new ArrayList<>();
        // TODO: obtain friendsList from firebase database

        // TODO: change the hardcoded users data to realtime data from firebase
        String[] names = new String[] {"Jack", "Steve", "Jane", "Martha", "Ines", "Lindsey", "Ryan",
        "Brady"};
        for (String name : names) {
            Username user = new Username(name);
            if (friendsList != null
                    && friendsList.stream().anyMatch(each -> each.getName().equals(name))) {
                user.setSelected(true);
            }
            if (!user.isSelected()) {
                usersList.add(user);
            }
        }
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