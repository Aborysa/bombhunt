package com.bombhunt.game.services.networking;


import java.util.EventListener;

public interface RealtimeListener {
    public void handleDataRecieved(Message message);
}
