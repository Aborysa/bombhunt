package com.bombhunt.game.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.bombhunt.game.BombHunt;
import com.bombhunt.game.view.BasicView;

/**
 * Created by samuel on 27/03/18.
 * reference: https://stackoverflow.com/questions/15484077
 * reference: https://stackoverflow.com/questions/27577976
 */

public class CreditsScreen extends BasicView {

    private SpriteBatch batch;
    private Texture background;
    private ScrollPane scroller;
    private int offsetBackgroundX = 0;
    private int offsetBackgroundY = 0;

    public CreditsScreen(BombHunt bombHunt) {
        super(bombHunt);

        batch = new SpriteBatch();
        background = new Texture(Gdx.files.internal("angryBombsBackground.png"));
        background.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        Table scroll_table = new Table();
        addTitle(scroll_table);
        addDevelopersToTable(scroll_table);

        // Should read content through files
        // developers
        // content TDT4240 - Software Architecture NTNU (date)
        // course staff
        // artists
        // Label developers = new Label("");

        scroller = new ScrollPane(scroll_table, skin, "default");

        Table table = new Table();
        table.setFillParent(true);
        table.add(scroller).top().fill().expand().row();
        stage.addActor(table);
    }

    @Override
    public void update(float dtime) {
        updateMovingBackgroundPosition();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        drawMovingBackground(batch);
        batch.end();
        stage.act();
        stage.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        background.dispose();
    }

    @Override
    public InputProcessor getInputProcessor() {
        return stage;
    }

    private void addTitle(Table table) {
        Label title = new Label("Credits", skin, "title");
        table.add(title).top().row();
    }

    private void addDevelopersToTable(Table table) {
        table.add(new Label("Developers", skin, "bold")).row();
        FileHandle file = Gdx.files.internal("developers.txt");
        String lines[] = file.readString().split("\\r?\\n");
        for (int i = 0; i < lines.length; i++) {
            table.add(new Label(lines[i], skin, "default")).row();
        }
    }

    private void updateMovingBackgroundPosition() {
        offsetBackgroundX = (offsetBackgroundX + 2) % background.getWidth();
        offsetBackgroundY = (offsetBackgroundY + 2) % background.getHeight();
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
