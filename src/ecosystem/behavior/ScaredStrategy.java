package ecosystem.behavior;

import ecosystem.entities.Animal;
import ecosystem.environment.Environment;
import ecosystem.physics.Vector2D;
import ecosystem.terrain.TerrainType;

import java.util.List;

public class ScaredStrategy implements SurvivalStrategy {
    private double visionRange = 5.0;

    @Override
    public void execute(Animal animal, Environment env) {
        Animal threat = findNearestThreat(animal, env.getAnimals());
        
        if (threat != null) {
            // Check if prey can hide in forest
            int currentX = (int) animal.getPosition().getX();
            int currentY = (int) animal.getPosition().getY();
            
            if (env.getGrid().getTile(currentX, currentY).getType() == TerrainType.FOREST) {
                // Prey is in forest - wolves can't enter, so prey is safe
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
            } else {
                // Move away from threat
                Vector2D direction = animal.getPosition().subtract(threat.getPosition()).normalize();
                animal.setDirection(direction);
                animal.setSpeedBoost(1.3); // Speed boost when scared
            }
        } else {
            animal.setSpeedBoost(1.0); // Reset speed
            new PassiveStrategy().execute(animal, env);
        }
    }

    private Animal findNearestThreat(Animal prey, List<Animal> animals) {
        Animal nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Animal other : animals) {
            if (other != prey && other.isPredator()) {
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
