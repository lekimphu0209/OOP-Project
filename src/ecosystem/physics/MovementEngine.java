package ecosystem.physics;

import ecosystem.SimulationConfig;
import ecosystem.entities.Animal;
import ecosystem.environment.Environment;

/**
 * Encapsulates movement along a direction with smooth cell interpolation.
 * Depends on abstractions {@link CollisionDetector} and {@link YieldMediator}.
 */
public class MovementEngine {

    private final CollisionDetector collisionDetector;
    private final YieldMediator yieldMediator;

    public MovementEngine(CollisionDetector collisionDetector, YieldMediator yieldMediator) {
        this.collisionDetector = collisionDetector;
        this.yieldMediator = yieldMediator;
    }

    public void step(Animal animal, Environment env) {
        Vector2D nextCell = calculateTargetPosition(animal);
        if (PhysicsCollider.sameGridCell(nextCell, animal.getPosition())) {
            animal.changeDirectionForPhysics();
            nextCell = calculateTargetPosition(animal);
        }

        if (!collisionDetector.canEnterTile(animal, env, nextCell)) {
            for (int i = 0; i < 8; i++) {
                animal.changeDirectionForPhysics();
                nextCell = calculateTargetPosition(animal);
                if (collisionDetector.canEnterTile(animal, env, nextCell)) {
                    break;
                }
                if (i == 7) {
                    animal.setVelocity(new Vector2D(0, 0));
                    return;
                }
            }
        }

        double step = cellsPerTick(animal, env);
        double dx = nextCell.getX() - animal.getDisplayX();
        double dy = nextCell.getY() - animal.getDisplayY();
        double dist = Math.hypot(dx, dy);
        if (dist < 1e-6) {
            animal.setVelocity(new Vector2D(0, 0));
            return;
        }

        double moveStep = Math.min(step, dist);
        animal.addDisplayOffset((dx / dist) * moveStep, (dy / dist) * moveStep);
        animal.setVelocity(new Vector2D(dx / dist, dy / dist).multiply(calculateSpeed(animal, env)));

        if (moveStep >= dist - 1e-6) {
            yieldMediator.onEnterCell(animal, env, nextCell);
            animal.snapToCell(nextCell);
            collisionDetector.rebuildOccupancyIndex(env);
        }

        animal.trackStuckAndRecoverPhysics();
    }

    public Vector2D calculateTargetPosition(Animal animal) {
        int currentX = (int) animal.getPosition().getX();
        int currentY = (int) animal.getPosition().getY();
        int targetX = currentX;
        int targetY = currentY;

        Vector2D direction = animal.getMovementDirection();
        double dx = direction.getX();
        double dy = direction.getY();
        if (Math.abs(dx) >= Math.abs(dy)) {
            if (dx > 0.05) {
                targetX++;
            } else if (dx < -0.05) {
                targetX--;
            }
        } else {
            if (dy > 0.05) {
                targetY++;
            } else if (dy < -0.05) {
                targetY--;
            }
        }
        return new Vector2D(targetX, targetY);
    }

    private double cellsPerTick(Animal animal, Environment env) {
        return calculateSpeed(animal, env) * SimulationConfig.legacyRate(1.0) * SimulationConfig.MOVEMENT_SPEED_MULT;
    }

    private double calculateSpeed(Animal animal, Environment env) {
        return animal.getBaseSpeed() * animal.getSpeedBoost()
                * env.getSpeedModifier((int) animal.getPosition().getX(), (int) animal.getPosition().getY());
    }
}
