package ecosystem.physics;

/**
 * Facade composing the physics subsystem (Facade pattern).
 * Single entry point for Environment / entities.
 */
public class PhysicsSystem {

    private final CollisionDetector collisionDetector;
    private final YieldMediator yieldMediator;
    private final MovementEngine movementEngine;

    public PhysicsSystem() {
        this.collisionDetector = new CollisionDetector();
        this.yieldMediator = new YieldMediator(collisionDetector);
        this.movementEngine = new MovementEngine(collisionDetector, yieldMediator);
    }

    public CollisionDetector getCollisionDetector() {
        return collisionDetector;
    }

    public YieldMediator getYieldMediator() {
        return yieldMediator;
    }

    public MovementEngine getMovementEngine() {
        return movementEngine;
    }
}
