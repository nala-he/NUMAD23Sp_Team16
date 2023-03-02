package edu.northeastern.numad23sp_team16.receivednotification;

public class Message {
    public String receiverName;
    public String senderName;
    public String stickerId;
//    public String timeStamp;

    public Message() {
        // Default constructor required for calls to DataSnapshot.getValue(Message.class)
    }

    public Message(String receiverName, String senderName, String stickerId) {
        this.receiverName = receiverName;
        this.senderName = senderName;
        this.stickerId = stickerId;
    }
}
