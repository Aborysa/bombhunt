package com.bombhunt.game.services.networking;


public class PlayAdapter implements PlayServices {
    public void signIn(){}
    public void signOut(){}
    public void startMatchMaking(){}
    public boolean isSignedIn(){return false;}
    public void sendToAllReliably(byte[] message){}
}
