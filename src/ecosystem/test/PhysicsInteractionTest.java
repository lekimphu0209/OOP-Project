package ecosystem.test;

import ecosystem.entities.Elephant;
import ecosystem.entities.Rabbit;
import ecosystem.entities.Wolf;
import ecosystem.environment.Environment;
import ecosystem.physics.CollisionDetector;
import ecosystem.physics.PhysicsCollider;
import ecosystem.physics.PhysicsSystem;
import ecosystem.physics.Vector2D;
import ecosystem.physics.YieldMediator;
import ecosystem.terrain.Grid;
import ecosystem.terrain.TerrainType;
import ecosystem.terrain.Tile;

/**
 * Tests for Member 4 physics & interaction subsystem.
 */
public class PhysicsInteractionTest {

    public static void main(String[] args) {
        System.out.println("=== PHYSICS INTERACTION TESTS ===\n");
        testYieldPriority();
        testCollisionTerrain();
        testPhysicsColliderOverlap();
        testMediatorDisplacement();
        System.out.println("\n=== ALL PHYSICS TESTS PASSED ===");
    }

    private static void testYieldPriority() {
        System.out.println("Test 1: Yield priority (Elephant > Rabbit)");
        YieldMediator mediator = new YieldMediator(new CollisionDetector());
        Grid grid = new Grid(8, 8, false);
        Environment env = new Environment(grid);
        env.addAnimal(new Rabbit(new Vector2D(1, 1)));
        env.addAnimal(new Elephant(new Vector2D(2, 2)));

        var sorted = mediator.sortForInteraction(env.getAnimals());
        assert sorted.get(0) instanceof Elephant : "Elephant should act first";
        assert sorted.get(1) instanceof Rabbit : "Rabbit should act second";
        System.out.println("  OK\n");
    }

    private static void testCollisionTerrain() {
        System.out.println("Test 2: Forest blocks wolf");
        Grid grid = new Grid(5, 5, false);
        grid.setTile(2, 2, new Tile(2, 2, TerrainType.FOREST));
        Environment env = new Environment(grid);
        Wolf wolf = new Wolf(new Vector2D(1, 2));
        CollisionDetector detector = env.getPhysicsSystem().getCollisionDetector();
        detector.rebuildOccupancyIndex(env);
        boolean blocked = !detector.canEnterTile(wolf, env, new Vector2D(2, 2));
        assert blocked : "Wolf must not enter forest tile";
        System.out.println("  OK\n");
    }

    private static void testPhysicsColliderOverlap() {
        System.out.println("Test 3: PhysicsCollider circle overlap");
        Rabbit a = new Rabbit(new Vector2D(0, 0));
        Rabbit b = new Rabbit(new Vector2D(0.3, 0));
        boolean overlap = PhysicsCollider.circlesOverlap(a, a.getRenderPosition(), b, b.getRenderPosition());
        assert overlap : "Close rabbits should overlap by radius";
        System.out.println("  OK\n");
    }

    private static void testMediatorDisplacement() {
        System.out.println("Test 4: Low priority yields tile");
        Grid grid = new Grid(6, 6, false);
        Environment env = new Environment(grid);
        Rabbit rabbit = new Rabbit(new Vector2D(3, 3));
        Elephant elephant = new Elephant(new Vector2D(2, 3));
        env.addAnimal(rabbit);
        env.addAnimal(elephant);

        PhysicsSystem physics = env.getPhysicsSystem();
        physics.getCollisionDetector().rebuildOccupancyIndex(env);
        elephant.setDirection(new Vector2D(1, 0));
        physics.getMovementEngine().step(elephant, env);

        boolean rabbitMoved = (int) rabbit.getPosition().getX() != 3 || (int) rabbit.getPosition().getY() != 3;
        assert rabbitMoved || !rabbit.getPosition().equals(new Vector2D(3, 3))
                : "Rabbit should be displaced or elephant entered";
        System.out.println("  OK\n");
    }
}
