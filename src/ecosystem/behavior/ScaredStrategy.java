package ecosystem.behavior;

import ecosystem.entities.Animal;
import ecosystem.entities.Entity;
import ecosystem.environment.Environment;
import ecosystem.physics.Vector2D;
import ecosystem.terrain.TerrainType;

import java.util.List;

public class ScaredStrategy implements SurvivalStrategy {
    private double visionRange = 5.0;

    @Override
    public void execute(Animal animal, Environment env) {
        // Priority: check fatigue first
        if (animal.isTired()) {
            animal.rest();
            return;
        }
        
        // Priority: check thirst and hunger first
        if (animal.getThirst() > 30) {
            if (env.hasWaterNearby(animal)) {
                animal.drink(env);
                animal.setActionState("Đang uống");
            } else {
                animal.setActionState("Tìm nước");
            }
            Vector2D waterDir = env.findDirectionToNearestWater(animal, 6);
            if (waterDir != null) animal.setDirection(waterDir);
            return;
        }
        
        if (animal.getHunger() > 30) {
            Entity food = animal.findFood(env);
            if (food != null) {
                double distance = animal.getPosition().distanceTo(food.getPosition());
                if (distance < 1.0) {
                    // At the food - eat it
                    animal.eat(food);
                    animal.setActionState("Đang ăn");
                } else {
                    // Move towards food
                    animal.setActionState("Tìm thức ăn");
                    Vector2D foodDir = food.getPosition().subtract(animal.getPosition()).normalize();
                    animal.setDirection(foodDir);
                }
            } else {
                animal.setActionState("Tìm thức ăn");
            }
            return;
        }
        
        Animal threat = findNearestThreat(animal, env.getAnimals());
        
        if (threat != null) {
            animal.setActionState("Trốn chạy");
            
            // Check if prey can hide in forest
            int currentX = (int) animal.getPosition().getX();
            int currentY = (int) animal.getPosition().getY();
            
            if (env.getGrid().getTile(currentX, currentY).getType() == TerrainType.FOREST) {
                // Prey is in forest - wolves can't enter, so prey is safe
                animal.setActionState("Trốn trong rừng");
                // Stay in forest and move slowly
                if (Math.random() < 0.3) {
                    new PassiveStrategy().execute(animal, env);
                }
                return;
            }
            
            // Try to move towards nearest forest to hide
            Vector2D forestDirection = findNearestForest(animal, env);
            if (forestDirection != null && animal.getPosition().distanceTo(threat.getPosition()) < 3.0) {
                // Priority: run to forest when threat is close
                animal.setDirection(forestDirection);
                animal.setSpeedBoost(1.5); // Speed boost when escaping
                animal.setActionState("Chạy vào rừng");
            } else {
                // Move away from threat
                Vector2D direction = animal.getPosition().subtract(threat.getPosition()).normalize();
                animal.setDirection(direction);
                animal.setSpeedBoost(1.3); // Speed boost when scared
                animal.setActionState("Chạy trốn");
            }
        } else {
            animal.setSpeedBoost(1.0); // Reset speed
            // Khi không có threat, rabbit đi lang thang bình thường
            new PassiveStrategy().execute(animal, env);
        }
    }

    private Animal findNearestThreat(Animal prey, List<Animal> animals) {
        Animal nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Animal other : animals) {
            if (other == null) continue;
            if (other != prey && other.isAlive() && prey.isEnemy(other)) {
                double distance = prey.getPosition().distanceTo(other.getPosition());
                if (distance < minDistance && distance <= visionRange) {
                    minDistance = distance;
                    nearest = other;
                }
            }
        }
        return nearest;
    }

    private Vector2D findNearestForest(Animal animal, Environment env) {
        Vector2D nearestForest = null;
        double minDistance = Double.MAX_VALUE;
        int searchRange = 5;

        for (int dx = -searchRange; dx <= searchRange; dx++) {
            for (int dy = -searchRange; dy <= searchRange; dy++) {
                int x = (int) animal.getPosition().getX() + dx;
                int y = (int) animal.getPosition().getY() + dy;
                
                if (env.getGrid().getTile(x, y) != null && 
                    env.getGrid().getTile(x, y).getType() == TerrainType.FOREST) {
                    Vector2D forestPos = new Vector2D(x, y);
                    double distance = animal.getPosition().distanceTo(forestPos);
                    if (distance < minDistance) {
                        minDistance = distance;
                        nearestForest = forestPos;
                    }
                }
            }
        }

        if (nearestForest != null) {
            return nearestForest.subtract(animal.getPosition()).normalize();
        }
        return null;
    }

    @Override
    public String getName() {
        return "Scared";
    }
}
