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

import sun.nio.cs.US_ASCII;

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

    // TODO: make getters to get specific data from the message. i.e. get x pos (byte[2] - byte[6]) -> float xpos


    public Message copy(){
        return new Message(Arrays.copyOf(this.data, buffer.position()), this.senderID, this.describeContents);
    }

    public ByteBuffer getBuffer(){
        return buffer;
    }


    public String getString(){
        byte[] chars = new byte[128];
        byte next = buffer.get();
        int idx = 0;
        while(next != '\0'){
            chars[idx++] = next;
            next = buffer.get();
        }
        return new String(chars, StandardCharsets.UTF_8);
    }

    public void putString(String str){
        buffer.put(str.getBytes(StandardCharsets.UTF_8));
        buffer.put((byte)'\0');
    }

    public void putTransform(TransformComponent transformComponent){
        buffer.putFloat(transformComponent.position.x);
        buffer.putFloat(transformComponent.position.y);
        buffer.putFloat(transformComponent.position.z);

    }

    public TransformComponent getTransform(TransformComponent component){
        component.position.set(buffer.getFloat(), buffer.getFloat(), buffer.getFloat());
        return component;
    }

    public void putBox2d(Box2dComponent component){
        Vector2 veloc = component.body.getLinearVelocity();
        Transform t = component.body.getTransform();
        buffer.putFloat(veloc.x);
        buffer.putFloat(veloc.y);
        buffer.putFloat(t.getPosition().x);
        buffer.putFloat(t.getPosition().y);
    }

    public Box2dComponent getBox2d(Box2dComponent component){
        Transform t = component.body.getTransform();
        component.body.setLinearVelocity(buffer.getFloat(), buffer.getFloat());
        component.body.setTransform(buffer.getFloat(), buffer.getFloat(), t.getRotation());
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
