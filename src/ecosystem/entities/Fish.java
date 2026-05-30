package ecosystem.entities;

import ecosystem.physics.Vector2D;

public class Fish extends Animal {
    public Fish(Vector2D position) {
        super(position, 0.3, "Cá", 100, 0.8, 0, false);
        this.canSwim = true;
        this.canWalk = false;
        this.reproductionCooldownMax = 40; // 2 seconds (40 ticks at 500ms/tick)
    }

    @Override
    public void update() {
        // Handled by act()
    }

    @Override
    public boolean isEnemy(Animal other) {
        // Fish sợ Crocodile
        return other instanceof Crocodile;
    }

    @Override
    public boolean canEat(Animal other) {
        // Fish không ăn thịt
        return false;
    }

    @Override
    public boolean canEat(Plant plant) {
        // Fish không ăn thực vật
        return false;
    }

    @Override
    protected Animal createChild(Vector2D position) {
        return new Fish(position);
    }
}
