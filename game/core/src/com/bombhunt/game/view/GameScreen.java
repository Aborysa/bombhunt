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
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.bombhunt.game.ecs.components.AnimationComponent;
import com.bombhunt.game.ecs.components.SpriteComponent;
import com.bombhunt.game.ecs.components.TransformComponent;
import com.bombhunt.game.ecs.components.VelocityComponent;
import com.bombhunt.game.ecs.systems.SpriteSystem;
import com.bombhunt.game.ecs.systems.VelocitySystem;
import com.bombhunt.game.utils.Assets;
import com.bombhunt.game.utils.SpriteHelper;


public class GameScreen extends InputAdapter implements IView{

    private World world;

    public static int TPS = 120;

    private float accTime = 0;
    EntitySubscription subscription;

    private SpriteBatch batch;
    private ComponentMapper<SpriteComponent> mapSprite;

    private OrthogonalTiledMapRenderer mapRenderer;
    private ComponentMapper<AnimationComponent> mapAnimation;
    ComponentMapper<TransformComponent> mapTransform;

    private TiledMap testMap;
    public GameScreen(){
        System.out.println("Creating gamescreen");
        WorldConfiguration config = new WorldConfigurationBuilder()
                .with(new SpriteSystem(), new VelocitySystem())
                .build();
        world = new World(config);
        world.getAspectSubscriptionManager().get(Aspect.all(SpriteComponent.class));
        mapSprite = world.getMapper(SpriteComponent.class);
        mapTransform = world.getMapper(TransformComponent.class);
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

        Animation<Sprite> test2 = SpriteHelper.createAnimation(
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
        int entityTest2 = world.create(testType);
        mapAnimation.get(entityTest).animation = test;
        mapVelocity.get(entityTest).velocity = new Vector2(20f,0f);
        mapTransform.get(entityTest).position = new Vector3(0f, 150f, 2f);
        mapTransform.get(entityTest).scale = new Vector2(10f, 10f);

        mapAnimation.get(entityTest2).animation = test2;
        mapVelocity.get(entityTest2).velocity = new Vector2(-20f,0f);
        mapTransform.get(entityTest2).position = new Vector3(300f, 150f, 1f);
        mapTransform.get(entityTest2).scale = new Vector2(10f, 10f);



        testMap = Assets.getInstance().get("map1.tmx", TiledMap.class);
        mapRenderer = new OrthogonalTiledMapRenderer(testMap, 1);
       //batch.getTransformMatrix().setToOrtho(0, 300,0,300,0,100);
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
        mapRenderer.setView(batch.getProjectionMatrix(),0,0,600,600);
        mapRenderer.render();
        batch.begin();
        IntBag entities = subscription.getEntities();
        for(int e : entities.getData()){
            SpriteComponent spriteComponent = mapSprite.get(e);
            TransformComponent transformComponent = mapTransform.get(e);
            Vector3 translation = new Vector3(0,0,0);
            batch.getProjectionMatrix().getTranslation(translation);
            batch.getProjectionMatrix().setTranslation(translation.x, translation.y, transformComponent.position.z);
            spriteComponent.sprite.draw(batch);
            batch.getProjectionMatrix().setTranslation(translation);

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
