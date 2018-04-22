package com.bombhunt.game.services.networking;


import java.util.EventListener;

public interface RealtimeListener {
    public void handleDataReceived(Message message);
    public void setSender(IPlayServices playServices);
}
