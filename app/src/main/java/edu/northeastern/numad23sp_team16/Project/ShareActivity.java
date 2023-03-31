package edu.northeastern.numad23sp_team16.Project;

import static edu.northeastern.numad23sp_team16.R.*;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.numad23sp_team16.R;

public class ShareActivity extends AppCompatActivity {
    private List<Username> friendsList = new ArrayList<>();
    private final String FRIENDS_LIST = "FRIENDS_LIST";

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

        // TODO: obtain friendsList from ShareActivity, needs to be replaced with data from firebase later
        Bundle bundle= getIntent().getExtras();
        if (bundle != null) {
            friendsList.addAll(bundle.getParcelableArrayList(FRIENDS_LIST));
        }
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
        startActivity(new Intent(ShareActivity.this, AddFriendsActivity.class));
    }

    public void onClickSendStatus(View view) {
        // TODO: for demo now, need to be revised once implementing firebase database
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(FRIENDS_LIST, (ArrayList<? extends Parcelable>) friendsList);
        Intent intent = new Intent(ShareActivity.this, SendStatusActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}