package com.bombhunt.game.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Erling on 17/03/2018.
 * Inspired by Transform component used in Unity
 */

public class Transform extends Component{

    // physical position of entity
    public float posX;
    public float posY;

    // render layer
    public float posZ;

    // physical rotation of entity
    public float rotation;

    public float scaleX;
    public float scaleY;

    // adding this component to entity, can either specify where to add it
    public Transform(float posX, float posY, float posZ, float scaleX, float scaleY, float rotation){
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.rotation = rotation;
    }

    public Transform(){
        this(0, 0, 0, 1, 1, 0);
    }

    // choose either this or the next function for the game, can be used to update positions for enemy AI when recieving position data.
    /*public void setPosition(float posX, float posY){

    }

    public void setPosition(Vector2 position){

    }*/
}
