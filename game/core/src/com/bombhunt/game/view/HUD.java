package com.bombhunt.game.view;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
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

    private final float SCALING_FACTOR = 2.5f;
    private final int TEXTURE_SIZE = 32;

    private GameController controller;

    private Stage stage;
    private Table table;
    private Map<STATS_ENUM, Group> groups;
    private float total_height;

    public HUD(GameController controller){
        this.controller = controller;
        Assets asset_manager = Assets.getInstance();
        Texture texture = asset_manager.get("items.png", Texture.class);
        Skin skin = asset_manager.get("skin/craftacular-ui.json", Skin.class);
        groups = new HashMap<>();
        Map<STATS_ENUM, Number> stats = controller.getPlayerStats();
        for(STATS_ENUM i : STATS_ENUM.values()) {
            try {
                ITEM_TYPE_ENUM item = ITEM_TYPE_ENUM.valueOf(String.valueOf(i));
                TextureRegion textureRegion = new TextureRegion(texture, item.getCoord_x()*TEXTURE_SIZE,
                        item.getCoord_y()*TEXTURE_SIZE,
                        TEXTURE_SIZE,
                        TEXTURE_SIZE);
                Image image = new Image(textureRegion);
                image.setName("IMAGE");
                image.setScale(SCALING_FACTOR);
                Label label = new Label(stats.get(i).toString(), skin, "xp");
                label.setName("LABEL");
                Group group = new Group();
                group.addActor(image);
                group.addActor(label);
                group.setHeight(image.getHeight()*SCALING_FACTOR);
                groups.put(i, group);
            } catch (IllegalArgumentException ignored) {
            }
        }
        table = new Table();
        table.defaults().uniformY();
        for (Map.Entry<STATS_ENUM, Group> group : groups.entrySet()) {
            Group value = group.getValue();
            total_height += value.getHeight();
            table.add(value).row();
        }
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
