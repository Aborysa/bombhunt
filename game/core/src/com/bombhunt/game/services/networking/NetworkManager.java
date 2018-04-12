package com.bombhunt.game.services.networking;

import java.util.HashMap;

public class NetworkManager implements RealtimeListener{
    private HashMap<Integer, RealtimeListener> listeners;
    private IPlayServices sender;

    public void openChannel(RealtimeListener listener, Integer channel) {
        listeners.put(channel, listener);
    }

    @Override
    public void handleDataReceived(Message message) {
        int channel = message.getInt();
        if(listeners.containsKey(channel)){
            listeners.get(channel).handleDataReceived(message.copy());
        }
    }

    @Override
    public void setSender(IPlayServices playServices) {
        this.sender = playServices;
    }
}
