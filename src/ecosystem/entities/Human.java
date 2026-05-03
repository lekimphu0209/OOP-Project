package ecosystem.entities;

import ecosystem.behavior.PassiveStrategy;
import ecosystem.physics.Vector2D;

public class Human extends Animal {
    public Human(Vector2D position) {
        super(position, 0.6, "Người", 100, 1.0, 5, true);
        this.setStrategy(new PassiveStrategy());
    }

    @Override
    public boolean mustYieldTo(ecosystem.physics.IYieldable other) {
        // Human never yields to anyone
        return false;
    }

    @Override
    public void update() {
        // Update logic handled by act() method
    }
}
