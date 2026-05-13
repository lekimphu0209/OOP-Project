package ecosystem.physics;

import ecosystem.entities.Animal;
import java.util.List;

public class MovementEngine {
    private final ecosystem.terrain.Grid grid;

    public MovementEngine(ecosystem.terrain.Grid grid) {
        this.grid = grid;
    }

    public Vector2D move(IMovable entity, Vector2D direction, double deltaTime, List<Animal> nearbyEntities) {
        if (direction == null || direction.magnitude() == 0) {
            return entity.getPosition();
        }

        Vector2D normalizedDirection = direction.normalize();
        double distance = entity.getBaseSpeed() * deltaTime;

        Vector2D desiredMove = normalizedDirection.multiply(distance);
        Vector2D adjustedMove = resolveYielding(entity, desiredMove, nearbyEntities);
        Vector2D newPosition = entity.getPosition().add(adjustedMove);

        int newX = (int) newPosition.getX();
        int newY = (int) newPosition.getY();

        if (!grid.isWalkable(newX, newY)) {
            entity.setVelocity(new Vector2D(0, 0));
            return entity.getPosition();
        }

        // Apply terrain speed modifier
        double speedModifier = grid.getSpeedModifier(newX, newY);
        adjustedMove = adjustedMove.multiply(speedModifier);
        newPosition = entity.getPosition().add(adjustedMove);

        entity.setPosition(newPosition);
        entity.setVelocity(adjustedMove);

        return newPosition;
    }

    private Vector2D resolveYielding(IMovable self, Vector2D desiredMove, List<Animal> nearbyEntities) {
        Vector2D adjustedMove = desiredMove;

        if (!(self instanceof IYieldable) || !(self instanceof ICollidable)) {
            return adjustedMove;
        }

        IYieldable selfYieldable = (IYieldable) self;
        ICollidable selfCollidable = (ICollidable) self;

        for (Animal other : nearbyEntities) {
            if (other == self) {
                continue;
            }

            Vector2D toOther = other.getPosition().subtract(self.getPosition());
            double distance = toOther.magnitude();
            double safeDistance = selfCollidable.getRadius() + other.getRadius() + 1.0;

            if (other instanceof IYieldable) {
                IYieldable otherYieldable = (IYieldable) other;
                if (distance <= safeDistance && selfYieldable.mustYieldTo(otherYieldable)) {
                    Vector2D sidestep = toOther.perpendicular().normalize().multiply(1.0);
                    adjustedMove = adjustedMove.add(sidestep);
                }
            }
        }

        return adjustedMove;
    }
}
