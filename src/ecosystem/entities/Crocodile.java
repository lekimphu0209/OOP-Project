package ecosystem.entities;

import ecosystem.behavior.HunterStrategy;
import ecosystem.physics.Vector2D;

public class Crocodile extends Animal {
    public Crocodile(Vector2D position) {
        // Position, Radius, Name, Health, Speed, Priority, IsPredator
        super(position, 0.6, "Cá sấu", 100, 1.2, 3, true);
        this.hungerRate = 1;
        this.canSwim = true;
        this.canWalk = true;
        this.setStrategy(new HunterStrategy());
        // attackDamage uses default 100 from Animal constructor
        setLegacyReproductionCooldown(120);
    }

    @Override
    public void move(ecosystem.environment.Environment env) {
        // Fast in water (1.2x boost), slow on land (0.6x penalty)
        boolean inWater = env.getGrid().getTile((int)position.getX(), (int)position.getY()).getType() == ecosystem.terrain.TerrainType.WATER;
        this.speedBoost = inWater ? 1.2 : 0.6;
        
        // Strict distance limit: cannot go more than 3 tiles away from water
        if (!inWater && !isNearWater(env, 3)) {
            Vector2D toWater = findWaterDirection(env);
            if (toWater != null) {
                this.setDirection(toWater);
            } else {
                // If no water found in 5 tiles, stop moving
                this.setVelocity(new Vector2D(0,0));
                return;
            }
        }
        
        super.move(env);
    }

    private boolean isNearWater(ecosystem.environment.Environment env, int range) {
        int x = (int)position.getX();
        int y = (int)position.getY();
        for (int dx = -range; dx <= range; dx++) {
            for (int dy = -range; dy <= range; dy++) {
                if (env.getGrid().getTile(x+dx, y+dy) != null && 
                    env.getGrid().getTile(x+dx, y+dy).getType() == ecosystem.terrain.TerrainType.WATER) {
                    return true;
                }
            }
        }
        return false;
    }

    private Vector2D findWaterDirection(ecosystem.environment.Environment env) {
        int x = (int)position.getX();
        int y = (int)position.getY();
        for (int dx = -5; dx <= 5; dx++) {
            for (int dy = -5; dy <= 5; dy++) {
                if (env.getGrid().getTile(x+dx, y+dy) != null && 
                    env.getGrid().getTile(x+dx, y+dy).getType() == ecosystem.terrain.TerrainType.WATER) {
                    return new Vector2D(dx, dy).normalize();
                }
            }
        }
        return null;
    }

    @Override
    public void update() {
        // Handled by act()
    }

    @Override
    public boolean isEnemy(Animal other) {
        // Crocodile sợ Elephant và Human
        return other instanceof Elephant || other instanceof Human;
    }

    @Override
    public boolean canEat(Animal other) {
        // Crocodile ăn: Duck, Fish, Rabbit, Deer
        return other instanceof Duck || other instanceof Fish || other instanceof Rabbit || other instanceof Deer;
    }

    @Override
    protected Animal createChild(Vector2D position) {
        return new Crocodile(position);
    }
}
