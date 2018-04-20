package com.bombhunt.game.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.bombhunt.game.BombHunt;
import com.bombhunt.game.controller.MainMenuController;
import com.bombhunt.game.services.assets.Assets;
import com.bombhunt.game.services.networking.IPlayServices;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by samuel on 27/03/18.
 * references:
 * https://stackoverflow.com/questions/42822867/
 * http://www.gamefromscratch.com/post/2015/02/27/LibGDX-Video-Tutorial-Sprite-Animation.aspx
 * https://github.com/libgdx/libgdx/wiki/2D-Animation
 */

public class MainMenuScreen extends MovingBackgroundScreen {

    private final int ROTATION_ANGLE_INCREMENT = 50;
    private final float PROB_COLOR_CHANGE_FACTOR = 0.0025f;

    private MainMenuController controller;

    float elapsedTime;
    float angle;
    float last_color_change;
    ArrayList<String> list_colors;
    Random rnd;

    TextureAtlas atlas;
    Animation animation;
    Sprite sprite;
    private IPlayServices playServices;

    public MainMenuScreen(BombHunt bombHunt) {
        this.playServices = bombHunt.getPlayServices();
        controller = new MainMenuController(bombHunt);
        String theme_song = "heroism.ogg";
        controller.setNewThemeSong(theme_song);
        atlas = Assets.getInstance().get("colorbomb/colorbomb.pack", TextureAtlas.class);
        feedListColors();
        rnd = new Random();
        String init_color = selectRandomColor();
        setColorAnimation(init_color);
        initializeSprite();
        Table main_table = feedMainTable();
        setTable(main_table);
    }

    @Override
    public void dispose() {
        // IMPORTANT: super.dispose() will try to dispose texture that is not used here
        stage.dispose();
        skin.dispose();
        batch.dispose();
        //TODO: atlas.dispose();
    }

    private Table feedMainTable() {
        Table table = new Table();
        addCopyright(table);
        addTitle(table);
        addButtons(table);
        table.setFillParent(true);
        return table;
    }

    private void addCopyright(Table table) {
        String course =    "          TDT4240 - Software Architecture (NTNU)";
        String copyright = "(C) Cabral Cruz, Claessens, Ihlen, Trollebo 2018";
        Label copyright_label = new Label(course + "\n" + copyright, skin, "xp");
        Group group = new Group();
        group.addActor(copyright_label);
        group.setScale(0.5f, 0.5f);
        table.add(group).top().padTop(40).right().padRight(60).expandY().row();
    }

    private void addTitle(Table table) {
        Label title = new Label("Bomb Hunt", skin, "title");
        table.bottom();
        table.add(title).colspan(3).row();
    }

    private void addButtons(Table table) {
        ChangeListener listener;
        listener = controller.createViewTransitionWithSoundListener(WaitingRoomScreen.class);
        TextButton btnPlay = createButton("Find game", listener);
        listener = controller.createViewTransitionWithSoundListener(SettingsScreen.class);
        TextButton btnSettings = createButton("Settings", listener);
        listener = controller.createViewTransitionWithSoundListener(CreditsScreen.class);
        TextButton btnCredits = createButton("Credits", listener);
        listener = controller.createQuitWithSoundListener(this);
        TextButton btnQuit = createButton("Quit", listener);
        table.add(btnPlay).expandX().center().padBottom(10);
        table.add(btnSettings).expandX().center().padBottom(10).row();
        table.add(btnCredits).expandX().center().padBottom(10);
        table.add(btnQuit).expandX().padBottom(10).row();
    }

    @Override
    void updateMovingBackgroundPosition(float dt) {
        switchColorAnimation(dt);
        updateSprite(dt);
    }

    private void feedListColors() {
        list_colors = new ArrayList<>();
        list_colors.add("red");
        list_colors.add("green");
        list_colors.add("blue");
        list_colors.add("fushia");
        list_colors.add("yellow");
    }

    private void switchColorAnimation(float dt) {
        last_color_change += dt;
        float new_color_obs = rnd.nextFloat();
        if (new_color_obs < PROB_COLOR_CHANGE_FACTOR *last_color_change) {
            String new_color = selectRandomColor();
            setColorAnimation(new_color);
            last_color_change = 0;
        }
    }

    private String selectRandomColor() {
        return list_colors.get(rnd.nextInt(list_colors.size()));
    }

    private void setColorAnimation(String color) {
        animation = new Animation<TextureRegion>(0.05f,
                atlas.findRegions(color), Animation.PlayMode.LOOP);
    }

    private void initializeSprite() {
        sprite = new Sprite();
        sprite.setSize((int) 2*Gdx.graphics.getHeight(),
                (int) 2*Gdx.graphics.getHeight());
        sprite.setPosition(0, -Gdx.graphics.getHeight()/2);
        sprite.setOrigin(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight());
    }

    private void updateSprite(float dt) {
        elapsedTime += dt;
        updateFrame();
        rotateAnimation(dt);
    }

    private void updateFrame() {
        TextureRegion frame = (TextureRegion) animation.getKeyFrame(elapsedTime);
        sprite.setRegion(frame);
    }

    private void rotateAnimation(float dt) {
        angle += ROTATION_ANGLE_INCREMENT*dt;
        sprite.setRotation(angle);
    }

    @Override
    void drawMovingBackground(SpriteBatch batch) {
        sprite.draw(batch);
    }
}
