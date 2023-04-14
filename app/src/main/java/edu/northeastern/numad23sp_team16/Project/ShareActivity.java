package edu.northeastern.numad23sp_team16.Project;

import static edu.northeastern.numad23sp_team16.R.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.northeastern.numad23sp_team16.R;

public class ShareActivity extends AppCompatActivity {
    private final String CURRENT_USER = "CURRENT_USER";
    private final String LOGIN_TIME = "LOGIN_TIME";

    private String currentUser;
    private String loginTime;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_share);

        // customize action bar back button and title
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(layout.action_bar_layout);
        actionBar.setHomeAsUpIndicator(drawable.back_button);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#D9D9D9")));
        actionBar.setDisplayHomeAsUpEnabled(true);
        // change action bar title
        AppCompatTextView title = findViewById(R.id.navbar_title);
        title.setText("Share with Friends");

        // Retrieve currently logged in user -- Yutong
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentUser = extras.getString(CURRENT_USER);
            loginTime = extras.getString(LOGIN_TIME);
        }
        Log.i("ShareActivity", "currentUser: " + currentUser);

//        if (bundle != null) {
//            ArrayList<Username> preList = new ArrayList<>(bundle.getParcelableArrayList(FRIENDS_LIST));
//            if (preList.size() != 0) {
//                for (Username each : preList) {
//                    if (friendsList.stream().noneMatch(e -> e.getName().equals(each.getName()))) {
//                        friendsList.add(each);
//                    }
//                }
//            }
//        }
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

    public void onClickAddFriends(View view) {
        // TODO: for demo now, need to be revised once implementing firebase database
//        Bundle bundle = new Bundle();
//        bundle.putParcelableArrayList(FRIENDS_LIST, (ArrayList<? extends Parcelable>) friendsList);

        Intent intent = new Intent(ShareActivity.this, AddFriendsActivity.class);
        // pass the current user id
        intent.putExtra(CURRENT_USER, currentUser);
        intent.putExtra(LOGIN_TIME, loginTime);
        startActivity(intent);
    }

    public void onClickSendStatus(View view) {
        // TODO: for demo now, need to be revised once implementing firebase database
//        Bundle bundle = new Bundle();
//        bundle.putParcelableArrayList(FRIENDS_LIST, (ArrayList<? extends Parcelable>) friendsList);
        Intent intent = new Intent(ShareActivity.this, SendStatusActivity.class);
        // pass the current user id
        intent.putExtra(CURRENT_USER, currentUser);
        intent.putExtra(LOGIN_TIME, loginTime);
        startActivity(intent);
    }
}