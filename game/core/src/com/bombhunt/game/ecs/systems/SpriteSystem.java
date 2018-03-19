package com.bombhunt.game.ecs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.bombhunt.game.ecs.components.AnimationComponent;
import com.bombhunt.game.ecs.components.SpriteComponent;
import com.bombhunt.game.ecs.components.TransformComponent;


// System for moving sprites into place and dealing with animations for animated sprites
public class SpriteSystem extends IteratingSystem{


    private ComponentMapper<TransformComponent> mapTransform;
    private ComponentMapper<SpriteComponent> mapSprite;
    private ComponentMapper<AnimationComponent> mapAnimation;


    public SpriteSystem(){
        super(Aspect.all(TransformComponent.class).one(SpriteComponent.class, AnimationComponent.class));
    }

    // helper function to process each entity.
    protected void process(int entity){
        // using our component mappers to get the components from the entities.
        TransformComponent transformComponent = mapTransform.get(entity);
        AnimationComponent animationComponent = mapAnimation.get(entity);
        SpriteComponent spriteComponent = mapSprite.get(entity);

        Sprite sprite = null;
        if(animationComponent != null){
            animationComponent.time += world.getDelta();
            System.out.println(animationComponent.animation);
            System.out.println(animationComponent.time);
            sprite = animationComponent.animation
                    .getKeyFrame(animationComponent.time, true);
            if(spriteComponent != null){
                spriteComponent.sprite = sprite;
            }
        }

        if(spriteComponent != null){
            sprite = spriteComponent.sprite;
        }
        sprite.setPosition(transformComponent.position.x, transformComponent.position.y);
        sprite.setScale(transformComponent.scale.x, transformComponent.scale.y);
        sprite.setRotation(transformComponent.rotation);

    }

}
