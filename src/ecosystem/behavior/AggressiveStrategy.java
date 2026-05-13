package ecosystem.behavior;

import ecosystem.entities.Animal;
import ecosystem.environment.Environment;
import ecosystem.physics.Vector2D;

import java.util.List;

public class AggressiveStrategy implements SurvivalStrategy {
    private double visionRange = 5.0;

    @Override
    public void execute(Animal animal, Environment env) {
        // When hungry/aggressive, hunt even larger prey
        Animal prey = findNearestPrey(animal, env.getAnimals());
        
        if (prey != null) {
            Vector2D direction = prey.getPosition().subtract(animal.getPosition()).normalize();
            animal.setDirection(direction);
            
            if (animal.getPosition().distanceTo(prey.getPosition()) < 1.0) {
                prey.takeDamage((int)(animal.getAttackDamage() * 1.5)); // Bonus damage
            }
        } else {
            new PassiveStrategy().execute(animal, env);
        }
    }

    private Animal findNearestPrey(Animal hunter, List<Animal> animals) {
        Animal nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Animal other : animals) {
            if (other != hunter) {
                double distance = hunter.getPosition().distanceTo(other.getPosition());
                if (distance < minDistance && distance <= visionRange) {
                    minDistance = distance;
                    nearest = other;
                }
            }
        }
        return nearest;
    }

    @Override
    public String getName() {
        return "Aggressive";
    }
}
