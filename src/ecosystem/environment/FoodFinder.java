package ecosystem.environment;

import ecosystem.entities.Animal;
import ecosystem.entities.Entity;
import ecosystem.entities.Plant;

import java.util.List;

/**
 * Finds food sources for animals.
 * Extracted from Environment to reduce file size.
 */
public class FoodFinder {
    private List<Plant> plants;
    private List<Animal> animals;

    public FoodFinder(List<Plant> plants, List<Animal> animals) {
        this.plants = plants;
        this.animals = animals;
    }

    public Entity findNearestFood(Animal animal) {
        if (animal.isPredator()) {
            return findNearestPrey(animal);
        }
        return findNearestPlant(animal);
    }

    private Entity findNearestPlant(Animal animal) {
        Entity nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Plant plant : plants) {
            if (plant.isEdible() && animal.canEat(plant)) {
                double distance = animal.getPosition().distanceTo(plant.getPosition());
                if (distance < minDistance) {
                    minDistance = distance;
                    nearest = plant;
                }
            }
        }
        return nearest;
    }

    private Animal findNearestPrey(Animal hunter) {
        Animal nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Animal other : animals) {
            if (other == null) continue;
            if (other != hunter && other.isAlive() && hunter.canEat(other)) {
                double distance = hunter.getPosition().distanceTo(other.getPosition());
                if (distance < minDistance) {
                    minDistance = distance;
                    nearest = other;
                }
            }
        }
        return nearest;
    }
}
