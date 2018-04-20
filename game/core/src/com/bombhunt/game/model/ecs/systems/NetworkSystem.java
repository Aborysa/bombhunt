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
import com.artemis.annotations.DelayedComponentRemoval;
import com.artemis.annotations.LinkPolicy;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Transform;
import com.bombhunt.game.model.ecs.components.BombComponent;
import com.bombhunt.game.model.ecs.components.Box2dComponent;
import com.bombhunt.game.model.ecs.components.ExplosionComponent;
import com.bombhunt.game.model.ecs.components.KillableComponent;
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
    private ComponentMapper<BombComponent> mapBomb;
    private ComponentMapper<KillableComponent> mapKillable;

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
        this.netManager.openChannel(this, 50, true);
        this.factories = factories;
    }

    @Override
    protected void inserted(int entityId) {
        super.inserted(entityId);
        NetworkComponent netComponent = mapNetwork.get(entityId);
        netComponent.localTurn = localTurn;
        entityIdMap.put(netComponent.sequenceNumber, entityId);

    }

    @Override
    protected void removed(int entityId) {
        super.removed(entityId);
        NetworkComponent component = mapNetwork.get(entityId);
        entityIdMap.remove(component.sequenceNumber);
        if(component.isLocal && component.autoremove) {
            Message m = new Message(new byte[128], "", 0);
            m.putString("REMOVE_ENTITY");
            m.getBuffer().putInt(component.sequenceNumber);
            playServices.sendToAllUnreliably(m.getCompact());
        }
    }

    @Override
    protected void processSystem() {
        netManager.readyForMessages(50);
        IntBag entities = subscription.getEntities();
        localTurn++;
        int[] ids = entities.getData();
        for(int i = 0; i < entities.size(); i++){
            process(ids[i]);
        }
    }

    public void process(int e){
        NetworkComponent networkComponent = mapNetwork.get(e);
        //networkComponent.remoteTurn++;


        Box2dComponent box2d = mapBox2d.getSafe(e, null);
        TransformComponent transformComponent = mapTransform.getSafe(e, null);
        BombComponent bombComponent = mapBomb.getSafe(e, null);
        KillableComponent killableComponent = mapKillable.getSafe(e, null);



        if(networkComponent.isLocal && networkComponent.localTurn % networkComponent.updateRate == 0 && !networkComponent.owner.equals("NONE")){
            Message m = new Message(new byte[128], "", 0);
            m.putString("UPDATE_ENTITY");
            m.getBuffer().putInt(networkComponent.sequenceNumber);
            m.getBuffer().putInt(networkComponent.localTurn);

            if(box2d != null){
                m.putBox2d(box2d);
            }

            if(transformComponent != null){
                m.putTransform(transformComponent);
            }

            if(bombComponent != null){
                m.putBomb(bombComponent);
            }

            if(killableComponent != null){
                m.putKillable(killableComponent);
            }

            this.playServices.sendToAllReliably(m.getCompact());
        } else if(!networkComponent.isLocal) {
            /* Interpolate position and account for timers */
            int tickDiff = networkComponent.localTurn - networkComponent.remoteTurn;
            if(tickDiff > 0) {
                if (box2d != null) {

                    Body body = box2d.body;
                    Vector2 interpolated = Vector2.Zero;
                    Vector2 veloc = body.getLinearVelocity().cpy();
                    interpolated.lerp(veloc.scl(tickDiff * world.getDelta()), 0.1f);

                    Vector2 newpos = body.getTransform().getPosition().add(interpolated);
                    //body.setTransform(newpos, body.getTransform().getRotation());
                    System.out.println("Interpolating " + tickDiff + " " + veloc + " " + interpolated);
                }

                if(bombComponent != null) {
                    System.out.println("Interpolating bombtimer " + tickDiff * world.getDelta());

                    bombComponent.timer -= tickDiff * world.getDelta();
                }

                networkComponent.remoteTurn = networkComponent.localTurn;
            }

            networkComponent.remoteTurn++;
            networkComponent.localTurn++;

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
            int remoteTurn = message.getBuffer().getInt();
            if(entityIdMap.containsKey(seqNum)){
                int e = entityIdMap.get(seqNum);

                NetworkComponent networkComponent = mapNetwork.get(e);
                // Messages are sent unreliably, we only care about the newest data
                if(networkComponent.latestRemote  <= remoteTurn) {
                    networkComponent.latestRemote  = remoteTurn;
                    networkComponent.remoteTurn = remoteTurn;

                    Box2dComponent box2d = mapBox2d.getSafe(e, null);
                    TransformComponent transformComponent = mapTransform.getSafe(e, null);
                    BombComponent bombComponent = mapBomb.getSafe(e, null);
                    KillableComponent killableComponent = mapKillable.getSafe(e, null);

                    if (box2d != null) {
                        message.getBox2d(box2d);
                    }

                    if (transformComponent != null) {
                        message.getTransform(transformComponent);
                    }

                    if (bombComponent != null) {
                        message.getBomb(bombComponent);
                    }

                    if (killableComponent != null) {
                        message.getKillable(killableComponent);
                    }
                }
            }
        } else if (type.equals("REMOVE_ENTITY")) {
            int seqNum = message.getBuffer().getInt();
            if(entityIdMap.containsKey(seqNum)){
                int e = entityIdMap.get(seqNum);
                world.delete(e);
            }
        }
    }


    public void setSender(IPlayServices playService) {
        this.playServices = playService;
    }

}
