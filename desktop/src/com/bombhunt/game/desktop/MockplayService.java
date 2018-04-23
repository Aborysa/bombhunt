package com.bombhunt.game.desktop;

import com.bombhunt.game.services.networking.PlayAdapter;
import com.bombhunt.game.services.networking.RealtimeListener;
import com.bombhunt.game.services.networking.RoomListener;

import java.util.ArrayList;

public class MockplayService extends PlayAdapter{
    boolean signedIn = false;

    public void signIn(){
        signedIn = true;
    }
    public void signOut(){
        signedIn = false;
    }
    public void startMatchMaking(){

    }

    public boolean isSignedIn(){
        return signedIn;
    }

    public void setRealTimeListener(RealtimeListener listener){

    }

    public String getLocalID(){
        return "LOCAL_PLAYER";
    }
    public ArrayList<String> getRemotePlayers(){
        return new ArrayList(0);
    }
    public void setRoomListener(RoomListener listener){

    }
}
