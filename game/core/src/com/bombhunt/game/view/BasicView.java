package com.bombhunt.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.bombhunt.game.BombHunt;

public abstract class BasicView extends InputAdapter {

    protected BombHunt bombHunt;

    private final String SKIN_PATH = "skin/craftacular-ui.json";
    protected Skin skin;

    public BasicView(BombHunt bombHunt) {
        this.bombHunt = bombHunt;
        skin = new Skin(Gdx.files.internal(SKIN_PATH));
    }

    public abstract void update(float dt);

    public abstract void render();

    public void dispose() {
        skin.dispose();
    }

    public InputProcessor getInputProcessor() {
        return null;
    }

    protected void clearBackground() {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    protected TextButton createButton(String text, ChangeListener listener) {
        TextButton button = new TextButton(text, skin, "default");
        button.setTransform(true);
        button.addListener(listener);
        return button;
    }

    protected void addReturnButton(Table table, ChangeListener listener, int colspan) {
        TextButton btnReturn = createButton("Back", listener);
        table.add(btnReturn).colspan(colspan).expandX();
    }
}
