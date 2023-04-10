package edu.northeastern.numad23sp_team16.models;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Message {
    public String receiverName;
    public String senderName;
    public int heartCount;
    public String petType;
    public String timeStamp;

    public Message() {
        // Default constructor required for calls to DataSnapshot.getValue(Message.class)
    }

    public Message(String receiverName, String senderName, int heartCount, String petType) {
        this.receiverName = receiverName;
        this.senderName = senderName;
        this.heartCount = heartCount;
        this.petType = petType;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        this.timeStamp = String.valueOf(formatter.format(timestamp));
    }
}
