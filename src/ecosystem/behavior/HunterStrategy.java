package ecosystem.behavior;

import ecosystem.entities.Animal;
import ecosystem.environment.Environment;
import ecosystem.physics.Vector2D;
import ecosystem.terrain.TerrainType;

import java.util.List;

public class HunterStrategy implements SurvivalStrategy {
    private double visionRange = 5.0;

    @Override
    public void execute(Animal animal, Environment env) {
        Animal prey = findNearestPrey(animal, env.getAnimals());

        if (prey != null) {
            Vector2D direction = prey.getPosition().subtract(animal.getPosition()).normalize();
            animal.setDirection(direction);

            // Check if prey is in forest - wolves can't enter
            int preyX = (int) prey.getPosition().getX();
            int preyY = (int) prey.getPosition().getY();

            if (env.getGrid().getTile(preyX, preyY).getType() == TerrainType.FOREST) {
                // Prey is hiding in forest, can't chase
                animal.setSpeedBoost(1.0);
                new PassiveStrategy().execute(animal, env);
                return;
            }

            // Speed boost when chasing prey
            animal.setSpeedBoost(1.8);

            // Attack if close enough
            if (animal.getPosition().distanceTo(prey.getPosition()) < 1.0) {
                prey.takeDamage(animal.getAttackDamage());
                animal.eat(); // Reset hunger and heal slightly
            }
        } else {
            animal.setSpeedBoost(1.0); // Reset speed
            new PassiveStrategy().execute(animal, env);
        }
    }

    private Animal findNearestPrey(Animal hunter, List<Animal> animals) {
        Animal nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Animal other : animals) {
            if (other != hunter && isPrey(hunter, other)) {
                double distance = hunter.getPosition().distanceTo(other.getPosition());
                if (distance < minDistance && distance <= visionRange) {
                    minDistance = distance;
                    nearest = other;
                }
            }
        }
        return nearest;
    }

    private boolean isPrey(Animal hunter, Animal other) {
        return hunter.isPredator() && !other.isPredator();
    }

    @Override
    public String getName() {
        return "Hunter";
    }
}
