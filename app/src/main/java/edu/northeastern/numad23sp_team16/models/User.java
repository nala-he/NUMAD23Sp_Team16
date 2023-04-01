package edu.northeastern.numad23sp_team16.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    public String username;

    public User() {
        // Default constructor for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username) {
        this.username = username;
    }
}