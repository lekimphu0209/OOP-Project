package ecosystem.entities;

import ecosystem.behavior.PassiveStrategy;
import ecosystem.physics.Vector2D;

public class Elephant extends Animal {
    public Elephant(Vector2D position) {
        super(position, 1.0, "Voi", 150, 0.8, 5, false);
        this.setStrategy(new PassiveStrategy());
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
}
