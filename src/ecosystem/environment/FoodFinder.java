package ecosystem.environment;

import ecosystem.entities.Animal;
import ecosystem.entities.Entity;
import ecosystem.entities.Plant;

import java.util.ArrayList;
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
        return findPlantTarget(animal);
    }

    private Entity findPlantTarget(Animal animal) {
        List<Plant> candidates = new ArrayList<>();
        double minDistance = Double.MAX_VALUE;

        for (Plant plant : plants) {
            if (plant == null || !plant.isEdible() || !animal.canEat(plant)) {
                continue;
            }
            double distance = animal.getRenderPosition().distanceTo(plant.getPosition());
            if (distance < minDistance - 0.25) {
                minDistance = distance;
                candidates.clear();
                candidates.add(plant);
            } else if (distance <= minDistance + 2.0) {
                if (candidates.isEmpty()) {
                    minDistance = distance;
                }
                candidates.add(plant);
            }
        }

        if (candidates.isEmpty()) {
            return null;
        }
        return candidates.get(animal.pickPathRandomIndex(candidates.size()));
    }

    private Animal findNearestPrey(Animal hunter) {
        List<Animal> candidates = new ArrayList<>();
        double minDistance = Double.MAX_VALUE;

        for (Animal other : animals) {
            if (other == null || other == hunter || !other.isAlive() || !hunter.canEat(other)) {
                continue;
            }
            double distance = hunter.getRenderPosition().distanceTo(other.getRenderPosition());
            if (distance < minDistance - 0.25) {
                minDistance = distance;
                candidates.clear();
                candidates.add(other);
            } else if (distance <= minDistance + 2.0) {
                if (candidates.isEmpty()) {
                    minDistance = distance;
                }
                candidates.add(other);
            }
        }

        if (candidates.isEmpty()) {
            return null;
        }
        return candidates.get(hunter.pickPathRandomIndex(candidates.size()));
    }
}
