package com.bombhunt.game.model.ecs.systems;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.Component;
import com.artemis.ComponentManager;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.math.Vector3;
import com.bombhunt.game.model.ecs.components.Box2dComponent;
import com.bombhunt.game.model.ecs.components.ExplosionComponent;
import com.bombhunt.game.model.ecs.components.NetworkComponent;
import com.bombhunt.game.model.ecs.components.TimerComponent;
import com.bombhunt.game.model.ecs.components.TransformComponent;
import com.bombhunt.game.model.ecs.factories.BombFactory;
import com.bombhunt.game.model.ecs.factories.IEntityFactory;
import com.bombhunt.game.model.ecs.factories.INetworkFactory;
import com.bombhunt.game.services.networking.IPlayServices;
import com.bombhunt.game.services.networking.Message;
import com.bombhunt.game.services.networking.NetworkManager;
import com.bombhunt.game.services.networking.PlayerInfo;
import com.bombhunt.game.services.networking.RealtimeListener;

import sun.nio.ch.Net;

public class NetworkSystem extends BaseEntitySystem implements RealtimeListener {
    private ComponentMapper<ExplosionComponent> mapExplosion;
    private ComponentMapper<TransformComponent> mapTransform;
    private ComponentMapper<NetworkComponent> mapNetwork;
    private ComponentMapper<Box2dComponent> mapBox2d;
    private NetworkManager netManager;
    private IPlayServices playServices;


    private HashMap<Integer, Integer> entityIdMap = new HashMap<Integer, Integer>();

    private HashMap<String, INetworkFactory> factories;

    private int localTurn = 0;

    public NetworkSystem(HashMap<String, INetworkFactory> factories) {
        super(
            Aspect.all(NetworkComponent.class).one(TransformComponent.class, ExplosionComponent.class, Box2dComponent.class)
        );
        this.netManager = NetworkManager.getInstance();
        PlayerInfo player = this.netManager.getPlayerInfo(netManager.getPlayerService().getLocalID());
        NetworkComponent.playerIdx = player.playerIndex;
        this.netManager.openChannel(this, 50);
        this.factories = factories;
    }

    @Override
    protected void inserted(int entityId) {
        super.inserted(entityId);
        NetworkComponent netComponent = mapNetwork.get(entityId);
        netComponent.localTurn = localTurn;
        netComponent.sequenceNumber = NetworkComponent.getNextId();
        entityIdMap.put(netComponent.sequenceNumber, entityId);
        System.out.println("Network component added " + " " + netComponent.owner + " " + netComponent.sequenceNumber);

    }

    @Override
    protected void removed(int entityId) {
        super.removed(entityId);
        entityIdMap.remove(entityId);
        System.out.println("Network component removed");
    }

    @Override
    protected void processSystem() {
        IntBag entities = subscription.getEntities();
        localTurn++;
        int[] ids = entities.getData();
        for(int i = 0; i < entities.size(); i++){
            NetworkComponent netComponent = mapNetwork.get(ids[i]);
            netComponent.localTurn++;

            entityIdMap.put(netComponent.sequenceNumber, ids[i]);

            if(netComponent.isLocal && netComponent.localTurn % 4 == 0 && !netComponent.owner.equals("LOCAL")){
                Message m = new Message(new byte[512], "", 0);
                m.putString("UPDATE_ENTITY");
                m.getBuffer().putInt(netComponent.sequenceNumber);
                m.putBox2d(mapBox2d.get(ids[i]));
                //System.out.println("Sending: " + netComponent.sequenceNumber + " " + ids[i]);
                this.playServices.sendToAllReliably(m.getData());
            }
        }
    }


    public void handleDataReceived(Message message){
        String type = message.getString();
        ByteBuffer b = message.getBuffer();

        if (type.equals("CREATE_ENTITY")){
            String what = message.getString();
            INetworkFactory factory = factories.get(what);
            int entity = factory.createFromMessage(message);
        } else if(type.equals("UPDATE_ENTITY")) {

            int seqNum = message.getBuffer().getInt();
            if(entityIdMap.containsKey(seqNum)){
                int e = entityIdMap.get(seqNum);
                System.out.print("seq: " + seqNum);
                System.out.println(" entity: " + e);
                message.getBox2d(mapBox2d.get(e));
            }
            System.out.println();
        }
    }


    public void setSender(IPlayServices playService) {
        this.playServices = playService;
    }

}
