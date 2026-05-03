package ecosystem.entities;

import ecosystem.behavior.ScaredStrategy;
import ecosystem.physics.Vector2D;

public class Deer extends Animal {
    public Deer(Vector2D position) {
        super(position, 0.6, "Hươu", 50, 1.0, 2, false);
        this.setStrategy(new ScaredStrategy());
    }

    @Override
    public void update() {
        // Update logic handled by act() method
    }
}
