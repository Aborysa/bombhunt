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
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.decals.SimpleOrthoGroupStrategy;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.bombhunt.game.ecs.components.AnimationComponent;
import com.bombhunt.game.ecs.components.SpriteComponent;
import com.bombhunt.game.ecs.components.TransformComponent;
import com.bombhunt.game.ecs.components.VelocityComponent;
import com.bombhunt.game.ecs.factories.CrateFactory;
import com.bombhunt.game.ecs.factories.IEntityFactory;
import com.bombhunt.game.ecs.systems.SpriteSystem;
import com.bombhunt.game.ecs.systems.VelocitySystem;
import com.bombhunt.game.utils.Assets;
import com.bombhunt.game.utils.level.*;
import com.bombhunt.game.utils.SpriteHelper;

import java.util.HashMap;
import java.util.Vector;


public class GameScreen extends InputAdapter implements IView{

  private World world;

  public static int TPS = 128;

  private float accTime = 0;
  EntitySubscription subscription;

  private DecalBatch batch;
  private OrthogonalTiledMapRenderer mapRenderer;


  private ComponentMapper<SpriteComponent> mapSprite;

  private ComponentMapper<AnimationComponent> mapAnimation;
  private OrthographicCamera currentCamera;
  ComponentMapper<TransformComponent> mapTransform;

  private TiledMap testMap;


  private HashMap<Integer, Boolean> keysDown = new HashMap<Integer, Boolean>(20);


  private Vector3 camRot = new Vector3(0, 0,0 );

  // Temporary map for factories, may want to use injection in the future with @Wire
  private HashMap<String, IEntityFactory> factoryMap;
  private Level level;
  public GameScreen(){
    System.out.println("Creating gamescreen");

    // Create a camera
    currentCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    currentCamera.position.set(new Vector3(0,0,0f));

    // Set up batch
    batch = new DecalBatch(100000, new CameraGroupStrategy(currentCamera));



    // Set the camera's max depth
    currentCamera.far = 1000000f;

    factoryMap = new HashMap<String, IEntityFactory>(){{
      put(CrateFactory.class.getSimpleName(), new CrateFactory());
    }};
    // Set up ECS world
    WorldConfiguration config = new WorldConfigurationBuilder()
        .with(new SpriteSystem(), new VelocitySystem())
        .build();

    world = new World(config);

    for(IEntityFactory factory : factoryMap.values()){
      factory.setWorld(world);
    }

    //Set up component mappers

    mapSprite = world.getMapper(SpriteComponent.class);
    mapTransform = world.getMapper(TransformComponent.class);

    // Set up aspect subscription for rendering
    subscription = world.getAspectSubscriptionManager().get(Aspect.all(SpriteComponent.class));

    level = new Level(Assets.getInstance().get("maps/map1.tmx", TiledMap.class), world, factoryMap);

    level.create();
    // Initial update of camera
    mapRenderer = new OrthogonalTiledMapRenderer(level.getMap());

    currentCamera.position.set(level.getDim().scl(0.5f), 0f);

    currentCamera.update();
  }


  @Override
  public void update(float dtime){

    // Accumelate time
    accTime += dtime;

    // Set delta to match tps
    world.setDelta(1f/TPS);

    // While we got ticks to process, process ticks
    while(accTime >= 1f/TPS){
      world.process();
      // Subtrack the tick delta from accumelated time
      accTime -= 1f/TPS;
    }

    // Temp code for moving camera
    Vector3 camVec = new Vector3(0, 0 ,0);
    Vector3 rot = new Vector3(0, 0, 0);
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

    if(keysDown.containsKey(Input.Keys.W) && keysDown.get(Input.Keys.W)){
      camVec.z += 1;
    }
    if(keysDown.containsKey(Input.Keys.S) && keysDown.get(Input.Keys.S)){
      camVec.z -= 1;
    }
  /*  if(keysDown.containsKey(Input.Keys.A) && keysDown.get(Input.Keys.A)){
      rot.z += 1;
    }
    if(keysDown.containsKey(Input.Keys.D) && keysDown.get(Input.Keys.D)){
      rot.z -= 1;
    }
*/
    // Get the normal vec from the movement input
    camVec.nor();

    rot.nor();
    //camRot.add(rot.scl(dtime));

    // Move camera
    currentCamera.translate(camVec.scl(300*dtime));

    //currentCamera.rotate(rot, 1*dtime);
    Vector2 dim = level.getDim();
    Vector3 lookat = new Vector3(dim.x/2, dim.y/2, -200);

    currentCamera.lookAt(lookat);

    currentCamera.update();
    //mapRenderer.setView(currentCamera.combined.cpy().translate(0, 0, -100f),16,16,16*32,16*32);
    mapRenderer.setView(currentCamera);


  }

  @Override
  public void render(){
    // Grab entities with sprites
    IntBag entities = subscription.getEntities();

    // Iterate over entities to be rendered
    for(int i = 0; i < entities.size(); i++){
      int e = entities.get(i);
      // Grab the sprite and add it to the decal batch
      SpriteComponent spriteComponent = mapSprite.get(e);
      batch.add(spriteComponent.sprite);
    }
    //mapRenderer.render();
    // Flush all sprites
    batch.flush();
  }

  @Override
  public void dispose(){
    world.dispose();
    batch.dispose();
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
