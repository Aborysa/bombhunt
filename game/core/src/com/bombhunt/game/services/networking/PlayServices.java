package com.bombhunt.game.services.networking;

/**
 * Created by erlin on 22.03.2018.
 */

public interface PlayServices {
    public void signIn();
    public void signOut();
    public void startMatchMaking();
    public boolean isSignedIn();
    public void sendToAllReliably(byte[] message);
}
