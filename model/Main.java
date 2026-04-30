package model;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Vector2D a = new Vector2D(3, 4);
        Vector2D b = new Vector2D(1, 2);

        System.out.println("=== TEST Vector2D ===");
        System.out.println("a = " + a);
        System.out.println("b = " + b);
        System.out.println("a + b = " + a.add(b));
        System.out.println("a - b = " + a.subtract(b));
        System.out.println("a * 2 = " + a.multiply(2));
        System.out.println("|a| = " + a.magnitude());
        System.out.println("distance(a, b) = " + a.distanceTo(b));

        PhysicsBody body = new PhysicsBody(new Vector2D(10, 20), 5, 50);

        System.out.println("\n=== TEST PhysicsBody ===");
        System.out.println("Position: " + body.getPosition());
        System.out.println("Velocity: " + body.getVelocity());
        System.out.println("Radius: " + body.getRadius());
        System.out.println("Mass: " + body.getMass());

        body.setPosition(new Vector2D(30, 40));
        body.setVelocity(new Vector2D(2, 1));

        System.out.println("New Position: " + body.getPosition());
        System.out.println("New Velocity: " + body.getVelocity());

        System.out.println("\n=== TEST COLLISION ===");
        DummyAnimal rabbitCollision = new DummyAnimal(new Vector2D(0, 0), 10, 2, 1);
        DummyAnimal wolfCollision = new DummyAnimal(new Vector2D(3, 0), 10, 2, 4);

        CollisionDetector detector = new CollisionDetector();
        boolean isColliding = detector.isColliding(rabbitCollision, wolfCollision);

        System.out.println("Rabbit position: " + rabbitCollision.getPosition());
        System.out.println("Wolf position: " + wolfCollision.getPosition());
        System.out.println("Is Colliding: " + isColliding);

        System.out.println("\n=== TEST MAP BLOCK ===");
        DummyMap dummyMap = new DummyMap();
        InteractionMediator mediator = new InteractionMediator();
        MovementEngine engineWithMap = new MovementEngine(dummyMap, mediator);

        DummyAnimal animal1 = new DummyAnimal(new Vector2D(0, 0), 10, 2, 1);
        System.out.println("Animal1 before move: " + animal1.getPosition());
        engineWithMap.move(animal1, new Vector2D(1, 0), 1.0, List.of());
        System.out.println("Animal1 after move: " + animal1.getPosition());

        DummyAnimal animal2 = new DummyAnimal(new Vector2D(0, 10), 10, 2, 1);
        System.out.println("Animal2 before move: " + animal2.getPosition());
        engineWithMap.move(animal2, new Vector2D(1, 0), 1.0, List.of());
        System.out.println("Animal2 after move: " + animal2.getPosition());

        System.out.println("\n=== TEST YIELDING ===");
        DummyAnimal rabbitYield = new DummyAnimal(new Vector2D(0, 0), 5, 2, 1);
        DummyAnimal tiger = new DummyAnimal(new Vector2D(4, 0), 5, 3, 5);

        System.out.println("Rabbit before move: " + rabbitYield.getPosition());
        System.out.println("Tiger position: " + tiger.getPosition());

        engineWithMap.move(rabbitYield, new Vector2D(1, 0), 1.0, List.of(tiger));

        System.out.println("Rabbit after move: " + rabbitYield.getPosition());
        System.out.println("Rabbit velocity: " + rabbitYield.getVelocity());

        System.out.println("\n=== TEST ESCAPE FROM PREDATOR ===");
        DummyAnimal rabbitEscape = new DummyAnimal(new Vector2D(10, 10), 5, 2, 1);
        DummyAnimal tigerNear = new DummyAnimal(new Vector2D(14, 10), 5, 3, 5);

        Vector2D escapeDirection = rabbitEscape.getPosition().subtract(tigerNear.getPosition());

        System.out.println("Rabbit before move: " + rabbitEscape.getPosition());
        System.out.println("Tiger position: " + tigerNear.getPosition());
        System.out.println("Escape direction: " + escapeDirection);

        engineWithMap.move(rabbitEscape, escapeDirection, 1.0, List.of(tigerNear));

        System.out.println("Rabbit after move: " + rabbitEscape.getPosition());
        System.out.println("Rabbit velocity: " + rabbitEscape.getVelocity());
    }
}