package com.bombhunt.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.bombhunt.game.services.assets.Assets;

public abstract class BasicView extends InputAdapter {

    protected Skin skin;

    public BasicView() {
        Assets assetManager = Assets.getInstance();
        skin = assetManager.get("skin/craftacular-ui.json", Skin.class);
    }

    public abstract void update(float dt);

    public abstract void render();

    public void dispose() {
        //skin.dispose();
    }

    public InputProcessor getInputProcessor() {
        return null;
    }

    protected void changeBackground(float red, float green, float blue, float alpha) {
        Gdx.gl.glClearColor(red, green, blue, alpha);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    protected void clearBackground() {
        changeBackground(0,0,0,0);
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
