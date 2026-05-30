package ecosystem.entities;

import ecosystem.physics.Vector2D;

public class Duck extends Animal {
    public Duck(Vector2D position) {
        super(position, 0.4, "Vịt", 100, 1.0, 0, false);
        this.canSwim = true;
        this.canWalk = true;
    }

    @Override
    public void move(ecosystem.environment.Environment env) {
        // Fast in water (1.3x boost), slow on land (0.7x penalty)
        boolean inWater = env.getGrid().getTile((int)position.getX(), (int)position.getY()).getType() == ecosystem.terrain.TerrainType.WATER;
        this.speedBoost = inWater ? 1.3 : 0.7;

        // Strict distance limit: cannot go more than 4 tiles away from water
        if (!inWater && !isNearWater(env, 4)) {
            Vector2D toWater = findWaterDirection(env);
            if (toWater != null) {
                this.setDirection(toWater);
            } else {
                // If no water found, stop moving
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
        for (int dx = -6; dx <= 6; dx++) {
            for (int dy = -6; dy <= 6; dy++) {
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
    public boolean canEat(Plant plant) {
        // Duck ăn Grass và Fruit Tree
        return plant.getType().equals("Cỏ") || plant.getType().equals("Cây ăn quả");
    }
}
