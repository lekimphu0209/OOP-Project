package ecosystem.entities;

import ecosystem.SimulationConfig;
import ecosystem.physics.Vector2D;

public class Plant extends Entity {
    private final String type;
    private int growthStage;
    private final int maxGrowthStage;
    private boolean edible;
    private double nutritionValue;
    private boolean alive;
    private int respawnTimer = 0;
    private static final int RESPAWN_TIME = SimulationConfig.legacyTicks(50);

    public Plant(Vector2D position, String type, boolean edible, double nutritionValue) {
        super(position, 0.5);
        this.type = type;
        this.edible = edible;
        this.nutritionValue = nutritionValue;
        this.growthStage = 0;
        this.maxGrowthStage = 10;
        this.alive = true;
    }

    public String getType() {
        return type;
    }

    public int getGrowthStage() {
        return growthStage;
    }

    public boolean isAlive() {
        return alive;
    }

    public void grow() {
        if (!alive) return;

        if (growthStage < maxGrowthStage) {
            growthStage++;
        }
    }

    public boolean isEdible() {
        return edible && growthStage >= 3;
    }

    public double getNutritionValue() {
        return nutritionValue * (growthStage / (double) maxGrowthStage);
    }

    public void beEaten() {
        alive = false;
        respawnTimer = RESPAWN_TIME;
    }

    public void respawn(Vector2D newPosition) {
        this.position = newPosition;
        this.growthStage = 0;
        this.alive = true;
        this.respawnTimer = 0;
    }

    public int getRespawnTimer() {
        return respawnTimer;
    }

    public boolean canRespawn() {
        return !alive && respawnTimer <= 0;
    }

    public void decreaseRespawnTimer() {
        if (!alive && respawnTimer > 0) {
            respawnTimer--;
        }
    }

    @Override
    public void update() {
        if (alive) {
            grow();
        } else {
            decreaseRespawnTimer();
        }
    }
}
