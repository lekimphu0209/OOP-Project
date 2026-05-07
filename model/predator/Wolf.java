package model.predator;

import model.Animal;
import model.IMap;
import model.Vector2D;
import model.strategy.HunterStrategy;
import model.strategy.FleeStrategy;
import java.util.List;

public class Wolf extends Animal {

    private static final double ATTACK_RANGE      = 3.0;
    private static final double ATTACK_DAMAGE     = 25.0;
    private static final double HUNGER_REDUCE     = 30.0;
    private static final double DETECTION_RADIUS  = 80.0;
    private static final double FLEE_HP_THRESHOLD = 30.0; // Dưới 30 HP thì chạy

    public Wolf(String name, Vector2D position) {
        super(name, position, 12.0, 3.0, 4, 100.0);
        // Gắn strategy mặc định: săn mồi
        this.strategy = new HunterStrategy(DETECTION_RADIUS);
    }

    @Override
    public void update(double deltaTime, IMap map, List<Animal> nearby) {
        if (!alive) return;

        updateBiology(deltaTime);

        // --- Tự động đổi strategy theo tình trạng HP ---
        if (health < FLEE_HP_THRESHOLD && !(strategy instanceof FleeStrategy)) {
            strategy = new FleeStrategy(60.0); // Chạy trốn khi sắp chết
        } else if (health >= FLEE_HP_THRESHOLD && strategy instanceof FleeStrategy) {
            strategy = new HunterStrategy(DETECTION_RADIUS); // Hồi phục → săn lại
        }

        // --- Dùng strategy để tính hướng di chuyển ---
        if (strategy != null) {
            Vector2D direction = strategy.computeDirection(this, map, nearby);
            if (direction.magnitude() > 0) {
                this.velocity = direction.normalize().multiply(baseSpeed);
            }
        }

        // --- Tấn công con mồi trong tầm ---
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
    public String getSpeciesName() { return "Sói"; }

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