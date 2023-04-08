package edu.northeastern.numad23sp_team16.models;

public class Friend {
    private String currentUserId;
    private String friendId;

    public Friend(String currentUserId, String friendId) {
        this.currentUserId = currentUserId;
        this.friendId = friendId;
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    public String getFriendId() {
        return  friendId;
    }
}
