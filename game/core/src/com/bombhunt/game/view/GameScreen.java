package com.bombhunt.game.view;

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
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.bombhunt.game.box2d.Collision;
import com.bombhunt.game.ecs.components.AnimationComponent;
import com.bombhunt.game.ecs.components.SpriteComponent;
import com.bombhunt.game.ecs.components.TransformComponent;
import com.bombhunt.game.ecs.factories.CrateFactory;
import com.bombhunt.game.ecs.factories.IEntityFactory;
import com.bombhunt.game.ecs.systems.PhysicsSystem;
import com.bombhunt.game.ecs.systems.SpriteSystem;
import com.bombhunt.game.utils.Assets;
import com.bombhunt.game.utils.Joystick;
import com.bombhunt.game.utils.level.*;

import java.util.HashMap;


public class GameScreen extends InputAdapter implements IView{

  private World world;
  private com.badlogic.gdx.physics.box2d.World box2d;
  public static int TPS = 64;

  private float accTime = 0;
  EntitySubscription subscription;

  private DecalBatch batch;
  private OrthogonalTiledMapRenderer mapRenderer;
  private Box2DDebugRenderer box2DDebugRenderer;


  private ComponentMapper<SpriteComponent> mapSprite;

  private ComponentMapper<AnimationComponent> mapAnimation;
  private OrthographicCamera currentCamera;
  ComponentMapper<TransformComponent> mapTransform;

  private TiledMap testMap;


  private HashMap<Integer, Boolean> keysDown = new HashMap<Integer, Boolean>(20);


  private float zoom = 1;
  // Temporary map for factories, may want to use injection in the future with @Wire
  private HashMap<String, IEntityFactory> factoryMap;
  private Level level;

  private int tick = 0;
  private float gameTime = 0;

  private Joystick joystick;
  private Stage stage;

  public GameScreen(){
    System.out.println("Creating gamescreen");

    // Create a camera
    currentCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    currentCamera.position.set(new Vector3(0,0,0f));

    level = Assets.getInstance().get("maps/map1.tmx", Level.class);

    // Set up batch
    batch = new DecalBatch(100000, new CameraGroupStrategy(currentCamera));
    box2DDebugRenderer = new Box2DDebugRenderer(true, false, false, false, false, true);


    // Set the camera's max depth
    currentCamera.far = 1000000f;

    factoryMap = new HashMap<String, IEntityFactory>(){{
      put(CrateFactory.class.getSimpleName(), new CrateFactory());
    }};



    box2d = new com.badlogic.gdx.physics.box2d.World(level.getDim(), true);

    box2d.setGravity(new Vector2(0, 0));
    Collision.world = box2d;
    // Set up ECS world
    WorldConfiguration config = new WorldConfigurationBuilder()
        .with(new SpriteSystem(), new PhysicsSystem(box2d))
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

    level.createEntities(factoryMap);
    level.createCollisionBodies(box2d);

    // Initial update of camera
    
    currentCamera.position.set(level.getDim().scl(0.5f), 0f);

    currentCamera.update();


    // Set up joystick
    joystick = new Joystick(20,20);
    stage = new Stage();
    stage.addActor(joystick);
  }


  @Override
  public void update(float dtime){

    // Accumelate time
    accTime += dtime;
    accTime = Math.min(accTime, 1);
    // Set delta to match tps
    world.setDelta((1f/TPS));
    // While we got ticks to process, process ticks
    gameTime += dtime;
    while(accTime >= 1f/TPS){
      box2d.step(1f/TPS, 6, 4);
      world.process();
      // Subtrack the tick delta from accumelated time
      accTime -= 1f/TPS;
      tick++;
      if(tick % TPS == 0){
        System.out.println(Gdx.graphics.getFramesPerSecond() + " : " + tick + " : " + tick/gameTime);
      }

    }

    // Temp code for moving camera
    Vector3 camVec = new Vector3(0, 0 ,0);
    Vector3 rot = new Vector3(0, 0, 0);
    if(keysDown.getOrDefault(Input.Keys.RIGHT, false)){
      camVec.x += 1;
    }
    if(keysDown.getOrDefault(Input.Keys.LEFT, false)){
      camVec.x -= 1;
    }
    if(keysDown.getOrDefault(Input.Keys.UP, false)){
      camVec.y += 1;
    }
    if(keysDown.getOrDefault(Input.Keys.DOWN, false)){
      camVec.y -= 1;
    }

    if(keysDown.getOrDefault(Input.Keys.W, false)){
      zoom -= dtime;
    }
    if(keysDown.getOrDefault(Input.Keys.S, false)){
      zoom += dtime;
    }

    // Get the normal vec from the movement input
    camVec.nor();

    rot.nor();
    //camRot.add(rot.scl(dtime));

    // Move camera
    currentCamera.translate(camVec.scl(300*dtime));
    currentCamera.zoom = zoom;
    //currentCamera.rotate(rot, 1*dtime);

    currentCamera.update();

    stage.act(dtime);
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
    // Flush all sprites
    batch.flush();
    box2DDebugRenderer.render(box2d, currentCamera.combined.cpy().scl(Collision.box2dToWorld));

    stage.draw();
  }

  @Override
  public void dispose(){
    world.dispose();
    box2DDebugRenderer.dispose();
    box2d.dispose();
    batch.dispose();
  }

  @Override
  public InputProcessor getInputProcessor() {
      return stage;
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
