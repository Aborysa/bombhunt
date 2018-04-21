package com.bombhunt.game.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.bombhunt.game.controller.GameController;
import com.bombhunt.game.model.ecs.factories.ITEM_TYPE_ENUM;
import com.bombhunt.game.model.ecs.systems.STATS_ENUM;
import com.bombhunt.game.services.assets.Assets;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static com.badlogic.gdx.graphics.Pixmap.Format.RGB888;

/**
 * Created by samuel on 20/04/18.
 */

public class HUD implements Disposable {

    private final float SCALING_FACTOR = 3f;
    private final int MIN_HEALTH_INIT = 0;
    private final int MAX_HEALTH_INIT = 100;
    private final int HEALTH_INIT = 0;
    private final float HEIGHT_HEART = 16f;
    private final float WIDTH_HEARTH = 18f;
    private final float HP_PER_HEARTH = 10f;
    private final int TEXTURE_SIZE = 32;

    private GameController controller;

    private Stage stage;
    private Table health_bar_table;
    private Group health_bar_group;
    private ProgressBarStyle health_bar_style;
    private Table main_table;
    private Map<STATS_ENUM, Group> stats_groups;
    private float height_stats;

    public HUD(GameController controller){
        this.controller = controller;
        feedStats();
        feedMainTable();
        feedStage();
    }

    private void feedStats() {
        stats_groups = new HashMap<>();
        for(STATS_ENUM i : STATS_ENUM.values()) {
            try {
                ITEM_TYPE_ENUM item = ITEM_TYPE_ENUM.valueOf(String.valueOf(i));
                Group group = processItem(i, item);
                stats_groups.put(i, group);
            } catch (IllegalArgumentException ignored) {}
        }
    }

    private Group processItem(STATS_ENUM i, ITEM_TYPE_ENUM item) {
        Image image = createImage(item);
        Pixmap labelColor = new Pixmap((int) (image.getWidth()*SCALING_FACTOR), (int) (image.getHeight()*SCALING_FACTOR), RGB888);
        labelColor.setColor(Color.FIREBRICK);
        labelColor.fill();
        Image background = new Image(new Texture(labelColor));
        Map<STATS_ENUM, Number> stats = controller.getPlayerStats();
        String text_label = stats.get(i).toString();
        Label label = createLabel(text_label);
        return createGroup(background, image, label);
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
        Label label = new Label(text, skin, "dim");
        label.setFontScale(1.5f);
        label.setName("LABEL");
        return label;
    }

    private Group createGroup(Image background, Image image, Label label) {
        Group group = new Group();
        group.addActor(background);
        group.addActor(image);
        group.addActor(label);
        group.setHeight(image.getHeight()*SCALING_FACTOR);
        return group;
    }

    private void feedMainTable() {
        main_table = new Table();
        main_table.defaults().uniformY();
        addGroupsToTable();
    }

    private void addGroupsToTable() {
        // IMPORTANT: ordering stats entries before adding to table
        Map<STATS_ENUM, Group> ordered_stats = new TreeMap<>(stats_groups);
        for (Map.Entry<STATS_ENUM, Group> group : ordered_stats.entrySet()) {
            Group value = group.getValue();
            height_stats += value.getHeight();
            main_table.add(value).row();
        }
    }

    private void feedStage() {
        stage = new Stage();
        setUpHealthBarStyle();
        ProgressBar health_bar = createHealthBar(HEALTH_INIT, MIN_HEALTH_INIT, MAX_HEALTH_INIT);
        health_bar_group = new Group();
        health_bar_group.scaleBy(SCALING_FACTOR);
        addHealthBarToGroup(health_bar);
        health_bar_table = new Table();
        health_bar_table.add(health_bar_group).top().left().row();
        stage.addActor(health_bar_table);
        stage.addActor(main_table);
    }

    private void setUpHealthBarStyle() {
        Assets asset_manager = Assets.getInstance();
        Skin skin = asset_manager.get("skin/craftacular-ui.json", Skin.class);
        health_bar_style = skin.get("health", ProgressBarStyle.class);
        skin.getTiledDrawable("heart-bg").setMinWidth(0.0f);
        health_bar_style.background = skin.getTiledDrawable("heart-bg");
        skin.getTiledDrawable("heart").setMinWidth(0.0f);
        health_bar_style.knobBefore = skin.getTiledDrawable("heart");
    }

    private ProgressBar createHealthBar(int health, int min_health, int max_health) {
        ProgressBar health_bar = new ProgressBar(min_health, max_health, 1f, false, health_bar_style);
        health_bar.setWidth(WIDTH_HEARTH*max_health/HP_PER_HEARTH);
        health_bar.setValue(health);
        return health_bar;
    }

    private void addHealthBarToGroup(ProgressBar health_bar) {
        health_bar.setName("HEALTH_BAR");
        health_bar_group.addActor(health_bar);
        health_bar_group.setHeight(HEIGHT_HEART*SCALING_FACTOR);
    }

    public float getTotalHeight() {
        return height_stats;
    }

    public void setPosition(Vector3 position) {
        main_table.setPosition(position.x, position.y);
        health_bar_table.setPosition(position.x, position.y + height_stats - health_bar_group.getHeight());
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public void update(float dt) {
        updateHealthBar();
        updateStats();
        stage.act(dt);
    }

    private void updateHealthBar() {
        Map<STATS_ENUM, Number> stats = controller.getPlayerStats();
        int max_health = (int) stats.get(STATS_ENUM.MAX_HEALTH);
        int health = (int) stats.get(STATS_ENUM.HEALTH);
        ProgressBar health_bar = health_bar_group.findActor("HEALTH_BAR");
        if (health_bar.getMaxValue() != max_health) {
            health_bar_group.removeActor(health_bar);
            health_bar = createHealthBar(health, 0, max_health);
            addHealthBarToGroup(health_bar);
        } else {
            health_bar.setValue(health);
        }
    }

    private void updateStats() {
        Map<STATS_ENUM, Number> stats = controller.getPlayerStats();
        for (Map.Entry<STATS_ENUM, Group> group: this.stats_groups.entrySet()) {
            STATS_ENUM key = group.getKey();
            Group value = group.getValue();
            Label label = value.findActor("LABEL");
            label.setText(stats.get(key).toString());
        }
    }

    public void render() {
        stage.setDebugAll(true);
        stage.draw();
    }
}
