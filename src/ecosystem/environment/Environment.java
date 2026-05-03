package ecosystem.environment;

import ecosystem.entities.Animal;
import ecosystem.entities.Entity;
import ecosystem.entities.Plant;
import ecosystem.physics.Vector2D;
import ecosystem.terrain.Grid;

import java.util.ArrayList;
import java.util.List;

public class Environment {
    private Grid grid;
    private List<Animal> animals;
    private List<Plant> plants;
    private Season season;

    public Environment(int width, int height) {
        this.grid = new Grid(width, height);
        this.animals = new ArrayList<>();
        this.plants = new ArrayList<>();
        this.season = Season.SPRING;
    }

    public void addAnimal(Animal animal) {
        animals.add(animal);
    }

    public void addPlant(Plant plant) {
        plants.add(plant);
    }

    public void removeAnimal(Animal animal) {
        animals.remove(animal);
    }

    public void removePlant(Plant plant) {
        plants.remove(plant);
    }

    public List<Animal> getAnimals() {
        return animals;
    }

    public List<Plant> getPlants() {
        return plants;
    }

    public Grid getGrid() {
        return grid;
    }

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    public void nextSeason() {
        season = season.next();
    }

    public boolean isWalkable(int x, int y) {
        return grid.isWalkable(x, y);
    }

    public double getSpeedModifier(int x, int y) {
        return grid.getSpeedModifier(x, y);
    }

    public Entity findNearestFood(Animal animal) {
        if (animal.isPredator()) {
            return findNearestPrey(animal);
        }
        
        Entity nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Plant plant : plants) {
            if (plant.isEdible()) {
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
            if (other != hunter && !other.isPredator()) {
                double distance = hunter.getPosition().distanceTo(other.getPosition());
                if (distance < minDistance) {
                    minDistance = distance;
                    nearest = other;
                }
            }
        }
        return nearest;
    }

    public boolean hasWaterNearby(Animal animal) {
        int x = (int) animal.getPosition().getX();
        int y = (int) animal.getPosition().getY();
        int range = 2;

        for (int dx = -range; dx <= range; dx++) {
            for (int dy = -range; dy <= range; dy++) {
                if (grid.getTile(x + dx, y + dy) != null &&
                    grid.getTile(x + dx, y + dy).getType().getName().equals("Nước")) {
                    return true;
                }
            }
        }
        return false;
    }

    public void update() {
        // Update plants
        for (Plant plant : plants) {
            plant.update();
        }

        // Plant reproduction based on season
        if (season == Season.SPRING) {
            reproducePlants();
        }

        // Update animals
        for (Animal animal : animals) {
            if (animal.isAlive()) {
                animal.act(this);
            }
        }

        // Remove dead animals
        animals.removeIf(animal -> !animal.isAlive());
    }

    private void reproducePlants() {
        if (Math.random() < 0.1) { // 10% chance to spawn new plant
            int x = (int) (Math.random() * grid.getWidth());
            int y = (int) (Math.random() * grid.getHeight());
            
            if (grid.isWalkable(x, y)) {
                String type = Math.random() < 0.7 ? "Cỏ" : "Cây ăn quả";
                Plant plant = new Plant(
                    new Vector2D(x, y),
                    type,
                    true,
                    type.equals("Cỏ") ? 5.0 : 10.0
                );
                addPlant(plant);
            }
        }
    }

    public enum Season {
        SPRING("Mùa Xuân", 1.2, "Sinh sản nhiều (1.2x)"),
        SUMMER("Mùa Hạ", 1.0, "Bình thường (1.0x)"),
        AUTUMN("Mùa Thu", 0.8, "Giảm dần (0.8x)"),
        WINTER("Mùa Đông", 0.5, "Ít sinh sản (0.5x)");

        private final String name;
        private final double populationMultiplier;
        private final String description;

        Season(String name, double populationMultiplier, String description) {
            this.name = name;
            this.populationMultiplier = populationMultiplier;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public double getPopulationMultiplier() {
            return populationMultiplier;
        }

        public String getDescription() {
            return description;
        }

        public Season next() {
            Season[] seasons = values();
            return seasons[(this.ordinal() + 1) % seasons.length];
        }
    }
}
