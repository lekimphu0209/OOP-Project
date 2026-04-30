package model;

import java.util.List;

public class MovementEngine {
    private final IMap map;
    private final InteractionMediator mediator;

    public MovementEngine(IMap map, InteractionMediator mediator) {
        this.map = map;
        this.mediator = mediator;
    }

    public Vector2D move(IMovable entity, Vector2D direction, double deltaTime, List<DummyAnimal> nearbyEntities) {
        if (direction == null || direction.magnitude() == 0) {
            return entity.getPosition();
        }

        Vector2D normalizedDirection = direction.normalize();
        double distance = entity.getBaseSpeed() * deltaTime;

        Vector2D desiredMove = normalizedDirection.multiply(distance);
        Vector2D adjustedMove = mediator.resolveMovement(entity, desiredMove, nearbyEntities);
        Vector2D newPosition = entity.getPosition().add(adjustedMove);

        if (!map.isWalkable(newPosition.getX(), newPosition.getY())) {
            entity.setVelocity(new Vector2D(0, 0));
            return entity.getPosition();
        }

        entity.setPosition(newPosition);
        entity.setVelocity(adjustedMove);

        return newPosition;
    }
}