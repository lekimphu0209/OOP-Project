# Physics & Interaction (Member 4)

## OOP patterns

| Class | Pattern / principle |
|-------|---------------------|
| `IMovable`, `ICollidable`, `IYieldable` | **Interface segregation**, **abstraction** |
| `MovementEngine` | **Single responsibility** — movement only |
| `CollisionDetector` | **Single responsibility** — tile/terrain occupancy |
| `YieldMediator` | **Mediator** — yielding without tight coupling between species |
| `PhysicsSystem` | **Facade** — one entry point for `Environment` |
| `PhysicsCollider` | Utility — radius overlap (`ICollidable`) |

## Usage

```java
PhysicsSystem physics = environment.getPhysicsSystem();
physics.getCollisionDetector().rebuildOccupancyIndex(environment);
physics.getYieldMediator().sortForInteraction(animals);
animal.move(environment); // delegates to MovementEngine
```

## Tests

Run: `java -cp bin ecosystem.test.PhysicsInteractionTest`
