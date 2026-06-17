package ecosystem.environment;

/**
 * LEGACY ENVIRONMENT - God Class (352 lines)
 * 
 * This is the old environment system that combined grid, entities, seasons, XML parsing, and observer pattern.
 * Still used by MainLegacy.java for backward compatibility.
 * 
 * For the new modular world system, see:
 * - ecosystem.core.world.GridManager
 * - ecosystem.core.world.EntityManager
 * - ecosystem.core.world.FoodFinder
 */

import ecosystem.entities.Animal;
import ecosystem.entities.Entity;
import ecosystem.entities.Plant;
import ecosystem.physics.PhysicsSystem;
import ecosystem.physics.Vector2D;
import ecosystem.terrain.Grid;
import ecosystem.view.IObserver;

import java.util.ArrayList;
import java.util.List;

public class Environment implements ISubject {
    private Grid grid;
    private List<Animal> animals;
    private List<Plant> plants;
    private SeasonManager seasonManager;
    private GridHelper gridHelper;
    private FoodFinder foodFinder;
    private List<IObserver> observers;
    private final PhysicsSystem physicsSystem = new PhysicsSystem();

    public Environment(int width, int height) {
        this.grid = new Grid(width, height);
        this.animals = new ArrayList<>();
        this.plants = new ArrayList<>();
        this.observers = new ArrayList<>();
        this.seasonManager = new SeasonManager();
        this.gridHelper = new GridHelper(grid);
        this.foodFinder = new FoodFinder(plants, animals);
    }

    public Environment(Grid grid) {
        this.grid = grid;
        this.animals = new ArrayList<>();
        this.plants = new ArrayList<>();
        this.observers = new ArrayList<>();
        this.seasonManager = new SeasonManager();
        this.gridHelper = new GridHelper(grid);
        this.foodFinder = new FoodFinder(plants, animals);
    }

    // ==========================================
    // IMPLEMENT OBSERVER PATTERN (ISubject)
    // ==========================================
    @Override
    public void registerObserver(IObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(IObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (IObserver observer : observers) {
            observer.updateView();
        }
    }

    public void addAnimal(Animal animal) {
        animals.add(animal);
    }

    public void addPlant(Plant plant) {
        plants.add(plant);
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

    public SeasonManager.Season getSeason() {
        return seasonManager.getSeason();
    }

    public SeasonManager getSeasonManager() {
        return seasonManager;
    }

    public void setSeason(SeasonManager.Season season) {
        seasonManager.setSeason(season);
    }

    public void nextSeason() {
        seasonManager.nextSeason();
    }

    public boolean isWalkable(int x, int y) {
        return gridHelper.isWalkable(x, y);
    }

    public double getSpeedModifier(int x, int y) {
        return gridHelper.getSpeedModifier(x, y);
    }

    public Entity findNearestFood(Animal animal) {
        return foodFinder.findNearestFood(animal);
    }

    public boolean hasWaterNearby(Animal animal) {
        return gridHelper.hasWaterNearby(animal);
    }

    public Vector2D findDirectionToNearestWater(Animal animal, int searchRange) {
        return gridHelper.findDirectionToNearestWater(animal, searchRange);
    }

    public PhysicsSystem getPhysicsSystem() {
        return physicsSystem;
    }

    public void update() {
        updateEntities();
        handleSeasonEffects();
        notifyObservers();
    }

    private void updateEntities() {
        updatePlants();
        updateAnimals();
        removeDeadEntities();
        handlePlantRespawn();
        if (!ecosystem.SimulationConfig.MANUAL_SPAWNING) {
            spawnRandomPlants();
        }
    }

    private void updatePlants() {
        for (Plant plant : plants) {
            plant.update();
        }
    }

    private void updateAnimals() {
        physicsSystem.getCollisionDetector().rebuildOccupancyIndex(this);
        List<Animal> sorted = physicsSystem.getYieldMediator().sortForInteraction(animals);
        for (Animal animal : sorted) {
            animal.act(this);
        }
    }

    private void removeDeadEntities() {
        animals.removeIf(animal -> animal == null || !animal.isAlive());
        plants.removeIf(plant -> plant == null || !plant.isAlive()); // Xóa thực vật đã chết
    }

    private void handlePlantRespawn() {
        for (Plant plant : plants) {
            if (plant.canRespawn()) {
                Vector2D newPosition = findRandomWalkablePosition();
                if (newPosition != null) {
                    plant.respawn(newPosition);
                }
            }
        }
    }

    private void spawnRandomPlants() {
        // Spawn thực vật mới để thay thế thực vật đã bị ăn
        if (plants.size() < 80 && Math.random() < 0.05 / ecosystem.SimulationConfig.TICK_SCALE
                * ecosystem.SimulationConfig.WORLD_SPAWN_CHANCE_MULT) {
            Vector2D position = findRandomWalkablePosition();
            if (position != null) {
                Plant plant = createRandomPlant(position);
                addPlant(plant);
            }
        }
    }

    private void handleSeasonEffects() {
        if (seasonManager.getSeason() == SeasonManager.Season.SPRING) {
            reproducePlants();
        }
    }

    private void reproducePlants() {
        if (Math.random() < 0.1) {
            Vector2D position = findRandomWalkablePosition();
            if (position != null) {
                Plant plant = createRandomPlant(position);
                addPlant(plant);
            }
        }
    }

    private Vector2D findRandomWalkablePosition() {
        int x = (int) (Math.random() * grid.getWidth());
        int y = (int) (Math.random() * grid.getHeight());
        
        if (grid.isWalkable(x, y)) {
            return new Vector2D(x, y);
        }
        return null;
    }

    private Plant createRandomPlant(Vector2D position) {
        String type = Math.random() < 0.7 ? "Cỏ" : "Cây ăn quả";
        double nutrition = type.equals("Cỏ") ? 5.0 : 10.0;
        return new Plant(position, type, true, nutrition);
    }

    // Giới hạn quần thể theo loài
    private static final int MAX_RABBIT = 8;  // Giảm từ 10 để predator có đủ thức ăn
    private static final int MAX_DEER = 4;    // Giảm từ 5
    private static final int MAX_FISH = 8;    // Giảm từ 10
    private static final int MAX_WOLF = 7;    // Tăng từ 5
    private static final int MAX_TIGER = 7;   // Tăng từ 5
    private static final int MAX_CROCODILE = 7; // Tăng từ 5
    private static final int MAX_HUMAN = 5;
    private static final int MAX_ELEPHANT = 5;

    public int countSpecies(Class<?> speciesClass) {
        int count = 0;
        for (Animal animal : animals) {
            if (animal == null) continue;
            if (animal.isAlive() && speciesClass.isInstance(animal)) {
                count++;
            }
        }
        return count;
    }

    public boolean hasReachedPopulationLimit(Class<?> speciesClass) {
        if (speciesClass == ecosystem.entities.Rabbit.class) {
            return countSpecies(speciesClass) >= MAX_RABBIT;
        } else if (speciesClass == ecosystem.entities.Deer.class) {
            return countSpecies(speciesClass) >= MAX_DEER;
        } else if (speciesClass == ecosystem.entities.Fish.class) {
            return countSpecies(speciesClass) >= MAX_FISH;
        } else if (speciesClass == ecosystem.entities.Wolf.class) {
            return countSpecies(speciesClass) >= MAX_WOLF;
        } else if (speciesClass == ecosystem.entities.Tiger.class) {
            return countSpecies(speciesClass) >= MAX_TIGER;
        } else if (speciesClass == ecosystem.entities.Crocodile.class) {
            return countSpecies(speciesClass) >= MAX_CROCODILE;
        } else if (speciesClass == ecosystem.entities.Human.class) {
            return countSpecies(speciesClass) >= MAX_HUMAN;
        } else if (speciesClass == ecosystem.entities.Elephant.class) {
            return countSpecies(speciesClass) >= MAX_ELEPHANT;
        }
        return false;
    }

    public int getMaxPopulation(Class<?> speciesClass) {
        if (speciesClass == ecosystem.entities.Rabbit.class) return MAX_RABBIT;
        else if (speciesClass == ecosystem.entities.Deer.class) return MAX_DEER;
        else if (speciesClass == ecosystem.entities.Fish.class) return MAX_FISH;
        else if (speciesClass == ecosystem.entities.Wolf.class) return MAX_WOLF;
        else if (speciesClass == ecosystem.entities.Tiger.class) return MAX_TIGER;
        else if (speciesClass == ecosystem.entities.Crocodile.class) return MAX_CROCODILE;
        else if (speciesClass == ecosystem.entities.Human.class) return MAX_HUMAN;
        else if (speciesClass == ecosystem.entities.Elephant.class) return MAX_ELEPHANT;
        return Integer.MAX_VALUE;
    }
}
