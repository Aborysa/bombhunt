package com.bombhunt.game.services.networking;

import java.io.Console;
import java.util.ArrayList;
import java.util.Arrays;

import com.artemis.injection.CachedClass;

/**
 * Created by erlin on 22.03.2018.
 */

public class ChanneledSender implements IPlayServices{
    private IPlayServices playServices;
    private int channel;

    public ChanneledSender(IPlayServices playService, int channel){
        this.playServices = playService;
        this.channel = channel;
    }
    public void signIn(){this.playServices.signIn();}
    public void signOut(){this.playServices.signOut();}
    public void startMatchMaking(){this.playServices.startMatchMaking();}
    public boolean isSignedIn(){return this.playServices.isSignedIn();}
    public String getLocalID(){return this.playServices.getLocalID();}
    public ArrayList<String> getRemotePlayers(){return new ArrayList<String>();}

    @Override
    public void setRoomListener(RoomListener listener) {
        this.playServices.setRoomListener(listener);
    }

    private byte[] appendChannel(byte[] message){
        byte[] nMessage = new byte[message.length + 1];
        nMessage[0] = (byte)(this.channel & 0xFF);
        System.arraycopy(message, 0, nMessage, 1, message.length);
        return nMessage;
    }

    public void sendToAllReliably(byte[] message){
        this.playServices.sendToAllReliably(appendChannel(message));
    }

    public void sendToOneReliably(byte[] message, String userID){
        this.playServices.sendToOneReliably(appendChannel(message), userID);
    }
    public void setRealTimeListener(RealtimeListener listener){
    }
}
