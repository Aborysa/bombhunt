package com.bombhunt.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
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

    public abstract void update(float dtime);

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
        InputListener sound_listener = createSoundListener();
        button.addListener(sound_listener);
        return button;
    }

    protected void addReturnButton(Table table, ChangeListener listener, int colspan) {
        TextButton btnReturn = createButton("Back", listener);
        table.add(btnReturn).colspan(colspan).expandX();
    }

    protected ChangeListener bonifySoundListener(ChangeListener listener) {
        ChangeListener bonified_listener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Sound sound = Gdx.audio.newSound(Gdx.files.internal("digitalButton.mp3"));
                sound.play(1f);
                listener.changed(event, actor);
            }
        };
        return bonified_listener;
    }

    private InputListener createSoundListener() {
        InputListener listener = new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                bombHunt.audioPlayer.playButtonSound();
            }
            /*

            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                bombHunt.audioPlayer.playButtonSound();
                return false;
            }

            */
        };
        return listener;
    }
}
