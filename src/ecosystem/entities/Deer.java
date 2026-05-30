package ecosystem.entities;

import ecosystem.behavior.ScaredStrategy;
import ecosystem.physics.Vector2D;

public class Deer extends Animal {
    public Deer(Vector2D position) {
        super(position, 0.6, "Hươu", 100, 1.0, 2, false);
        this.setStrategy(new ScaredStrategy());
        this.reproductionCooldownMax = 80; // 4 seconds (80 ticks at 500ms/tick)
    }

    @Override
    public void update() {
        // Update logic handled by act() method
    }

    @Override
    public boolean isEnemy(Animal other) {
        // Deer sợ Tiger, Wolf, Crocodile, Human
        return other instanceof Tiger || other instanceof Wolf || 
               other instanceof Crocodile || other instanceof Human;
    }

    @Override
    public boolean canEat(Plant plant) {
        // Deer ăn Grass và Fruit
        return plant.getType().equals("Cỏ") || plant.getType().equals("Cây ăn quả");
    }

    @Override
    protected Animal createChild(Vector2D position) {
        return new Deer(position);
    }
}
