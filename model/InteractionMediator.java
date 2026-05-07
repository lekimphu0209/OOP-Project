package model;

import java.util.List;

public class InteractionMediator {

    public Vector2D resolveMovement(IMovable self, Vector2D desiredMove, List<DummyAnimal> nearbyEntities) {
        Vector2D adjustedMove = desiredMove;

        if (!(self instanceof IYieldable) || !(self instanceof ICollidable)) {
            return adjustedMove;
        }

        IYieldable selfYieldable = (IYieldable) self;
        ICollidable selfCollidable = (ICollidable) self;

        for (DummyAnimal other : nearbyEntities) {
            if (other == self) {
                continue;
            }

            Vector2D toOther = other.getPosition().subtract(self.getPosition());
            double distance = toOther.magnitude();
            double safeDistance = selfCollidable.getRadius() + other.getRadius() + 3.0;

           if (other instanceof IYieldable otherYieldable) {
    if (distance <= safeDistance && selfYieldable.mustYieldTo(otherYieldable)) {
        Vector2D sidestep = toOther.perpendicular().normalize().multiply(3.0);
        adjustedMove = adjustedMove.add(sidestep);
    }
}
        }

        return adjustedMove;
    }
}