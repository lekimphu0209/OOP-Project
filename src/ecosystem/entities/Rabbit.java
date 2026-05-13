package ecosystem.entities;

import ecosystem.behavior.ScaredStrategy;
import ecosystem.physics.Vector2D;

public class Rabbit extends Animal {
    public Rabbit(Vector2D position) {
        super(position, 0.4, "Thỏ", 30, 1.2, 1, false);
        this.setStrategy(new ScaredStrategy());
    }

    @Override
    public void update() {
        // Update logic handled by act() method
    }
}
