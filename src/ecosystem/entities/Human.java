package ecosystem.entities;

import ecosystem.behavior.HunterStrategy;
import ecosystem.physics.Vector2D;

public class Human extends Animal {
    public Human(Vector2D position) {
        super(position, 0.6, "Người", 100, 1.0, 5, true);
        this.setStrategy(new HunterStrategy());
        setLegacyReproductionCooldown(200);
    }

    @Override
    public void update() {
        // Update logic handled by act() method
    }

    @Override
    public boolean isEnemy(Animal other) {
        // Human không có kẻ thù (chỉ nhường Elephant)
        return false;
    }

    @Override
    public boolean canEat(Animal other) {
        // Human ăn: Duck, Tiger, Wolf, Crocodile, Deer, Rabbit
        return other instanceof Duck || other instanceof Tiger || other instanceof Wolf ||
               other instanceof Crocodile || other instanceof Deer ||
               other instanceof Rabbit;
    }

    @Override
    public boolean canEat(Plant plant) {
        // Human có thể ăn Fruit Tree
        return plant.getType().equals("Cây ăn quả");
    }

    @Override
    protected Animal createChild(Vector2D position) {
        return new Human(position);
    }
}
