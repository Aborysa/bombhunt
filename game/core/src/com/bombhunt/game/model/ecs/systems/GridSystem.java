package com.bombhunt.game.model.ecs.systems;


import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.bombhunt.game.services.physic.Collision;
import com.bombhunt.game.model.Grid;
import com.bombhunt.game.model.ecs.components.Box2dComponent;
import com.bombhunt.game.model.ecs.components.GridPositionComponent;
import com.bombhunt.game.model.ecs.components.TransformComponent;


public class GridSystem extends IteratingSystem {

    private ComponentMapper<TransformComponent> mapTransform;
    private ComponentMapper<GridPositionComponent> mapGridPosition;
    private ComponentMapper<Box2dComponent> mapBox2d;


    public GridSystem() {
        super(Aspect.all(TransformComponent.class, GridPositionComponent.class));
    }


    protected void process(int e) {
        TransformComponent transformComponent = mapTransform.get(e);
        GridPositionComponent gridPositionComponent = mapGridPosition.get(e);
        Grid grid = gridPositionComponent.grid;
        Vector3 position = transformComponent.position;

        Vector2 pos2d = new Vector2(position.x, position.y).sub(grid.getCellSize() / 2, grid.getCellSize() / 2);

        // TODO: FILTER OVER MOVABLE OBJECTS ONLY (NO NEED TO LOOK AT WALLS PER EXAMPLE)
        Vector2 gridPosition = grid.getSnappedPosition(pos2d.cpy().add(grid.getCellSize() / 2, grid.getCellSize() / 2));
        int cellIndex = grid.getCellIndex(gridPosition);
        if (cellIndex != gridPositionComponent.cellIndex) {
            if (!grid.isOutOfBounds(gridPositionComponent.cellIndex)) {
                grid.removeEntity(e, gridPositionComponent.cellIndex);
            }
            if (!grid.isOutOfBounds(cellIndex)) {
                grid.addEntity(e, cellIndex);
            }
            gridPositionComponent.cellIndex = cellIndex;
        }

        Vector2 diff = pos2d.cpy().sub(gridPosition);
        if (gridPositionComponent.snapToGrid) {
            position.set(gridPosition, position.z);
            if (gridPositionComponent.accumulate) {
                gridPositionComponent.accumulator.add(diff);
                if (Math.abs(gridPositionComponent.accumulator.x) >= grid.getCellSize()) {
                    float accumulated = Math.signum(gridPositionComponent.accumulator.x) * grid.getCellSize();
                    gridPositionComponent.accumulator.x -= accumulated;
                    position.x += accumulated;
                }
                if (Math.abs(gridPositionComponent.accumulator.y) >= grid.getCellSize()) {
                    float accumulated = Math.signum(gridPositionComponent.accumulator.y) * grid.getCellSize();
                    gridPositionComponent.accumulator.y -= accumulated;
                    position.y += accumulated;
                    System.out.println("Acc overflow");
                }
            }
            position.add(grid.getCellSize() / 2, grid.getCellSize() / 2, 0);
        }
        if (mapBox2d.has(e)) {
            Body body = mapBox2d.get(e).body;
            body.setTransform(position.x * Collision.worldTobox2d, position.y * Collision.worldTobox2d, body.getAngle());
        }
    }
}
