package com.bombhunt.game.services.networking;

import java.util.HashMap;

public class NetworkManager implements RealtimeListener{
    private HashMap<Integer, RealtimeListener> listeners;
    private IPlayServices sender;

    private static NetworkManager instance;
    public NetworkManager(){
        /*if(instance != null){
            throw new RuntimeException("Singleton already instantiated");
        }*/
        listeners = new HashMap<Integer, RealtimeListener>(255);
        instance = this;
    }

    public void openChannel(RealtimeListener listener, Integer channel) {
        listeners.put(channel, listener);
        listener.setSender(new ChanneledSender(sender, channel));
    }

    @Override
    public void handleDataReceived(Message message) {
        int channel = message.getBuffer().getInt();
        if(listeners.containsKey(channel)){
            listeners.get(channel).handleDataReceived(message.copy());
        }
    }

    @Override
    public void setSender(IPlayServices playServices) {
        this.sender = playServices;
    }


    public static NetworkManager getInstance(){
        return instance;
    }

}
