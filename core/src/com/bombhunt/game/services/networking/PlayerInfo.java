package com.bombhunt.game.services.networking;


public class PlayerInfo {

    public final String playerId;
    public int randomNumber = -1;
    public int playerIndex = -1;
    public final boolean isLocal;

    public PlayerInfo(String playerId, boolean local){
        this.playerId = playerId;
        this.isLocal = local;
    }



}
