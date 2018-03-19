package com.bombhunt.game.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.utils.Bag;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.bombhunt.game.components.Renderer;
import com.bombhunt.game.components.Transform;

/**
 * Created by Erling on 17/03/2018.
 */

public class RenderSystem extends EntitySystem{
    // reference to spritebatch for rendering
    private final SpriteBatch batch;

    protected ComponentMapper<Transform> mapTransform;
    protected ComponentMapper<Renderer> mapRenderer;

    public RenderSystem(SpriteBatch batch){
        super(Aspect.all(Transform.class));
        this.batch = batch;
    }

    // helper function to process each entity.
    protected void process(Entity entity){
        // using our component mappers to get the components from the entities.
        Transform transform = mapTransform.get(entity);
        Renderer renderer = mapRenderer.get(entity);

        // shouldn't need to check if they returned null, as only entities containing these components will be fetched in getEntities();

        // get the sprite from our renderer component, and change it's position according to where in the physical space our component is
        renderer.sprite.setPosition(transform.posX, transform.posY);
        renderer.sprite.setRotation(transform.rotation);
        renderer.sprite.setScale(transform.scaleX, transform.scaleY);

        // sprite has been moved accordingly to transform position, draw it.
        renderer.sprite.draw(batch);
    }

    // EntitySystem process called, iterates through our entities
    @Override
    protected void processSystem(){
        // Entity system function to get all entities catched by the system.
        Bag<Entity> entities = getEntities();

        // TODO: sort based on Transform posZ
        for(Entity e : entities){
            process(e);
        }
    }
}
