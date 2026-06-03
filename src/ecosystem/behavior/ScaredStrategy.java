package ecosystem.behavior;

import ecosystem.SimulationConfig;
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
        if (animal.getThirst() > SimulationConfig.HUNGER_SEEK_THRESHOLD) {
            if (env.hasWaterNearby(animal)) {
                animal.drink(env);
                animal.setActionState("Đang uống");
            } else {
                animal.setActionState("Tìm nước");
            }
            Vector2D waterDir = env.findDirectionToNearestWater(animal, 6);
            if (waterDir != null) {
                animal.setDirection(waterDir);
            } else {
                wander(animal);
            }
            return;
        }
        
        if (animal.getHunger() > SimulationConfig.HUNGER_SEEK_THRESHOLD) {
            Entity food = animal.findFood(env);
            if (food != null) {
                if (animal.isWithinRange(food.getPosition(), SimulationConfig.HERBIVORE_EAT_RADIUS)) {
                    animal.eat(food);
                    animal.setActionState("Đang ăn");
                } else {
                    animal.setActionState("Tìm thức ăn");
                    Vector2D foodDir = food.getPosition().subtract(animal.getRenderPosition()).normalize();
                    animal.setDirection(foodDir);
                }
            } else {
                animal.setActionState("Tìm thức ăn");
                wander(animal);
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
                animal.setActionState("Trốn trong rừng");
                new PassiveStrategy().execute(animal, env);
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
                double distance = prey.getRenderPosition().distanceTo(other.getRenderPosition());
                if (distance < minDistance && distance <= visionRange) {
                    minDistance = distance;
                    nearest = other;
                }
            }
        }
        return nearest;
    }

    private Vector2D findNearestForest(Animal animal, Environment env) {
        java.util.List<Vector2D> candidates = new java.util.ArrayList<>();
        double minDistance = Double.MAX_VALUE;
        int searchRange = 5;

        for (int dx = -searchRange; dx <= searchRange; dx++) {
            for (int dy = -searchRange; dy <= searchRange; dy++) {
                int x = (int) animal.getPosition().getX() + dx;
                int y = (int) animal.getPosition().getY() + dy;

                if (env.getGrid().getTile(x, y) != null
                        && env.getGrid().getTile(x, y).getType() == TerrainType.FOREST) {
                    Vector2D forestPos = new Vector2D(x, y);
                    double distance = animal.getPosition().distanceTo(forestPos);
                    if (distance < minDistance - 0.25) {
                        minDistance = distance;
                        candidates.clear();
                        candidates.add(forestPos);
                    } else if (distance <= minDistance + 1.5) {
                        if (candidates.isEmpty()) {
                            minDistance = distance;
                        }
                        candidates.add(forestPos);
                    }
                }
            }
        }

        if (candidates.isEmpty()) {
            return null;
        }
        Vector2D target = candidates.get(animal.pickPathRandomIndex(candidates.size()));
        return target.subtract(animal.getPosition()).normalize();
    }

    private void wander(Animal animal) {
        animal.pickRandomWanderDirection();
    }

    @Override
    public String getName() {
        return "Scared";
    }
}
