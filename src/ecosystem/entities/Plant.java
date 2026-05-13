package ecosystem.entities;

import ecosystem.physics.Vector2D;

public class Plant extends Entity {
    private final String type;
    private int growthStage;
    private final int maxGrowthStage;
    private boolean edible;
    private double nutritionValue;

    public Plant(Vector2D position, String type, boolean edible, double nutritionValue) {
        super(position, 0.5);
        this.type = type;
        this.edible = edible;
        this.nutritionValue = nutritionValue;
        this.growthStage = 0;
        this.maxGrowthStage = 10;
    }

    public String getType() {
        return type;
    }

    public int getGrowthStage() {
        return growthStage;
    }

    public void grow() {
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
        growthStage = 0;
    }

    @Override
    public void update() {
        grow();
    }
}
