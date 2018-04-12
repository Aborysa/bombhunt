package com.bombhunt.game.services.networking;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class Message {
    public byte[] data;
    private String senderID;
    private int describeContents;
    private int seeker = 0;


    public Message(byte[] data, String senderID, int describeContents){
        this.data = data;
        this.senderID = senderID;
        this.describeContents = describeContents;
    }

    // TODO: make getters to get specific data from the message. i.e. get x pos (byte[2] - byte[6]) -> float xpos
    public byte getByte() {
        return data[seeker++];
    }

    public float getFloat() {
        byte[] floatArray = new byte[4];
        for(int i = 0; i < 4; i++){
            floatArray[i] = getByte();
        }
        return ByteBuffer.wrap(floatArray).getFloat();
    }

    public char getChar() {
        return (char) getByte();
    }

    public int getInt() {
        return (getByte() & 0xFF);
    }

    public static byte[] floatToByteArray (float value) {
        return ByteBuffer.allocate(4).putFloat(value).array();
    }

    public Message copy(){
        return new Message(Arrays.copyOf(this.data, seeker), this.senderID, this.describeContents);
    }
}
