package model.predator;

import model.Animal;
import model.IMap;
import model.Vector2D;
import model.strategy.PatrolStrategy;
import java.util.List;

public class Elephant extends Animal {

    private static final double TRAMPLE_RANGE  = 5.0;
    private static final double TRAMPLE_DAMAGE = 30.0;

    public Elephant(String name, Vector2D position) {
        super(name, position, 6.0, 6.0, 8, 300.0);
        // Voi luôn tuần tra, không săn mồi chủ động
        this.strategy = new PatrolStrategy(
            position,
            position.add(new Vector2D(40, 0)),
            position.add(new Vector2D(40, 40)),
            position.add(new Vector2D(0, 40))
        );
    }

    @Override
    public void update(double deltaTime, IMap map, List<Animal> nearby) {
        if (!alive) return;

        updateBiology(deltaTime);

        if (strategy != null) {
            Vector2D direction = strategy.computeDirection(this, map, nearby);
            if (direction.magnitude() > 0) {
                this.velocity = direction.normalize().multiply(baseSpeed);
            }
        }

        // Giẫm lên tất cả thứ nhỏ hơn đi vào vùng của mình
        for (Animal other : nearby) {
            if (other == this || !other.isAlive()) continue;
            if (other.getPriority() < this.priority) {
                double dist = position.distanceTo(other.getPosition());
                if (dist <= TRAMPLE_RANGE) {
                    attack(other);
                }
            }
        }
    }

    @Override
    public String getSpeciesName() { return "Voi"; }

    @Override
    public void attack(Animal prey) {
        prey.takeDamage(TRAMPLE_DAMAGE);
    }

    @Override
    public void eat(double nutritionValue) {
        hunger = Math.max(0, hunger - nutritionValue);
    }
}