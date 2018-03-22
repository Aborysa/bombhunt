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
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
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

import java.util.HashMap;


public class GameScreen extends InputAdapter implements IView{

    private World world;

    public static int TPS = 120;

    private float accTime = 0;
    EntitySubscription subscription;

    private SpriteBatch batch;
    private ComponentMapper<SpriteComponent> mapSprite;

    private OrthogonalTiledMapRenderer mapRenderer;
    private ComponentMapper<AnimationComponent> mapAnimation;
    private Camera currentCamera;
    ComponentMapper<TransformComponent> mapTransform;

    private TiledMap testMap;


    private HashMap<Integer, Boolean> keysDown = new HashMap<Integer, Boolean>(20);


    public GameScreen(){
        System.out.println("Creating gamescreen");
        currentCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        currentCamera.near = -10000;
        currentCamera.far = 10000;
        batch = new SpriteBatch();


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
        mapTransform.get(entityTest).position = new Vector3(0f, 150f, 1f);
        mapTransform.get(entityTest).scale = new Vector2(10f, 10f);

        mapAnimation.get(entityTest2).animation = test2;
        mapVelocity.get(entityTest2).velocity = new Vector2(-20f,0f);
        mapTransform.get(entityTest2).position = new Vector3(300f, 150f, 100f);
        mapTransform.get(entityTest2).scale = new Vector2(10f, 10f);



        testMap = Assets.getInstance().get("map1.tmx", TiledMap.class);
        mapRenderer = new OrthogonalTiledMapRenderer(testMap, 1);
        //batch.getTransformMatrix().setToOrtho(0, 300,0,300,0,100);


        //currentCamera.lookAt(new Vector3(0f,5f,2f));
        currentCamera.update();
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
        Vector3 camVec = new Vector3(0, 0 ,0);


        if(keysDown.containsKey(Input.Keys.RIGHT) && keysDown.get(Input.Keys.RIGHT)){
            camVec.x += 1;
        }
        if(keysDown.containsKey(Input.Keys.LEFT) && keysDown.get(Input.Keys.LEFT)){
            camVec.x -= 1;
        }
        if(keysDown.containsKey(Input.Keys.UP) && keysDown.get(Input.Keys.UP)){
            camVec.y += 1;
        }
        if(keysDown.containsKey(Input.Keys.DOWN) && keysDown.get(Input.Keys.DOWN)){
            camVec.y -= 1;
        }

        camVec.nor();

        currentCamera.translate(camVec.scl(100*dtime));
        currentCamera.update();


    }

    @Override
    public void render(){
        //mapRenderer.setView(batch.getProjectionMatrix(),0,0,600,600);
        //mapRenderer.render();
        batch.setProjectionMatrix(currentCamera.combined);
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

    @Override
    public boolean keyDown(int key){
        keysDown.put(key, true);
        return true;
    }

    @Override
    public boolean keyUp(int key){
        keysDown.put(key, false);
        return true;
    }


}
