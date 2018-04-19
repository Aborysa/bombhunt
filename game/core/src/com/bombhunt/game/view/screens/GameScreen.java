package com.bombhunt.game.view.screens;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.EntitySubscription;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bombhunt.game.BombHunt;
import com.bombhunt.game.controller.GameController;
import com.bombhunt.game.model.Grid;
import com.bombhunt.game.model.Level;
import com.bombhunt.game.model.ecs.components.SpriteComponent;
import com.bombhunt.game.model.ecs.components.TransformComponent;
import com.bombhunt.game.model.ecs.factories.BombFactory;
import com.bombhunt.game.model.ecs.factories.CrateFactory;
import com.bombhunt.game.model.ecs.factories.ExplosionFactory;
import com.bombhunt.game.model.ecs.factories.IEntityFactory;
import com.bombhunt.game.model.ecs.factories.PlayerFactory;
import com.bombhunt.game.model.ecs.factories.WallFactory;
import com.bombhunt.game.model.ecs.systems.BombSystem;
import com.bombhunt.game.model.ecs.systems.DestroyableSystem;
import com.bombhunt.game.model.ecs.systems.ExplosionSystem;
import com.bombhunt.game.model.ecs.systems.GridSystem;
import com.bombhunt.game.model.ecs.systems.KillableSystem;
import com.bombhunt.game.model.ecs.systems.PhysicsSystem;
import com.bombhunt.game.model.ecs.systems.PlayerSystem;
import com.bombhunt.game.model.ecs.systems.SpriteSystem;
import com.bombhunt.game.model.ecs.systems.TimerSystem;
import com.bombhunt.game.services.assets.Assets;
import com.bombhunt.game.services.physic.Collision;
import com.bombhunt.game.view.BasicView;
import com.bombhunt.game.view.InGameSettings;
import com.bombhunt.game.view.controls.BombButton;
import com.bombhunt.game.view.controls.Joystick;
import com.bombhunt.game.view.controls.SettingsButton;

import java.util.HashMap;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class GameScreen extends BasicView {
    private static final int TPS = 64;
    private float zoom = 1f;
    private int tick = 0;
    private float accTime = 0;
    private float gameTime = 0;
    private float PADDING_TABLE_CONTROLS = 50;
    private float RATIO_WIDTH_CONTROLS = 0.16f;

    private World world;
    private GameController controller;
    private com.badlogic.gdx.physics.box2d.World box2d;

    private Box2DDebugRenderer box2DDebugRenderer;
    private ShapeRenderer ecsDebugRenderer;

    EntitySubscription subscription;
    private DecalBatch batch;
    private InputMultiplexer inputMux;


    private OrthographicCamera currentCamera;
    private Viewport viewport;

    // TODO: Temporary map for factories, may want to use injection in the future with @Wire
    private HashMap<String, IEntityFactory> factoryMap;
    
    private ComponentMapper<SpriteComponent> mapSprite;
    private ComponentMapper<TransformComponent> mapTransform;


    private Level level;
    private Grid grid;


    private HashMap<Integer, Boolean> keysDown = new HashMap<>(20);

    private Joystick joystick;
    private BombButton bombButton;
    private SettingsButton settingsButton;
    private Stage stage;

    private Decal mapDecals[];

    public GameScreen(BombHunt bombHunt) {
        feedFactoryMap();
        setUpWorld();
        setUpECS(bombHunt);
        setUpCamera();
        setUpBatching();
        setUpControls();
        setUpComponentMappers();
        setUpAspectSubscription();
        setUpInputProcessor();
        createMapEntities();
        createCollisionBodies();
        createPlayerEntities();
        initialUpdateCamera();
    }

    private void feedFactoryMap() {
        final String crateFactoryName = CrateFactory.class.getSimpleName();
        final String playerFactoryName = PlayerFactory.class.getSimpleName();
        final String wallFactoryName = WallFactory.class.getSimpleName();
        final String bombFactoryName = BombFactory.class.getSimpleName();
        final String explosionFactoryName = ExplosionFactory.class.getSimpleName();
        factoryMap = new HashMap<String, IEntityFactory>() {{
            put(crateFactoryName, new CrateFactory());
            put(playerFactoryName, new PlayerFactory());
            put(wallFactoryName, new WallFactory());
            put(bombFactoryName, new BombFactory());
            put(explosionFactoryName, new ExplosionFactory());
        }};
    }

    private void setUpWorld() {
        level = Assets.getInstance().get("maps/map1.tmx", Level.class);
        box2d = new com.badlogic.gdx.physics.box2d.World(level.getDim(), true);
        box2d.setGravity(new Vector2(0, 0));
        box2DDebugRenderer = new Box2DDebugRenderer(true, false, false,
                false, false, true);
        ecsDebugRenderer = new ShapeRenderer();
        Collision.world = box2d;
    }

    private void setUpECS(BombHunt bombHunt) {
        SpriteSystem spriteSystem = new SpriteSystem();
        PhysicsSystem physicsSystem = new PhysicsSystem(box2d);
        // TODO: why is the bomb factory has to be passed in argument?
        // TODO: cannot that be created into the constructor of each system respectively
        String bombFactoryName = BombFactory.class.getSimpleName();
        String explosionFactoryName = ExplosionFactory.class.getSimpleName();
        BombFactory bombFactory = (BombFactory) factoryMap.get(bombFactoryName);
        ExplosionFactory explosionFactory = (ExplosionFactory) factoryMap.get(explosionFactoryName);
        PlayerSystem playerSystem = new PlayerSystem(bombFactory);
        BombSystem bombSystem = new BombSystem(explosionFactory);
        ExplosionSystem explosionSystem = new ExplosionSystem(explosionFactory);
        TimerSystem timerSystem = new TimerSystem();
        GridSystem gridSystem = new GridSystem();
        DestroyableSystem destroyableSystem = new DestroyableSystem(box2d);
        KillableSystem killableSystem = new KillableSystem(box2d);
        WorldConfiguration config = new WorldConfigurationBuilder()
                .with(spriteSystem)
                .with(physicsSystem)
                .with(playerSystem)
                .with(bombSystem)
                .with(explosionSystem)
                .with(timerSystem)
                .with(gridSystem)
                .with(destroyableSystem)
                .with(killableSystem)
                .build();
        world = new World(config);
        for (IEntityFactory factory : factoryMap.values()) {
            factory.setWorld(world);
        }
        grid = level.createGrid(world);
        for(IEntityFactory factory : factoryMap.values()) {
            factory.setGrid(grid);
        }
        // TODO: should be done in first place into the root constructor
        // TODO: SET UP ECS should not be done in the interface
        controller = new GameController(bombHunt, playerSystem);
    }

    private void setUpCamera() {
        MapProperties mapProperties = level.getMap().getProperties();
        int tilePixelWidth = mapProperties.get("tilewidth", Integer.class);
        int tilePixelHeight = mapProperties.get("tileheight", Integer.class);
        int x_tile_desired = 10;
        int y_tile_desired = 6;
        currentCamera = new OrthographicCamera(x_tile_desired*tilePixelWidth,
                y_tile_desired*tilePixelHeight);
        currentCamera.position.set(new Vector3(0, 0, 0f));
        currentCamera.far = 10000f;
        viewport = new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), currentCamera);
    }

    private void setUpBatching() {
        batch = new DecalBatch(4096, new CameraGroupStrategy(currentCamera));
    }

    private void setUpControls() {
        setUpJoystick();
        setUpBombButton();
        setUpSettingsButton();
        Table table = feedControlsTable();
        table = addControlsToTable(table);
        stage = new Stage();
        stage.addActor(table);
    }

    private void setUpJoystick() {
        int size_joystick = (int) (Gdx.graphics.getWidth() * RATIO_WIDTH_CONTROLS);
        joystick = new Joystick(size_joystick);
        joystick.getTouchpad().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Vector2 orientation = new Vector2(joystick.getTouchpad().getKnobPercentX(),
                        joystick.getTouchpad().getKnobPercentY());
                controller.playerMove(orientation);
            }
        });
    }

    private void setUpBombButton() {
        int size = (int) (Gdx.graphics.getWidth() * RATIO_WIDTH_CONTROLS);
        bombButton = new BombButton(size);
        bombButton.getImageButton().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.playerPlantBomb();
            }
        });
    }

    private void setUpSettingsButton() {
        int size = (int) (Gdx.graphics.getWidth() * RATIO_WIDTH_CONTROLS/2);
        settingsButton = new SettingsButton(size);
        settingsButton.getImageButton().addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                InGameSettings inGameSettings = new InGameSettings(skin, controller.getBombHunt());
                inGameSettings.show(stage);
                Assets asset_manager = Assets.getInstance();
                Sound popUpSound = asset_manager.get("popUp.wav", Sound.class);
                controller.playSound(popUpSound, 1);
            }
        });
    }

    private Table feedControlsTable() {
        Table table = new Table();
        table.setFillParent(true);
        table.pad(PADDING_TABLE_CONTROLS);
        return table;
    }

    private Table addControlsToTable(Table table) {
        table.add(settingsButton.getImageButton()).colspan(2).expand().right().top().row();
        table.bottom();
        table.add(joystick.getTouchpad()).left().expandX();
        table.add(bombButton.getImageButton()).right();
        return table;
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
        java.util.List<Decal> decals = level.createDecals();
        mapDecals = decals.toArray(new Decal[decals.size()]);
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
        updateClock(dt);
        processTicks(dt);
        updateCamera(dt);
        stage.act(dt);
    }

    private void updateClock(float dt) {
        accTime += dt;
        accTime = min(accTime, 1);
        world.setDelta((1f / TPS));
    }

    private void processTicks(float dt) {
        gameTime += dt;
        while (accTime >= 1f / TPS) {
            box2d.step(1f / TPS, 6, 4);
            world.process();
            accTime -= 1f / TPS;
            tick++;
            if (tick % TPS == 0) {
                //System.out.println(Gdx.graphics.getFramesPerSecond() + " : " + tick + " : " + tick / gameTime);
            }
        }
    }

    private void updateCamera(float dt) {
        currentCamera.position.set(moveCameraWithPlayer());
        //TODO: update zoom as the game time expire
        // currentCamera.zoom += dt/100;
        currentCamera.update();
    }

    private Vector3 moveCameraWithPlayer() {
        Vector3 mapDimensions = getMapDimensions();
        float min_x = currentCamera.viewportWidth/2;
        float min_y = currentCamera.viewportHeight/2;
        float max_x = mapDimensions.x-currentCamera.viewportWidth/2;
        float max_y = mapDimensions.y-currentCamera.viewportHeight/2;
        Vector3 currentPosition = controller.getPlayerPosition();
        currentPosition.x = min(max(currentPosition.x, min_x), max_x);
        currentPosition.y = min(max(currentPosition.y, min_y), max_y);
        currentPosition.z = 0;
        return currentPosition;
    }

    private Vector3 getMapDimensions() {
        MapProperties prop = level.getMap().getProperties();
        int mapWidth = prop.get("width", Integer.class);
        int mapHeight = prop.get("height", Integer.class);
        int tilePixelWidth = prop.get("tilewidth", Integer.class);
        int tilePixelHeight = prop.get("tileheight", Integer.class);
        return new Vector3(mapWidth*tilePixelWidth, mapHeight*tilePixelHeight, 0);
    }

    @Override
    public void render() {
        renderEntities();
        flushAllSprites();
        //box2DDebugRenderer.render(box2d, currentCamera.combined.cpy().scl(Collision.box2dToWorld));
        stage.draw();
    }

    private void renderEntities() {
        ecsDebugRenderer.setTransformMatrix(currentCamera.combined);
        ecsDebugRenderer.begin(ShapeType.Filled);
        IntBag entities = subscription.getEntities();
        for (int i = 0; i < entities.size(); i++) {
            int e = entities.get(i);
            SpriteComponent spriteComponent = mapSprite.get(e);
            batch.add(spriteComponent.sprite);
            if(mapTransform.has(e)){
                Vector3 pos = mapTransform.get(e).position;
                ecsDebugRenderer.circle(pos.x, pos.y, 8);
            }
        }
        for(Decal d : mapDecals){
            batch.add(d);
        }

        ecsDebugRenderer.end();

    }

    private void flushAllSprites() {
        batch.flush();
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

