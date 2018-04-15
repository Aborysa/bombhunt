package com.bombhunt.game.services.networking;


import java.util.ArrayList;

public class PlayAdapter implements IPlayServices {
    public void signIn(){}
    public void signOut(){}
    public void startMatchMaking(){}
    public boolean isSignedIn(){return false;}
    public void sendToAllReliably(byte[] message){}
    public void sendToOneReliably(byte[] message, String userID){}
    public void setRealTimeListener(RealtimeListener listener){}
    public String getLocalID(){return "";}
    public ArrayList<String> getRemotePlayers(){return new ArrayList<String>();}

    @Override
    public void setRoomListener(RoomListener listener) {

    }
}
