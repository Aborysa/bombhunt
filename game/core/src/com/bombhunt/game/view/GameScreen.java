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
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.bombhunt.game.ecs.components.AnimationComponent;
import com.bombhunt.game.ecs.components.SpriteComponent;
import com.bombhunt.game.ecs.components.TransformComponent;
import com.bombhunt.game.ecs.components.VelocityComponent;
import com.bombhunt.game.ecs.systems.SpriteSystem;
import com.bombhunt.game.ecs.systems.VelocitySystem;
import com.bombhunt.game.util.Assets;
import com.bombhunt.game.util.SpriteHelper;


public class GameScreen extends InputAdapter implements IView{

    private World world;

    public static int TPS = 120;

    private float accTime = 0;
    EntitySubscription subscription;

    private SpriteBatch batch;
    private ComponentMapper<SpriteComponent> mapSprite;
    private ComponentMapper<AnimationComponent> mapAnimation;

    public GameScreen(){
        System.out.println("Creating gamescreen");
        WorldConfiguration config = new WorldConfigurationBuilder()
                .with(new SpriteSystem(), new VelocitySystem())
                .build();
        world = new World(config);
        world.getAspectSubscriptionManager().get(Aspect.all(SpriteComponent.class));
        mapSprite = world.getMapper(SpriteComponent.class);
        ComponentMapper<TransformComponent> mapTransform = world.getMapper(TransformComponent.class);
        ComponentMapper<VelocityComponent> mapVelocity = world.getMapper(VelocityComponent.class);
        mapAnimation = world.getMapper(AnimationComponent.class);
        subscription = world.getAspectSubscriptionManager().get(Aspect.all(SpriteComponent.class, TransformComponent.class));
        batch = new SpriteBatch();

        //Sprite test = new Sprite(Assets.getInstance().get("tilemap1.png", Texture.class));
        Animation<Sprite> test = SpriteHelper.createAnimation(
                SpriteHelper.createSprites(
                        Assets.getInstance().get("tilemap1.atlas", TextureAtlas.class).findRegion("bomb_party_v4"),
                        16, 4, 18, 6
                ),
                2);
        Archetype testType = new ArchetypeBuilder()
                .add(TransformComponent.class)
                .add(SpriteComponent.class)
                .add(AnimationComponent.class)
                .add(VelocityComponent.class)
                .build(world);
        int entityTest = world.create(testType);
        //int entityTest2 = world.create(testType);
        mapAnimation.get(entityTest).animation = test;
        mapVelocity.get(entityTest).velocity = new Vector2(20f,0f);
        mapTransform.get(entityTest).position = new Vector3(0f, 150f, 0);
        mapTransform.get(entityTest).scale = new Vector2(10f, 10f);
        /*mapSprite.get(entityTest2).sprite = test.;
        mapVelocity.get(entityTest2).velocity = new Vector2(-20f,-20f);
        mapTransform.get(entityTest2).position = new Vector3(200f, 0f, 200f);*/
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
