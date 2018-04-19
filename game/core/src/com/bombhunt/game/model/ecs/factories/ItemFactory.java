package com.bombhunt.game.model.ecs.factories;

import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.ComponentMapper;
import com.artemis.World;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.bombhunt.game.model.Grid;
import com.bombhunt.game.model.ecs.components.AnimationComponent;
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
    private ComponentMapper<AnimationComponent> mapAnimation;
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
        mapAnimation = world.getMapper(AnimationComponent.class);
        mapNetwork = world.getMapper(NetworkComponent.class);

        itemArchetype = new ArchetypeBuilder()
                .add(TransformComponent.class)
                .add(GridPositionComponent.class)
                .add(SpriteComponent.class)
                .add(AnimationComponent.class)
                .add(ItemComponent.class)
                .add(NetworkComponent.class)
                .build(world);
    }

    @Override
    public void setGrid(Grid grid) {
        this.grid = grid;
    }


    //TODO
    @Override
    public int createFromMessage(String message) {
        int e = createRandomItem(Vector3.Zero);
        return e;
    }

    public int createItem(Vector3 position, ItemType itemType ) {
        final int e = world.create(itemArchetype);
        ItemComponent itemComponent = mapItem.get(e);
        itemComponent.type = itemType;
        mapTransform.get(e).position = position;
        mapTransform.get(e).scale = new Vector2(1f, 1f);
        GridPositionComponent gridPositionComponent = mapGrid.get(e);
        gridPositionComponent.grid = grid;
        //gridPositionComponent.cellIndex = gridPositionComponent.grid.getCellIndex(position);

        //TODO change texture (copied from bomb)
        int x;
        int y;
        switch (itemType) {
            case INCREASEDAMAGE: x=0; y=0; break;
            default: x=1; y=1;
        }
        mapAnimation.get(e).animation = SpriteHelper.createDecalAnimation(
                SpriteHelper.createSprites(region, 32, x, y, 1),
                1);
        mapSprite.get(e).sprite = mapAnimation.get(e).animation.getKeyFrame(0, true);

        return e;
    }

    public int createRandomItem(Vector3 position) {
        ItemType randomType = ItemType.values()[random.nextInt(ItemType.values().length)];
        return createItem(position, randomType);
    }
}
