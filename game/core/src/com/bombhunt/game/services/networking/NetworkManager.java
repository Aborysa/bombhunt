package com.bombhunt.game.services.networking;

import java.util.HashMap;

public class NetworkManager implements RealtimeListener{
    private HashMap<Integer, RealtimeListener> listeners;
    private IPlayServices sender;

    public void openChannel(RealtimeListener listener, Integer channel) {

    }

    @Override
    public void handleDataReceived(Message message) {

    }

    @Override
    public void setSender(IPlayServices playServices) {
        this.sender = playServices;
    }
}
