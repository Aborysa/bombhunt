package com.bombhunt.game.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.bombhunt.game.BombHunt;
import com.bombhunt.game.view.BasicView;

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

    Animation animation;
    float elapsedTime;
    float angle;
    float last_color_change;

    TextureAtlas atlas;
    ArrayList<String> list_colors;


    public MainMenuScreen(BombHunt bombHunt) {
        super(bombHunt);
        atlas = new TextureAtlas("colorbomb/colorbomb.pack");
        animation = new Animation<TextureRegion>(0.05f,
                atlas.findRegions("red"), Animation.PlayMode.LOOP);

        list_colors = new ArrayList<String>();
        list_colors.add("red");
        list_colors.add("green");
        list_colors.add("blue");
        list_colors.add("fushia");
        list_colors.add("yellow");

        Table main_table = feedMainTable();
        setTable(main_table);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        batch.dispose();
        atlas.dispose();
    }

    private ChangeListener createQuitListener(BasicView current_view, BombHunt bombHunt) {
        ChangeListener listener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                current_view.dispose();
                System.exit(-1);
            }
        };
        return listener;
    }

    private Table feedMainTable() {
        Label title = new Label("Bomb Hunt", skin, "title");
        TextButton btnPlay = createButton("Play",
                createChangeListener(this, GameScreen.class));
        TextButton btnSettings = createButton("Settings",
                createChangeListener(this, SettingsScreen.class));
        TextButton btnCredits = createButton("Credits",
                createChangeListener(this, CreditsScreen.class));
        TextButton btnQuit = createButton("Quit",
                createQuitListener(this, bombHunt));
        String course = "TDT4240 - Software Architecture (NTNU)";
        String copyright = "(C) Cabral Cruz, Claessens, Ihlen, Trollebo 2018";
        Label copyright_label = new Label(course + "\n" + copyright, skin, "xp");
        Group group = new Group();
        group.addActor(copyright_label);
        group.setScale(0.5f, 0.5f);

        Table table = new Table();
        table.add(group).top().padTop(40).right().padRight(50).expandY().row();
        table.bottom();
        table.add(title).colspan(3).row();
        table.add(btnPlay).expandX().center().padBottom(10);
        table.add(btnSettings).expandX().center().padBottom(10).row();
        table.add(btnCredits).expandX().center().padBottom(10);
        table.add(btnQuit).expandX().padBottom(10).row();
        table.setFillParent(true);
        return table;
    }

    @Override
    void updateMovingBackgroundPosition() {
        float dt = Gdx.graphics.getDeltaTime();
        elapsedTime += dt;
        last_color_change += dt;
        angle += 1;
        Random rnd = new Random();
        float new_color_obs = rnd.nextFloat();
        if (new_color_obs < 0.0025*last_color_change) {
            String new_color = list_colors.get(rnd.nextInt(list_colors.size()));
            animation = new Animation<TextureRegion>(0.05f,
                    atlas.findRegions(new_color), Animation.PlayMode.LOOP);
            last_color_change = 0;
        }
    }

    @Override
    void drawMovingBackground(SpriteBatch batch) {
        TextureRegion frame = (TextureRegion) animation.getKeyFrame(elapsedTime);

        Sprite sprite = new Sprite();
        sprite.setRegion(frame);
        sprite.setSize((int) 2*Gdx.graphics.getHeight(),
                (int) 2*Gdx.graphics.getHeight());
        sprite.setPosition(0, -Gdx.graphics.getHeight()/2);
        sprite.setOrigin(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight());
        sprite.setRotation(angle);

        sprite.draw(batch);
/*
        batch.draw(sprite,
                0,
                -Gdx.graphics.getHeight()/2,
                (int) 2*Gdx.graphics.getHeight(),
                (int) 2*Gdx.graphics.getHeight());
                */
    }
}
