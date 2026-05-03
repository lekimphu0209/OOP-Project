package ecosystem.entities;

import ecosystem.physics.Vector2D;

public class Duck extends Animal {
    public Duck(Vector2D position) {
        super(position, 0.4, "Vịt", 25, 1.0, 0, false);
        this.canSwim = true;
        this.canWalk = true;
    }

    @Override
    public void update() {
        // Handled by act()
    }
}
