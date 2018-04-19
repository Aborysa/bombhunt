package com.bombhunt.game.controller;

import com.artemis.utils.Sort;
import com.badlogic.gdx.Net;
import com.bombhunt.game.BombHunt;
import com.bombhunt.game.services.networking.IPlayServices;
import com.bombhunt.game.services.networking.Message;
import com.bombhunt.game.services.networking.NetworkManager;
import com.bombhunt.game.services.networking.PlayerInfo;
import com.bombhunt.game.services.networking.RealtimeListener;
import com.bombhunt.game.services.networking.RoomListener;
import com.bombhunt.game.view.screens.GameScreen;
import com.bombhunt.game.view.screens.MainMenuScreen;
import com.bombhunt.game.view.screens.WaitingRoomScreen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by erlin on 15.04.2018.
 */

public class WaitingRoomController extends BasicController implements RoomListener, RealtimeListener{

    private IPlayServices sender;
    private WaitingRoomScreen waitingRoomScreen;
    private HashMap<String, PlayerInfo> playerInfo = new HashMap<String, PlayerInfo>(4);
    int leftToReady = 4;
    private PlayerInfo localPlayer;


    public WaitingRoomController(BombHunt bombHunt, WaitingRoomScreen waitingRoomScreen){
        super(bombHunt);
        this.waitingRoomScreen = waitingRoomScreen;
    }

    public void backToMainMenu() {changeView(new MainMenuScreen(bombHunt));}
    public void enterGameScreen() {

        PlayerInfo[] players = playerInfo.values().toArray(new PlayerInfo[playerInfo.size()]);
        Sort.instance().sort(players, new Comparator<PlayerInfo>() {
            @Override
            public int compare(PlayerInfo p1, PlayerInfo p2) {
                return p1.randomNumber - p2.randomNumber;
            }
        });

        for(int i = 0; i < players.length; i++){
            players[i].playerIndex = i;
        }
        NetworkManager netManager = NetworkManager.getInstance();
        netManager.setPlayers(Arrays.asList(players));
        changeView(new GameScreen(bombHunt, Arrays.asList(players)));
    }


    public void pingRemote() {
        Message message = new Message(new byte[512],"",0);
        Random random = new Random();
        int randomNumber = random.nextInt() & Integer.MAX_VALUE;
        message.putString("RAN_NUM");
        message.getBuffer().putInt(randomNumber);
        sender.sendToAllReliably(message.getData());
        System.out.println("Sending to others");
        System.out.println("Random number");

        localPlayer.randomNumber = randomNumber;
    }

    @Override
    public void roomConnected() {
        System.out.println("roomconnected callback in waiting room controller");
        IPlayServices playServices = bombHunt.getPlayServices();

        leftToReady = playServices.getRemotePlayers().size();

        NetworkManager networkManager = new NetworkManager();
        playServices.setRealTimeListener(networkManager);

        for(String playerId : playServices.getRemotePlayers()){
            playerInfo.put(playerId, new PlayerInfo(playerId, false));
        }
        localPlayer = new PlayerInfo(playServices.getLocalID(), true);
        playerInfo.put(localPlayer.playerId, localPlayer);

        networkManager.openChannel(this, 10);



        pingRemote();
    }

    @Override
    public void leftRoom(){
        waitingRoomScreen.leftRoom = true;
    }

    @Override
    public void handleDataReceived(Message message) {
        String type = message.getString();
        System.out.println(type);
        if(type.equals("RAN_NUM")) {
            int num = message.getBuffer().getInt();
            System.out.println("Got number");
            System.out.println(num);
            System.out.println(message.getSender() + " " + bombHunt.getPlayServices().getLocalID());

            PlayerInfo player = playerInfo.get(message.getSender());
            player.randomNumber = num;

            // Do a check to see if ready
            boolean gotall = true;
            for (PlayerInfo p : playerInfo.values()) {
                if (p.randomNumber <= 0) {
                    gotall = false;
                }
            }
            if (gotall) {
                Message readyMessage = new Message(new byte[512], "", 0);
                readyMessage.putString("READY");
                sender.sendToAllReliably(readyMessage.getData());
            }
        } else if(type.equals("READY")){
            System.out.println("Player is ready " + message.getSender());
            leftToReady--;

            if(leftToReady <= 0){
                waitingRoomScreen.joinedRoom = true;
                //enterGameScreen();
            }
        }
    }


    @Override
    public void setSender(IPlayServices playServices) {
        this.sender = playServices;
    }
}
