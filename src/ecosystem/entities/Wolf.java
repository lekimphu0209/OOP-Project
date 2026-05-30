package ecosystem.entities;

import ecosystem.behavior.HunterStrategy;
import ecosystem.physics.Vector2D;

public class Wolf extends Animal {
    public Wolf(Vector2D position) {
        super(position, 0.5, "Sói", 100, 1.5, 3, true);
        this.hungerRate = 1;
        this.setStrategy(new HunterStrategy());
        this.reproductionCooldownMax = 90; // 4.5 seconds (90 ticks at 500ms/tick) - reduced for survival
    }

    @Override
    public void update() {
        // Update logic handled by act() method
    }

    @Override
    public boolean isEnemy(Animal other) {
        // Wolf sợ Tiger và Human
        return other instanceof Tiger || other instanceof Human;
    }

    @Override
    public boolean canEat(Animal other) {
        // Wolf ăn: Duck, Rabbit, Deer
        return other instanceof Duck || other instanceof Rabbit || other instanceof Deer;
    }

    @Override
    protected Animal createChild(Vector2D position) {
        return new Wolf(position);
    }
}
