package com.bombhunt.game.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.bombhunt.game.BombHunt;
import com.bombhunt.game.view.BasicView;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by samuel on 28/03/18.
 * reference: https://stackoverflow.com/questions/15484077
 * reference: https://stackoverflow.com/questions/27577976
 * reference: https://stackoverflow.com/questions/3574065
 */

public abstract class MovingBackgroundScreen extends BasicView {

    protected final int OFFSET_BACKGROUND_STEP_X = 2;
    protected final int OFFSET_BACKGROUND_STEP_Y = 2;

    private final int SCALE = 800;
    private final String SKIN_PATH = "skin/craftacular-ui.json";

    protected Camera camera;
    protected Stage stage;
    protected Skin skin;

    protected SpriteBatch batch;
    protected Texture background;
    protected int offsetBackgroundX = 0;
    protected int offsetBackgroundY = 0;

    public MovingBackgroundScreen(BombHunt bombHunt) {
        super(bombHunt);
        camera = new OrthographicCamera(SCALE,
                SCALE * (Gdx.graphics.getHeight() / (float) Gdx.graphics.getWidth()));
        stage = new Stage(new StretchViewport(camera.viewportWidth, camera.viewportHeight));
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal(SKIN_PATH));
        batch = new SpriteBatch();
    }

    public void setBackground(Texture background) {
        this.background = background;
        this.background.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
    }

    public void setTable(Table table) {
        stage.addActor(table);
    }

    @Override
    public void update(float dtime) {
        updateMovingBackgroundPosition();
    }

    @Override
    public void render() {
        clearBackground();
        batch.begin();
        drawMovingBackground(batch);
        batch.end();
        stage.act();
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        batch.dispose();
        background.dispose();
    }

    @Override
    public InputProcessor getInputProcessor() {
        return stage;
    }

    abstract void updateMovingBackgroundPosition();

    abstract void drawMovingBackground(SpriteBatch batch);

    protected TextButton createButton(String text, ChangeListener listener) {
        TextButton button = new TextButton(text, skin, "default");
        button.setTransform(true);
        button.addListener(listener);
        return button;
    }

    protected ChangeListener createChangeListener(BasicView current_view, Class new_view_class) {
        ChangeListener listener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    Constructor<?> cons = new_view_class.getConstructor(BombHunt.class);
                    BasicView new_view = (BasicView) cons.newInstance(bombHunt);
                    changeView(current_view, (BasicView) new_view);
                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        };
        return listener;
    }

    protected void addReturnButton(Table table, int colspan) {
        TextButton btnReturn = createButton("Back",
                createChangeListener(this, MainMenuScreen.class));
        table.add(btnReturn).colspan(colspan).expandX();
    }

}
