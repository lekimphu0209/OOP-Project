package model.predator;

import model.Animal;
import model.IMap;
import model.Vector2D;
import model.strategy.HunterStrategy;
import model.strategy.PatrolStrategy;
import java.util.List;

public class Human extends Animal {

    private static final double ATTACK_RANGE     = 15.0;
    private static final double ATTACK_DAMAGE    = 60.0;
    private static final double HUNGER_REDUCE    = 20.0;
    private static final double DETECTION_RADIUS = 120.0;

    public Human(String name, Vector2D position) {
        super(name, position, 8.0, 2.5, 10, 200.0);
        // Human mặc định tuần tra, chuyển sang săn khi phát hiện mồi
        this.strategy = new PatrolStrategy(
            position,
            position.add(new Vector2D(30, 0)),
            position.add(new Vector2D(30, 30)),
            position.add(new Vector2D(0, 30))
        );
    }

    @Override
    public void update(double deltaTime, IMap map, List<Animal> nearby) {
        if (!alive) return;

        updateBiology(deltaTime);

        // --- Phát hiện mồi → chuyển sang chế độ săn ---
        boolean preyNearby = nearby.stream()
            .anyMatch(a -> a != this
                       && a.isAlive()
                       && a.getPriority() < this.priority
                       && position.distanceTo(a.getPosition()) <= DETECTION_RADIUS);

        if (preyNearby && strategy instanceof PatrolStrategy) {
            strategy = new HunterStrategy(DETECTION_RADIUS);
        } else if (!preyNearby && strategy instanceof HunterStrategy) {
            // Mất dấu mồi → quay lại tuần tra
            strategy = new PatrolStrategy(
                position,
                position.add(new Vector2D(30, 0)),
                position.add(new Vector2D(30, 30)),
                position.add(new Vector2D(0, 30))
            );
        }

        if (strategy != null) {
            Vector2D direction = strategy.computeDirection(this, map, nearby);
            if (direction.magnitude() > 0) {
                this.velocity = direction.normalize().multiply(baseSpeed);
            }
        }

        for (Animal other : nearby) {
            if (other == this || !other.isAlive()) continue;
            if (other.getPriority() < this.priority) {
                double dist = position.distanceTo(other.getPosition());
                if (dist <= ATTACK_RANGE) {
                    attack(other);
                    break;
                }
            }
        }
    }

    @Override
    public String getSpeciesName() { return "Thợ Săn"; }

    @Override
    public void attack(Animal prey) {
        prey.takeDamage(ATTACK_DAMAGE);
        hunger = Math.max(0, hunger - HUNGER_REDUCE);
    }

    @Override
    public void eat(double nutritionValue) {
        hunger = Math.max(0, hunger - nutritionValue);
    }
}