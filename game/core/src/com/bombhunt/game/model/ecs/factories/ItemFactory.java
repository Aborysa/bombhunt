package com.bombhunt.game.model.ecs.factories;

import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.ComponentMapper;
import com.artemis.World;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.bombhunt.game.model.Grid;
import com.bombhunt.game.model.ecs.components.GridPositionComponent;
import com.bombhunt.game.model.ecs.components.ItemComponent;
import com.bombhunt.game.model.ecs.components.NetworkComponent;
import com.bombhunt.game.model.ecs.components.SpriteComponent;
import com.bombhunt.game.model.ecs.components.TransformComponent;
import com.bombhunt.game.services.assets.Assets;
import com.bombhunt.game.services.graphics.SpriteHelper;

import java.util.Random;

/**
 * Created by bartc on 19.04.2018.
 */

public class ItemFactory implements IEntityFactory, INetworkFactory {

    private ComponentMapper<TransformComponent> mapTransform;
    private ComponentMapper<GridPositionComponent> mapGrid;
    private ComponentMapper<SpriteComponent> mapSprite;
    private ComponentMapper<ItemComponent> mapItem;
    private ComponentMapper<NetworkComponent> mapNetwork;

    private World world;
    private Grid grid;
    private Archetype itemArchetype;
    private TextureRegion region;
    private Random random;

    public ItemFactory() {
        Assets asset_manager = Assets.getInstance();
        region = new TextureRegion(asset_manager.get("items.png",
                Texture.class));
        random = new Random();
    }

    @Override
    public void setWorld(World world) {
        this.world = world;

        mapTransform = world.getMapper(TransformComponent.class);
        mapGrid = world.getMapper(GridPositionComponent.class);
        mapSprite = world.getMapper(SpriteComponent.class);
        mapItem = world.getMapper(ItemComponent.class);
        mapNetwork = world.getMapper(NetworkComponent.class);

        itemArchetype = new ArchetypeBuilder()
                .add(TransformComponent.class)
                .add(GridPositionComponent.class)
                .add(SpriteComponent.class)
                .add(ItemComponent.class)
                .add(NetworkComponent.class)
                .build(world);
    }

    @Override
    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    @Override
    public int createFromMessage(String message) {
        int e = createItem(Vector3.Zero, ITEM_TYPE_ENUM.DAMAGE);
        return e;
    }

    public int createRandomItem(Vector3 position) {
        int random_item_value = random.nextInt(ITEM_TYPE_ENUM.values().length);
        ITEM_TYPE_ENUM randomType = ITEM_TYPE_ENUM.values()[random_item_value];
        return createItem(position, randomType);
    }

    private int createItem(Vector3 position, ITEM_TYPE_ENUM type) {
        final int e = world.create(itemArchetype);
        ItemComponent itemComponent = mapItem.get(e);
        mapTransform.get(e).position = position;
        mapTransform.get(e).scale = new Vector2(1f, 1f);
        GridPositionComponent gridPositionComponent = mapGrid.get(e);
        gridPositionComponent.grid = grid;
        gridPositionComponent.cellIndex = gridPositionComponent.grid.getCellIndex(position);
        itemComponent.type = type;
        Array<Sprite> sprites = SpriteHelper.createSprites(region, 32, type.getCoord_x(), type.getCoord_y(), 1);
        Sprite sprite = sprites.get(0);
        mapSprite.get(e).sprite = Decal.newDecal(sprite, true);
        mapSprite.get(e).sprite.setDimensions(16, 16);
        return e;
    }
}
