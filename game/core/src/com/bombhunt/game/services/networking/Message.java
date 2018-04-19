package com.bombhunt.game.services.networking;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Transform;
import com.bombhunt.game.model.ecs.components.Box2dComponent;
import com.bombhunt.game.model.ecs.components.NetworkComponent;
import com.bombhunt.game.model.ecs.components.TimerComponent;
import com.bombhunt.game.model.ecs.components.TransformComponent;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Message {
    private byte[] data;
    private ByteBuffer buffer;
    private String senderID;
    private int describeContents;



    public Message(byte[] data, String senderID, int describeContents){
        this.data = data;
        buffer = ByteBuffer.wrap(data);
        this.senderID = senderID;
        this.describeContents = describeContents;
    }

    public Message copy(){
        return new Message(Arrays.copyOf(this.data, buffer.position()), this.senderID, this.describeContents);
    }

    public ByteBuffer getBuffer(){
        return buffer;
    }


    public Vector2 getVector2(Vector2 dst){
        dst.set(buffer.getFloat(), buffer.getFloat());
        return dst;
    }

    public Vector2 getVector2(){
        return getVector2(new Vector2());
    }

    public void putVector(Vector2 src){
        buffer.putFloat(src.x);
        buffer.putFloat(src.y);
    }

    public void putVector(Vector3 src){
        buffer.putFloat(src.x);
        buffer.putFloat(src.y);
        buffer.putFloat(src.z);
    }

    public Vector3 getVector3(Vector3 dst){
        dst.set(buffer.getFloat(), buffer.getFloat(), buffer.getFloat());
        return dst;
    }

    public Vector3 getVector3(){
        return getVector3(new Vector3());
    }

    public String getString(){
        int len = buffer.getInt();
        byte[] chars = new byte[len];
        buffer.get(chars);
        return new String(chars, StandardCharsets.UTF_8);
    }

    public void putString(String str){
        byte[] data = str.getBytes(StandardCharsets.UTF_8);
        buffer.putInt(data.length);
        buffer.put(data);
    }

    public void putTransform(TransformComponent transformComponent){
        putVector(transformComponent.position);
    }

    public TransformComponent getTransform(TransformComponent component){
        getVector3(component.position);
        return component;
    }

    public void putBox2d(Box2dComponent component){
        putVector(component.body.getLinearVelocity());
        putVector(component.body.getTransform().getPosition());
    }

    public Box2dComponent getBox2d(Box2dComponent component){
        component.body.setLinearVelocity(getVector2());
        component.body.setTransform(getVector2(), component.body.getTransform().getRotation());
        return component;
    }

    public void putNetwork(NetworkComponent component){
        buffer.putInt(component.localTurn);
    }

    public NetworkComponent getNetwork(NetworkComponent component){
        component.remoteTurn = buffer.getInt();
        return component;
    }

    public TimerComponent getTimer(TimerComponent component){
        component.timer = buffer.getFloat();
        return component;
    }

    public void putTimer(TimerComponent component){
        buffer.putFloat(component.timer);
    }

    public String getSender(){
        return senderID;
    }

    public byte[] getData(){
        return data;
    }
}
