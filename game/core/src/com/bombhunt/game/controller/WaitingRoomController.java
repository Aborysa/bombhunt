package com.bombhunt.game.controller;

import com.bombhunt.game.BombHunt;
import com.bombhunt.game.services.networking.IPlayServices;
import com.bombhunt.game.services.networking.Message;
import com.bombhunt.game.services.networking.NetworkManager;
import com.bombhunt.game.services.networking.RealtimeListener;
import com.bombhunt.game.services.networking.RoomListener;
import com.bombhunt.game.view.screens.GameScreen;
import com.bombhunt.game.view.screens.MainMenuScreen;

import java.util.Random;

/**
 * Created by erlin on 15.04.2018.
 */

public class WaitingRoomController extends BasicController implements RoomListener, RealtimeListener{

    private IPlayServices sender;
    public WaitingRoomController(BombHunt bombHunt){ super(bombHunt); }

    public void backToMainMenu() {changeView(new MainMenuScreen(bombHunt));}
    public void enterGameScreen() {changeView(new GameScreen(bombHunt));}
    public void pingRemote() {
        Message message = new Message(new byte[512],"",0);
        Random random = new Random();
        message.getBuffer().putInt(random.nextInt(100000000));
        sender.sendToAllReliably(message.getData());
        System.out.println("Sending to others");
    }

    @Override
    public void roomConnected() {
        System.out.println("roomconnected callback in waiting room controller");
        NetworkManager networkManager = new NetworkManager();
        bombHunt.getPlayServices().setRealTimeListener(networkManager);
        networkManager.openChannel(this, 10);
        pingRemote();
    }

    @Override
    public void handleDataReceived(Message message) {
        int num = message.getBuffer().getInt();
        System.out.println("Got number");
        System.out.println(num);
        System.out.println(message.getSender());

    }

    @Override
    public void setSender(IPlayServices playServices) {
        this.sender = playServices;
    }
}
