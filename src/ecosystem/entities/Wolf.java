package ecosystem.entities;

import ecosystem.behavior.HunterStrategy;
import ecosystem.physics.Vector2D;

public class Wolf extends Animal {
    public Wolf(Vector2D position) {
        super(position, 0.5, "Sói", 50, 1.5, 3, true);
        this.hungerRate = 2;
        this.setStrategy(new HunterStrategy());
    }

    @Override
    public void update() {
        // Update logic handled by act() method
    }
}
