package com.bombhunt.game.services.networking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class NetworkManager implements RealtimeListener{
    private HashMap<Integer, ListenerContainer> listeners;
    private IPlayServices sender;

    private List<PlayerInfo> players;
    private HashMap<String, PlayerInfo> playerMap;
    

    private static NetworkManager instance;
    
    public NetworkManager(){
        listeners = new HashMap<Integer, ListenerContainer>(255);
        instance = this;
    }

    public void openChannel(RealtimeListener listener, Integer channel) {
        openChannel(listener, channel, false);

    }


    public void openChannel(RealtimeListener listener, Integer channel, boolean waitForUpdate) {
        ListenerContainer container = new ListenerContainer();
        container.listener = listener;
        container.wait = waitForUpdate;
        listeners.put(channel, container);
        listener.setSender(createSender(channel));
    }

    public IPlayServices createSender(Integer channel){
        return new ChanneledSender(sender, channel);
    }

    public synchronized void readyForMessages(int channel){
        
        if(listeners.containsKey(channel)){
            ListenerContainer listenerContainer = listeners.get(channel);
            for(Message m : listenerContainer.queue){
                listenerContainer.listener.handleDataReceived(m);    
            }
            listenerContainer.queue.clear();
        }
    }


    @Override
    public synchronized void handleDataReceived(Message message) {
        byte[] data = message.copy().getData();
        /*System.out.println(data.length + " " + message.getData().length);
        for(int i=0; i < data.length; i++){
            if(i % 32 == 0){
                System.out.println();
            }
            System.out.print(data[i] + " ");
        }
        System.out.println();*/
        int channel = message.getBuffer().get();

        if(listeners.containsKey(channel)){
            ListenerContainer listenerContainer = listeners.get(channel);
            if(!listenerContainer.wait){
                listenerContainer.listener.handleDataReceived(message.copy());
            } else {
                listenerContainer.queue.add(message.copy());
            }
        }
    }

    @Override
    public void setSender(IPlayServices playServices) {
        this.sender = playServices;
    }

    public IPlayServices getPlayerService(){
        return this.sender;
    }

    public static NetworkManager getInstance(){
        return instance;
    }

    public void setPlayers(List<PlayerInfo> players){
        this.players = players;
        playerMap = new HashMap<String, PlayerInfo>();
        for(PlayerInfo player : players){
            playerMap.put(player.playerId, player);
        }
    }

    public PlayerInfo getPlayerInfo(String id){
        return playerMap.get(id);
    }


    private class ListenerContainer{
        public Boolean wait;
        public List<Message> queue = new ArrayList<Message>();
        public RealtimeListener listener;

    }

}
