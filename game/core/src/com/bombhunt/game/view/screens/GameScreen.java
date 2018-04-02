package com.bombhunt.game.view.screens;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.EntitySubscription;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.bombhunt.game.BombHunt;
import com.bombhunt.game.services.physic.Collision;
import com.bombhunt.game.model.ecs.components.AnimationComponent;
import com.bombhunt.game.model.ecs.components.SpriteComponent;
import com.bombhunt.game.model.ecs.components.TransformComponent;
import com.bombhunt.game.model.ecs.factories.BombFactory;
import com.bombhunt.game.model.ecs.factories.CrateFactory;
import com.bombhunt.game.model.ecs.factories.IEntityFactory;
import com.bombhunt.game.model.ecs.factories.PlayerFactory;
import com.bombhunt.game.model.ecs.systems.BombSystem;
import com.bombhunt.game.model.ecs.systems.ExplosionSystem;
import com.bombhunt.game.model.ecs.systems.PhysicsSystem;
import com.bombhunt.game.model.ecs.systems.PlayerInputSystem;
import com.bombhunt.game.model.ecs.systems.SpriteSystem;
import com.bombhunt.game.services.assets.Assets;
import com.bombhunt.game.view.Joystick;
import com.bombhunt.game.model.Level;
import com.bombhunt.game.view.BasicView;

import java.util.HashMap;

public class GameScreen extends BasicView {
    private static final int TPS = 64;
    private float zoom = 1;
    private int tick = 0;
    private float accTime = 0;
    private float gameTime = 0;

    private World world;
    private com.badlogic.gdx.physics.box2d.World box2d;
    private Box2DDebugRenderer box2DDebugRenderer;

    EntitySubscription subscription;
    private DecalBatch batch;
    private InputMultiplexer inputMux;
    private OrthographicCamera currentCamera;

    // TODO: Temporary map for factories, may want to use injection in the future with @Wire
    private HashMap<String, IEntityFactory> factoryMap;
    private ComponentMapper<SpriteComponent> mapSprite;
    private Level level;
    ComponentMapper<TransformComponent> mapTransform;
    // TODO: clean those... will this be necessary ?
    private OrthogonalTiledMapRenderer mapRenderer;
    private TiledMap testMap;
    private ComponentMapper<AnimationComponent> mapAnimation;

    private HashMap<Integer, Boolean> keysDown = new HashMap<>(20);

    private Joystick joystick;
    private Button bombButton;
    private Stage stage;

    public GameScreen(BombHunt bombHunt) {
        super(bombHunt);
        feedFactoryMap();
        setUpCamera();
        setUpBatching();
        setUpWorld();
        setUpControls();
        setUpECS();
        setUpComponentMappers();
        setUpAspectSubscription();
        setUpInputProcessor();
        createMapEntities();
        createCollisionBodies();
        createPlayerEntities();
        initialUpdateCamera();
    }

    private void feedFactoryMap() {
        String crateFactoryName = CrateFactory.class.getSimpleName();
        String playerFactoryName = PlayerFactory.class.getSimpleName();
        String bombFactoryName = BombFactory.class.getSimpleName();
        factoryMap = new HashMap<String, IEntityFactory>() {{
            put(crateFactoryName, new CrateFactory());
            put(playerFactoryName, new PlayerFactory());
            put(bombFactoryName, new BombFactory());
        }};
    }

    private void setUpCamera() {
        currentCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        currentCamera.position.set(new Vector3(0, 0, 0f));
        currentCamera.far = 1000000f;
    }

    private void setUpBatching() {
        batch = new DecalBatch(100000, new CameraGroupStrategy(currentCamera));
    }

    private void setUpWorld() {
        level = Assets.getInstance().get("maps/map1.tmx", Level.class);
        box2d = new com.badlogic.gdx.physics.box2d.World(level.getDim(), true);
        box2d.setGravity(new Vector2(0, 0));
        box2DDebugRenderer = new Box2DDebugRenderer(true, false, false,
                false, false, true);
        Collision.world = box2d;
    }

    private void setUpControls() {
        Table table = new Table();
        table.setDebug(true);
        table.setFillParent(true);
        // TODO: CONVERT PADDING AS CTE
        table.pad(50);
        // TODO : CONVERT THIS AS CTE
        int size_joystick = Gdx.graphics.getWidth()/6;
        joystick = new Joystick(size_joystick);

        // TODO: CREATE CLASS FOR BOMB BUTTON
        // TODO: make common base class having extract drawable from texture as common method
        // TODO: maybe resize too
        // TODO: second picture for bombButton when clicked
        Texture texture_up = Assets.getInstance().get("textures/bombButtonUp.png", Texture.class);
        Texture texture_down = Assets.getInstance().get("textures/bombButtonDown.png", Texture.class);
        Drawable drawable_up = new TextureRegionDrawable(new TextureRegion(texture_up));
        Drawable drawable_down = new TextureRegionDrawable(new TextureRegion(texture_down));
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        drawable_up.setMinWidth(size_joystick);
        drawable_up.setMinHeight(size_joystick);
        drawable_down.setMinWidth(size_joystick);
        drawable_down.setMinHeight(size_joystick);
        style.imageUp = drawable_up;
        style.imageDown = drawable_down;
        bombButton = new ImageButton(style);

        table.bottom();
        table.add(joystick.getTouchpad()).left().expandX();
        table.add(bombButton).right();
        stage = new Stage();
        stage.addActor(table);
    }

    private void setUpECS() {
        SpriteSystem spriteSystem = new SpriteSystem();
        PhysicsSystem physicsSystem = new PhysicsSystem(box2d);
        String bombFactoryName = BombFactory.class.getSimpleName();
        BombFactory bombFactory = (BombFactory) factoryMap.get(bombFactoryName);
        BombSystem bombSystem = new BombSystem(bombFactory);
        ExplosionSystem explosionSystem = new ExplosionSystem();
        PlayerInputSystem playerInputSystem = new PlayerInputSystem(box2d, joystick.getTouchpad(),
                bombButton, bombFactory);
        WorldConfiguration config = new WorldConfigurationBuilder()
                .with(spriteSystem, physicsSystem, playerInputSystem, bombSystem, explosionSystem)
                .build();
        world = new World(config);
        for (IEntityFactory factory : factoryMap.values()) {
            factory.setWorld(world);
        }
    }

    private void setUpComponentMappers() {
        mapSprite = world.getMapper(SpriteComponent.class);
        mapTransform = world.getMapper(TransformComponent.class);
    }

    private void setUpAspectSubscription() {
        subscription = world.getAspectSubscriptionManager().get(Aspect.all(SpriteComponent.class));
    }

    private void setUpInputProcessor() {
        inputMux = new InputMultiplexer(stage, this);
    }

    private void createMapEntities() {
        level.createEntities(factoryMap);
    }

    private void createCollisionBodies() {
        level.createCollisionBodies(box2d);
    }

    private void createPlayerEntities() {
        /*
        TextureRegion textureRegion = new TextureRegion(new Texture("textures/badlogic.jpg"));
        String playerFactoryName = PlayerFactory.class.getSimpleName()
        PlayerFactory playerFactory = (PlayerFactory) factoryMap.get(playerFactoryName);
        playerFactory.createPlayer(0, 0, Decal.newDecal(textureRegion));
        */
    }

    private void initialUpdateCamera() {
        currentCamera.position.set(level.getDim().scl(0.5f), 0f);
        currentCamera.update();
    }

    @Override
    public void update(float dt) {

        // Accumelate time
        accTime += dt;
        accTime = Math.min(accTime, 1);
        // Set delta to match tps
        world.setDelta((1f / TPS));
        // While we got ticks to process, process ticks
        gameTime += dt;
        while (accTime >= 1f / TPS) {
            box2d.step(1f / TPS, 6, 4);
            world.process();
            // Subtrack the tick delta from accumulated time
            accTime -= 1f / TPS;
            tick++;
            if (tick % TPS == 0) {
                System.out.println(Gdx.graphics.getFramesPerSecond() + " : " + tick + " : " + tick / gameTime);
            }
        }

        // Temp code for moving camera
        Vector3 camVec = new Vector3(0, 0, 0);
        Vector3 rot = new Vector3(0, 0, 0);
        if (keysDown.getOrDefault(Input.Keys.RIGHT, false)) {
            camVec.x += 1;
        }
        if (keysDown.getOrDefault(Input.Keys.LEFT, false)) {
            camVec.x -= 1;
        }
        if (keysDown.getOrDefault(Input.Keys.UP, false)) {
            camVec.y += 1;
        }
        if (keysDown.getOrDefault(Input.Keys.DOWN, false)) {
            camVec.y -= 1;
        }

        // Get the normal vec from the movement input
        camVec.nor();

        rot.nor();
        //camRot.add(rot.scl(dtime));

        // Move camera
        currentCamera.translate(camVec.scl(300 * dt));
        currentCamera.zoom = zoom;
        //currentCamera.rotate(rot, 1*dtime);

        currentCamera.update();

        stage.act(dt);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Grab entities with sprites
        IntBag entities = subscription.getEntities();

        // Iterate over entities to be rendered
        for (int i = 0; i < entities.size(); i++) {
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
    public void dispose() {
        world.dispose();
        box2DDebugRenderer.dispose();
        box2d.dispose();
        batch.dispose();
    }

    @Override
    public InputProcessor getInputProcessor() {
        return inputMux;
    }

    @Override
    public boolean keyDown(int key) {
        keysDown.put(key, true);
        return true;
    }

    @Override
    public boolean keyUp(int key) {
        keysDown.put(key, false);
        return true;
    }

}

