package com.bombhunt.game.view;

import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.EntitySubscription;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.bombhunt.game.ecs.components.SpriteComponent;
import com.bombhunt.game.ecs.components.TransformComponent;
import com.bombhunt.game.ecs.components.VelocityComponent;
import com.bombhunt.game.ecs.systems.SpriteSystem;
import com.bombhunt.game.ecs.systems.VelocitySystem;



public class GameScreen extends InputAdapter implements IView{

    private World world;

    public static int TPS = 120;

    private float accTime = 0;
    EntitySubscription subscription;

    private SpriteBatch batch;
    private ComponentMapper<SpriteComponent> mapSprite;

    public GameScreen(){
        WorldConfiguration config = new WorldConfigurationBuilder()
                .with(new SpriteSystem(), new VelocitySystem())
                .build();
        world = new World(config);
        world.getAspectSubscriptionManager().get(Aspect.all(SpriteComponent.class));
        mapSprite = world.getMapper(SpriteComponent.class);
        ComponentMapper<TransformComponent> mapTransform = world.getMapper(TransformComponent.class);
        ComponentMapper<VelocityComponent> mapVelocity = world.getMapper(VelocityComponent.class);

        subscription = world.getAspectSubscriptionManager().get(Aspect.all(SpriteComponent.class, TransformComponent.class));
        batch = new SpriteBatch();

        Sprite test = new Sprite(new Texture("badlogic.jpg"));

        Archetype testType = new ArchetypeBuilder()
                .add(TransformComponent.class)
                .add(SpriteComponent.class)
                .add(VelocityComponent.class)
                .build(world);
        int entityTest = world.create(testType);
        int entityTest2 = world.create(testType);
        mapSprite.get(entityTest).sprite = test;
        mapVelocity.get(entityTest).velocity = new Vector2(20f,20f);

        mapSprite.get(entityTest2).sprite = new Sprite(test);
        mapVelocity.get(entityTest2).velocity = new Vector2(-20f,-20f);
        mapTransform.get(entityTest2).position = new Vector3(200f, 0f, 200f);
    }

    @Override
    public void update(float dtime){
        accTime += dtime;
        world.setDelta(1f/TPS);
        while(accTime >= 1f/TPS){
            world.process();
            accTime -= 1f/TPS;
        }
        System.out.println(Gdx.graphics.getFramesPerSecond());
        //world.setDelta(dtime);
        //world.process();
    }

    @Override
    public void render(){
        batch.begin();
        IntBag entities = subscription.getEntities();
        for(int e : entities.getData()){
            SpriteComponent spriteComponent = mapSprite.get(e);
            spriteComponent.sprite.draw(batch);
        }
        batch.end();

    }

    @Override
    public void dispose(){
        world.dispose();
    }

    @Override
    public InputProcessor getInputProcessor() {
        return this;
    }


}
