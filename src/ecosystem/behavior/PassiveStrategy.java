package ecosystem.behavior;

import ecosystem.entities.Animal;
import ecosystem.entities.Entity;
import ecosystem.environment.Environment;
import ecosystem.physics.Vector2D;

import java.util.Random;

public class PassiveStrategy implements SurvivalStrategy {
    private Random random = new Random();

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
                Vector2D waterDir = env.findDirectionToNearestWater(animal, 6);
                if (waterDir != null) animal.setDirection(waterDir);
            }
            return;
        }
        
        if (animal.getHunger() > 30) {
            animal.setActionState("Tìm thức ăn");
            Entity food = animal.findFood(env);
            if (food != null) {
                double distance = animal.getPosition().distanceTo(food.getPosition());
                if (distance <= 1.0) {
                    animal.eat(food);
                } else {
                    // Di chuyển về phía thức ăn
                    Vector2D dir = food.getPosition().subtract(animal.getPosition()).normalize();
                    animal.setDirection(dir);
                }
            }
            return;
        }

        // Priority: reproduction (sau khi đã xử lý mệt, khát, đói)
        if (animal.canReproduce(env) && animal.sameSpeciesNearby(env)) {
            animal.setActionState("Sinh sản");
            animal.reproduce(env);
            return;
        }
        
        animal.setActionState("Đi lang thang");
        
        // Use AI memory to avoid repetition - try to find unvisited direction
        Vector2D bestDirection = findUnvisitedDirection(animal, env);
        if (bestDirection != null) {
            animal.setDirection(bestDirection);
        } else {
            // Random wandering as fallback
            double angle = random.nextDouble() * 2 * Math.PI;
            Vector2D direction = new Vector2D(Math.cos(angle), Math.sin(angle));
            animal.setDirection(direction);
        }
    }
    
    private Vector2D findUnvisitedDirection(Animal animal, Environment env) {
        int currentX = (int) animal.getPosition().getX();
        int currentY = (int) animal.getPosition().getY();
        
        // Check 8 directions
        Vector2D[] directions = {
            new Vector2D(1, 0), new Vector2D(-1, 0),
            new Vector2D(0, 1), new Vector2D(0, -1),
            new Vector2D(1, 1), new Vector2D(-1, -1),
            new Vector2D(1, -1), new Vector2D(-1, 1)
        };
        
        for (Vector2D dir : directions) {
            int newX = currentX + (int) dir.getX();
            int newY = currentY + (int) dir.getY();
            
            // Check if position is valid and unvisited
            if (newX >= 0 && newX < env.getGrid().getWidth() && 
                newY >= 0 && newY < env.getGrid().getHeight() &&
                env.isWalkable(newX, newY) &&
                !animal.hasVisitedPosition(newX, newY)) {
                return dir.normalize();
            }
        }
        
        return null; // All nearby positions visited
    }

    @Override
    public String getName() {
        return "Passive";
    }
}
