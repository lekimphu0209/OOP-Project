package ecosystem.entities;

import ecosystem.behavior.PassiveStrategy;
import ecosystem.physics.Vector2D;

public class Elephant extends Animal {
    public Elephant(Vector2D position) {
        super(position, 1.0, "Voi", 100, 0.8, 5, false);
        this.setStrategy(new PassiveStrategy());
        this.reproductionCooldownMax = 200; // 10 seconds (200 ticks at 500ms/tick)
    }

    @Override
    public boolean mustYieldTo(ecosystem.physics.IYieldable other) {
        // Elephant never yields to anyone
        return false;
    }

    @Override
    public void update() {
        // Update logic handled by act() method
    }

    @Override
    public boolean isEnemy(Animal other) {
        // Elephant không có kẻ thù
        return false;
    }

    @Override
    public boolean canEat(Plant plant) {
        // Elephant ăn Grass và Fruit
        return plant.getType().equals("Cỏ") || plant.getType().equals("Cây ăn quả");
    }

    @Override
    protected Animal createChild(Vector2D position) {
        return new Elephant(position);
    }
}
