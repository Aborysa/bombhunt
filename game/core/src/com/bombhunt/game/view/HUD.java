package com.bombhunt.game.view;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.bombhunt.game.controller.GameController;
import com.bombhunt.game.model.ecs.factories.ITEM_TYPE_ENUM;
import com.bombhunt.game.model.ecs.systems.STATS_ENUM;
import com.bombhunt.game.services.assets.Assets;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by samuel on 20/04/18.
 */

public class HUD implements Disposable {

    private final float SCALING_FACTOR = 3f;
    private final int TEXTURE_SIZE = 32;

    private GameController controller;

    private Stage stage;
    private Table table;
    private Map<STATS_ENUM, Group> groups;
    private float total_height;

    public HUD(GameController controller){
        this.controller = controller;
        feedGroups();
        feedMainTable();
        feedStage();
    }

    private void feedGroups() {
        groups = new HashMap<>();
        for(STATS_ENUM i : STATS_ENUM.values()) {
            try {
                ITEM_TYPE_ENUM item = ITEM_TYPE_ENUM.valueOf(String.valueOf(i));
                Group group = processItem(i, item);
                groups.put(i, group);
            } catch (IllegalArgumentException ignored) {}
        }
    }

    private Group processItem(STATS_ENUM i, ITEM_TYPE_ENUM item) {
        Image image = createImage(item);
        Map<STATS_ENUM, Number> stats = controller.getPlayerStats();
        String text_label = stats.get(i).toString();
        Label label = createLabel(text_label);
        return createGroup(image, label);
    }

    private Image createImage(ITEM_TYPE_ENUM item) {
        Assets asset_manager = Assets.getInstance();
        Texture texture = asset_manager.get("items.png", Texture.class);
        int x_coord = item.getCoord_x()*TEXTURE_SIZE;
        int y_coord = item.getCoord_y()*TEXTURE_SIZE;
        TextureRegion textureRegion = new TextureRegion(texture,
                x_coord, y_coord, TEXTURE_SIZE, TEXTURE_SIZE);
        Image image = new Image(textureRegion);
        image.setName("IMAGE");
        image.setScale(SCALING_FACTOR);
        return image;
    }

    private Label createLabel(String text) {
        Assets asset_manager = Assets.getInstance();
        Skin skin = asset_manager.get("skin/craftacular-ui.json", Skin.class);
        Label label = new Label(text, skin, "xp");
        label.setName("LABEL");
        return label;
    }

    private Group createGroup(Image image, Label label) {
        Group group = new Group();
        group.addActor(image);
        group.addActor(label);
        group.setHeight(image.getHeight()*SCALING_FACTOR);
        return group;
    }

    private void feedMainTable() {
        table = new Table();
        table.defaults().uniformY();

        // TODO: health bar
        Assets asset_manager = Assets.getInstance();
        Skin skin = asset_manager.get("skin/craftacular-ui.json", Skin.class);
        Map<STATS_ENUM, Number> stats = controller.getPlayerStats();
        int max_health = (int) stats.get(STATS_ENUM.MAX_HEALTH);
        int health = (int) stats.get(STATS_ENUM.HEALTH);
        ProgressBar health_bar = new ProgressBar(0, max_health, 10, false, skin, "health");
        health_bar.setValue(health);
        // TODO: check spacing
        // total_height += health_bar.getHeight();
        table.add(health_bar).row();

        addGroupsToTable();
    }

    private void addGroupsToTable() {
        for (Map.Entry<STATS_ENUM, Group> group : groups.entrySet()) {
            Group value = group.getValue();
            total_height += value.getHeight();
            table.add(value).row();
        }
    }

    private void feedStage() {
        stage = new Stage();
        stage.addActor(table);
    }

    public float getTotalHeight() {
        return total_height;
    }

    public void setPosition(Vector3 position) {
        table.setPosition(position.x, position.y);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public void update(float dt) {
        Map<STATS_ENUM, Number> stats = controller.getPlayerStats();
        for (Map.Entry<STATS_ENUM, Group> group: groups.entrySet()) {
            STATS_ENUM key = group.getKey();
            Group value = group.getValue();
            Label label = value.findActor("LABEL");
            label.setText(stats.get(key).toString());
        }
        stage.act(dt);
    }

    public void render() {
        stage.setDebugAll(true);
        stage.draw();
    }
}
