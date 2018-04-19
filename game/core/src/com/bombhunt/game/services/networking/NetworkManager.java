package com.bombhunt.game.services.networking;

import java.util.HashMap;

public class NetworkManager implements RealtimeListener{
    private HashMap<Integer, RealtimeListener> listeners;
    private IPlayServices sender;

    private static NetworkManager instance;
    
    public NetworkManager(){
        listeners = new HashMap<Integer, RealtimeListener>(255);
        instance = this;
    }

    public void openChannel(RealtimeListener listener, Integer channel) {
        listeners.put(channel, listener);
        listener.setSender(new ChanneledSender(sender, channel));
    }

    @Override
    public void handleDataReceived(Message message) {
        byte[] data = message.copy().getData();
        System.out.println(data.length + " " + message.getData().length);
        for(int i=0; i < data.length; i++){
            if(i % 32 == 0){
                System.out.println();
            }
            System.out.print(data[i] + " ");
        }
        System.out.println();
        int channel = message.getBuffer().get();

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
