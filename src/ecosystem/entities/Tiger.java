package ecosystem.entities;

import ecosystem.behavior.HunterStrategy;
import ecosystem.physics.Vector2D;

public class Tiger extends Animal {
    public Tiger(Vector2D position) {
        super(position, 0.7, "Hổ", 70, 1.3, 4, true);
        this.hungerRate = 2;
        this.setStrategy(new HunterStrategy());
    }

    @Override
    public void update() {
        // Update logic handled by act() method
    }
}
