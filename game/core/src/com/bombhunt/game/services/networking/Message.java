package com.bombhunt.game.services.networking;

public class Message {
    private byte[] data;
    private String senderID;
    private int describeContents;

    public Message(byte[] data, String senderID, int describeContents){
        this.data = data;
        this.senderID = senderID;
        this.describeContents = describeContents;
    }

    // TODO: make getters to get specific data from the message. i.e. get x pos (byte[2] - byte[6]) -> float xpos
}
