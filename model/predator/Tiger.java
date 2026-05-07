package model.predator;

import model.Animal;
import model.IMap;
import model.Vector2D;
import model.strategy.HunterStrategy;
import model.strategy.FleeStrategy;
import java.util.List;

public class Tiger extends Animal {

    private static final double ATTACK_RANGE      = 4.0;
    private static final double ATTACK_DAMAGE     = 40.0;
    private static final double HUNGER_REDUCE     = 40.0;
    private static final double DETECTION_RADIUS  = 100.0;
    private static final double FLEE_HP_THRESHOLD = 40.0;

    public Tiger(String name, Vector2D position) {
        super(name, position, 14.0, 4.0, 5, 150.0);
        this.strategy = new HunterStrategy(DETECTION_RADIUS);
    }

    @Override
    public void update(double deltaTime, IMap map, List<Animal> nearby) {
        if (!alive) return;

        updateBiology(deltaTime);

        if (health < FLEE_HP_THRESHOLD && !(strategy instanceof FleeStrategy)) {
            strategy = new FleeStrategy(80.0);
        } else if (health >= FLEE_HP_THRESHOLD && strategy instanceof FleeStrategy) {
            strategy = new HunterStrategy(DETECTION_RADIUS);
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
    public String getSpeciesName() { return "Hổ"; }

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