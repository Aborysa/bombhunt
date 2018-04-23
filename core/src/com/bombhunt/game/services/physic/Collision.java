package com.bombhunt.game.services.physic;


import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/* Constants and helpers for collision */
public class Collision {

    // We're likely only going to operate on one world, so lets keep a static ref
    public static World world;

    public static final float worldTobox2d = 1 / 32f;
    public static final float box2dToWorld = 1 / worldTobox2d;

    public class Category {
        public static final short WALL = 0b0001;
        public static final short PLAYER = 0b0010;
        public static final short ITEM = 0b0100;
        public static final short BOMB = 0b1000;
    }

    public static final BodyDef kinematicDef = new BodyDef() {{
        type = BodyDef.BodyType.KinematicBody;
        fixedRotation = true;
        active = true;
    }};

    public static final BodyDef saticDef = new BodyDef() {{
        type = BodyType.StaticBody;
        fixedRotation = true;
        active = true;
    }};

    public static final BodyDef dynamicDef = new BodyDef() {{
        type = BodyType.DynamicBody;
        fixedRotation = false;
        active = true;
    }};

    public static final FixtureDef playerFixture = new FixtureDef() {{
        shape = new CircleShape();
        shape.setRadius(7 * worldTobox2d);
        density = 1;
        filter.categoryBits = Category.PLAYER;
        filter.maskBits = Category.WALL | Category.PLAYER;
        filter.groupIndex = 0;
        restitution = 0f;
        friction = 0f;
    }};

    public static final FixtureDef wallFixture = new FixtureDef() {{
        shape = new PolygonShape() {{
            setAsBox(1, 1);
        }};
        density = 1;
        filter.categoryBits = Category.WALL;
        filter.maskBits = Category.WALL | Category.PLAYER;
        filter.groupIndex = 0;
        restitution = 0f;
        friction = 1f;
    }};

    public static Body createBody(World world, BodyDef bodyDef, FixtureDef... fixtureDefs) {
        Body body = world.createBody(bodyDef);
        CircleShape circle = new CircleShape();
        for (FixtureDef fixtureDef : fixtureDefs) {
            body.createFixture(fixtureDef);
        }
        return body;
    }

    public static Body createBody(BodyDef bodyDef, FixtureDef... fixtureDefs) {
        return createBody(world, bodyDef, fixtureDefs);
    }

}
