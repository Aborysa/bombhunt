package com.bombhunt.game.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.bombhunt.game.BombHunt;
import com.bombhunt.game.view.BasicView;

/**
 * Created by samuel on 28/03/18.
 * reference: https://stackoverflow.com/questions/15484077
 * reference: https://stackoverflow.com/questions/27577976
 */

public class MovingBackgroundScreen extends BasicView {

    private final int OFFSET_BACKGROUND_STEP_X = 2;
    private final int OFFSET_BACKGROUND_STEP_Y = 2;
    private final int SCALE = 800;
    private final String SKIN_PATH = "skin/craftacular-ui.json";

    protected Camera camera;
    protected Stage stage;
    protected Skin skin;

    private SpriteBatch batch;
    private Texture background;
    private int offsetBackgroundX = 0;
    private int offsetBackgroundY = 0;

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

    private void updateMovingBackgroundPosition() {
        offsetBackgroundX = (offsetBackgroundX + OFFSET_BACKGROUND_STEP_X) % background.getWidth();
        offsetBackgroundY = (offsetBackgroundY + OFFSET_BACKGROUND_STEP_Y) % background.getHeight();
    }

    private void drawMovingBackground(SpriteBatch batch) {
        batch.draw(background, -offsetBackgroundX, -offsetBackgroundY,
                0, 0,
                Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(background, background.getWidth()-offsetBackgroundX, -offsetBackgroundY,
                0, 0,
                Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(background, -offsetBackgroundX, background.getHeight()-offsetBackgroundY,
                0, 0,
                Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(background, background.getWidth()-offsetBackgroundX, background.getHeight()-offsetBackgroundY,
                0, 0,
                Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

}
