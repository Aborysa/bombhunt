package com.bombhunt.game.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.bombhunt.game.BombHunt;
import com.bombhunt.game.view.BasicView;

/**
 * Created by samuel on 28/03/18.
 * reference: https://stackoverflow.com/questions/15484077
 * reference: https://stackoverflow.com/questions/27577976
 * reference: https://stackoverflow.com/questions/3574065
 */

public abstract class MovingBackgroundScreen extends BasicView {

    protected final int OFFSET_BACKGROUND_STEP_X = 200;
    protected final int OFFSET_BACKGROUND_STEP_Y = 200;
    protected final int SCALE = 800;

    protected Camera camera;
    protected Stage stage;
    protected SpriteBatch batch;
    protected Texture background;
    protected int offsetBackgroundX = 0;
    protected int offsetBackgroundY = 0;

    public MovingBackgroundScreen() {
        camera = new OrthographicCamera(SCALE,
                SCALE * (Gdx.graphics.getHeight() / (float) Gdx.graphics.getWidth()));
        stage = new Stage(new StretchViewport(camera.viewportWidth, camera.viewportHeight));
        Gdx.input.setInputProcessor(stage);
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
    public void update(float dt) {
        updateMovingBackgroundPosition(dt);
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
        super.dispose();
        stage.dispose();
        batch.dispose();
    }

    @Override
    public InputProcessor getInputProcessor() {
        return stage;
    }

    abstract void updateMovingBackgroundPosition(float dt);

    abstract void drawMovingBackground(SpriteBatch batch);

}
