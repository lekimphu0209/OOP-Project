package ecosystem.entities;

import ecosystem.behavior.HunterStrategy;
import ecosystem.physics.Vector2D;

public class Tiger extends Animal {
    public Tiger(Vector2D position) {
        super(position, 0.7, "Hổ", 100, 1.3, 4, true);
        this.hungerRate = 1;
        this.setStrategy(new HunterStrategy());
        this.reproductionCooldownMax = 120; // 6 seconds (120 ticks at 500ms/tick) - reduced for survival
    }

    @Override
    public void update() {
        // Update logic handled by act() method
    }

    @Override
    public boolean isEnemy(Animal other) {
        // Tiger sợ Human
        return other instanceof Human;
    }

    @Override
    public boolean canEat(Animal other) {
        // Tiger ăn: Duck, Rabbit, Deer
        return other instanceof Duck || other instanceof Rabbit || other instanceof Deer;
    }

    @Override
    protected Animal createChild(Vector2D position) {
        return new Tiger(position);
    }
}
