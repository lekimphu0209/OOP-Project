package ecosystem.entities;

import ecosystem.behavior.ScaredStrategy;
import ecosystem.physics.Vector2D;

public class Rabbit extends Animal {
    public Rabbit(Vector2D position) {
        super(position, 0.4, "Thỏ", 100, 1.2, 1, false);
        this.hungerRate = 1;
        this.metabolismFactor = 0.38; // Đói chậm — sống lâu hơn khi chưa có cỏ
        this.setStrategy(new ScaredStrategy());
        setLegacyReproductionCooldown(60);
    }

    @Override
    public void update() {
        // Update logic handled by act() method
    }

    @Override
    public boolean isEnemy(Animal other) {
        // Rabbit sợ Tiger, Wolf, Crocodile, Human
        return other instanceof Tiger || other instanceof Wolf || 
               other instanceof Crocodile || other instanceof Human;
    }

    @Override
    public boolean canEat(Plant plant) {
        // Rabbit chỉ ăn Grass
        return plant.getType().equals("Cỏ");
    }

    @Override
    protected Animal createChild(Vector2D position) {
        return new Rabbit(position);
    }
}
