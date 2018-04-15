package com.bombhunt.game.model.ecs.systems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.IntBag;
import com.bombhunt.game.model.ecs.components.Box2dComponent;
import com.bombhunt.game.model.ecs.components.ExplosionComponent;
import com.bombhunt.game.model.ecs.components.NetworkComponent;
import com.bombhunt.game.model.ecs.components.TransformComponent;
import com.bombhunt.game.services.networking.IPlayServices;
import com.bombhunt.game.services.networking.Message;
import com.bombhunt.game.services.networking.NetworkManager;
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

    private int localTurn = 0;

    public NetworkSystem(NetworkManager manager) {
        super(
            Aspect.all(NetworkComponent.class).one(TransformComponent.class, ExplosionComponent.class, Box2dComponent.class )
        );
        this.netManager = manager;
        this.netManager.openChannel(this, 50);
    }

    @Override
    protected void inserted(int entityId) {
        super.inserted(entityId);
        NetworkComponent netComponent = mapNetwork.get(entityId);
        netComponent.localTurn = localTurn;
        netComponent.sequenceNumber = NetworkComponent.getNextId();
        entityIdMap.put(netComponent.sequenceNumber, entityId);



    }

    @Override
    protected void removed(int entityId) {
        super.removed(entityId);
        entityIdMap.remove(entityId);
    }

    @Override
    protected void processSystem() {
        IntBag entities = subscription.getEntities();
        localTurn++;
        int[] ids = entities.getData();
        for(int i = 0; i < entities.size(); i++){
            mapNetwork.get(ids[i]).localTurn++;
        }
    }

    public void handleDataReceived(Message message){

    }


    public void setSender(IPlayServices playService){
      this.playServices = playService;
    }
}
