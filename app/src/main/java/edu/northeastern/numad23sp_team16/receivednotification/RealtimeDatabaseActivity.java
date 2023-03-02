package edu.northeastern.numad23sp_team16.receivednotification;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import edu.northeastern.numad23sp_team16.R;

public class RealtimeDatabaseActivity extends AppCompatActivity {
    private static final String TAG = RealtimeDatabaseActivity.class.getSimpleName();

    private DatabaseReference mDatabase;
    private TextView username1;
    private TextView sticker1;
    private TextView username2;
    private TextView sticker2;
    private RadioButton receiverA;
    private RadioButton option1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime_database);

        username1 = (TextView) findViewById(R.id.username1);
        username2 = (TextView) findViewById(R.id.username2);
        sticker1 = (TextView) findViewById(R.id.sticker1);
        sticker2 = (TextView) findViewById(R.id.sticker2);
        receiverA = (RadioButton) findViewById(R.id.receiverA);
        option1 = (RadioButton) findViewById(R.id.sticker_option1);

        // Connect with firebase
        //
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // Update the sticker in realtime
        mDatabase.child("messages").addChildEventListener(
                new ChildEventListener() {

                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        showSticker(dataSnapshot);
                        Log.e(TAG, "onChildAdded: dataSnapshot = " + dataSnapshot.getValue().toString());
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        showSticker(dataSnapshot);
                        Log.v(TAG, "onChildChanged: " + dataSnapshot.getValue().toString());
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
    }



    // Send sticker button
    public void sendSticker(View view) {
        RealtimeDatabaseActivity.this.onSendSticker(mDatabase, receiverA.isChecked() ? "receiver A" : "receiver B");
    }

//    // Reset USERS Button
//    public void resetUsers(View view) {
//
//        User user;
//        user = new User("user1", "0");
//        Task t1 = mDatabase.child("users").child(user.username).setValue(user);
//
//        user = new User("user2", "0");
//        Task t2 = mDatabase.child("users").child(user.username).setValue(user);
//
//        if(!t1.isSuccessful() && !t2.isSuccessful()){
//            Toast.makeText(getApplicationContext(),"Unable to reset players!",Toast.LENGTH_SHORT).show();
//        }
//        else if(!t1.isSuccessful() && t2.isSuccessful()){
//            Toast.makeText(getApplicationContext(),"Unable to reset player1!",Toast.LENGTH_SHORT).show();
//        }
//        else if(t1.isSuccessful() && t2.isSuccessful()){
//            Toast.makeText(getApplicationContext(),"Unable to reset player2!",Toast.LENGTH_SHORT).show();
//        }
//
//
//    }


    /**
     * Called on score_user1 add
     *
     * @param postRef
     * @param user
     */
    private void onSendSticker(DatabaseReference postRef, String user) {
        postRef
                .child("users")
                .child(user)
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {

                        User user = mutableData.getValue(User.class);
                        if (user == null) {
                            return Transaction.success(mutableData);
                        }

                        user.score = String.valueOf(Integer.valueOf(user.score) + 5);

                        mutableData.setValue(user);
                        int i =0 ;

                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b,
                                           DataSnapshot dataSnapshot) {
                        // Transaction completed
                        Log.d(TAG, "postTransaction:onComplete:" + databaseError);
                        Toast.makeText(getApplicationContext()
                                , "DBError: " + databaseError, Toast.LENGTH_SHORT).show();
                    }



                });
    }


    private void showSticker(DataSnapshot dataSnapshot) {
        User user = dataSnapshot.getValue(User.class);

        if (dataSnapshot.getKey().equalsIgnoreCase("user1")) {
            score_user1.setText(String.valueOf(user.score));
            user1.setText(user.username);
        } else {
            score_user2.setText(String.valueOf(user.score));
            user2.setText(user.username);
        }
    }

}