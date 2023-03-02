package edu.northeastern.numad23sp_team16.receivednotification;



public class User {

    public String username;


    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username) {
        this.username = username;
    }

}
