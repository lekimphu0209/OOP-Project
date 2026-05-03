package ecosystem.entities;

import ecosystem.physics.Vector2D;

public class Fish extends Animal {
    public Fish(Vector2D position) {
        super(position, 0.3, "Cá", 20, 0.8, 0, false);
        this.canSwim = true;
        this.canWalk = false;
    }

    @Override
    public void update() {
        // Handled by act()
    }
}
