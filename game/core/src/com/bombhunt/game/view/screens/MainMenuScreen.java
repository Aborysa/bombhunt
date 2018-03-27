package com.bombhunt.game.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.bombhunt.game.view.IView;

public class MainMenuScreen extends InputAdapter implements IView {
    private Camera camera;
    private Stage stage;
    private Table table;
    private Skin skin;

    private TextButton btnPlay;
    private TextButton btnSettings;
    private TextButton btnCredits;
    private TextButton btnQuit;

    private BitmapFont font;

    public MainMenuScreen() {
        camera = new OrthographicCamera(800,
                800 * (Gdx.graphics.getHeight() / (float) Gdx.graphics.getWidth()));
        stage = new Stage(new StretchViewport(camera.viewportWidth, camera.viewportHeight));
        table = new Table();
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("skin/craftacular-ui.json"));

        btnPlay = createButton("Play");
        btnSettings = createButton("Settings");
        btnCredits = createButton("Credits");
        btnQuit = createButton("Quit");

        Label test = new Label("test", skin, "title");

        table.add(test).center().row();
        table.add(btnPlay).expandX().center().padBottom(10);
        table.add(btnSettings).expandX().center().padBottom(10).row();
        table.add(btnCredits).expandX().center().padBottom(10);
        table.add(btnQuit).expandX();
        table.setFillParent(true);
        stage.addActor(table);
    }

    @Override
    public void update(float dtime) {
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();

    }

    @Override
    public InputProcessor getInputProcessor() {
        return stage;
    }

    private TextButton createButton(String text) {
        TextButton button = new TextButton(text, skin, "default");
        button.setTransform(true);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("Button " + text + " Pressed");
            }
        });
        return button;
    }
}
