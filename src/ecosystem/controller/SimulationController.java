package ecosystem.controller;

import ecosystem.environment.SeasonManager;
import ecosystem.test.TestConfig;

/**
 * LEGACY CONTROLLER - God Class (172 lines)
 * 
 * This is the old simulation controller that combined game loop, spawning, and factory logic.
 * Still used by MainLegacy.java for backward compatibility.
 * 
 * For the new modular simulation system, see:
 * - ecosystem.core.simulation.SimulationLoop
 * - ecosystem.core.simulation.SpawnManager
 * - ecosystem.core.world.GridManager
 * - ecosystem.core.world.EntityManager
 */

import ecosystem.SimulationConfig;
import ecosystem.entities.*;
import ecosystem.environment.Environment;
import ecosystem.physics.Vector2D;
import ecosystem.terrain.TmxMapLoader;
import ecosystem.view.GraphicalView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class SimulationController {
    private Environment environment;
    private GraphicalView view;
    private Timer timer;
    private Random random = new Random();

    public SimulationController(int width, int height) {
        this.environment = createEnvironment(width, height);
        this.view = new GraphicalView(environment);
        this.timer = new Timer(SimulationConfig.TICK_MS, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateSimulation();
            }
        });
    }

    private Environment createEnvironment(int width, int height) {
        try {
            // Load map from TMX file
            ecosystem.terrain.Grid grid = TmxMapLoader.loadGrid("resources/maps/map.tmx");
            return new Environment(grid);
        } catch (Exception e) {
            System.out.println("Không thể load file map.tmx, sử dụng map ngẫu nhiên");
            e.printStackTrace();
            return new Environment(width, height);
        }
    }

    public void initialize() {
        if (!SimulationConfig.MANUAL_SPAWNING) {
            spawnInitialEntities();
        }

        view.setController(this);
        if (SimulationConfig.MANUAL_SPAWNING) {
            view.setActionMode("inspect",
                    "Map trống — chọn loài, bấm «Đặt động vật», nhấp ô trống. Dùng «Trồng cây» cho thức ăn.");
        }
        view.setVisible(true);
        timer.start();
    }

    private void spawnInitialEntities() {
        spawnInitialAnimals();
        spawnAquaticAnimals();
        spawnInitialPlants();
    }

    /**
     * Đặt một con vật lên ô (x, y). type: 0=Thỏ, 1=Hươu, 2=Sói, 3=Hổ, 4=Voi, 5=Người, 6=Cá, 7=Vịt, 8=Cá sấu.
     */
    public boolean spawnAnimalAt(int type, int x, int y) {
        if (!isValidSpawnPosition(x, y, type)) {
            return false;
        }
        Animal prototype = createAnimal(type, new Vector2D(x, y));
        if (prototype == null) {
            return false;
        }
        if (environment.hasReachedPopulationLimit(prototype.getClass())) {
            return false;
        }
        for (Animal existing : environment.getAnimals()) {
            if (existing != null && existing.isAlive()
                    && (int) existing.getPosition().getX() == x
                    && (int) existing.getPosition().getY() == y) {
                return false;
            }
        }
        environment.addAnimal(prototype);
        view.update();
        return true;
    }

    public static String getAnimalTypeName(int type) {
        switch (type) {
            case 0: return "Thỏ";
            case 1: return "Hươu";
            case 2: return "Sói";
            case 3: return "Hổ";
            case 4: return "Voi";
            case 5: return "Người";
            case 6: return "Cá";
            case 7: return "Vịt";
            case 8: return "Cá sấu";
            default: return "?";
        }
    }

    private void spawnInitialAnimals() {
        for (int i = 0; i < 40; i++) {
            addRandomAnimal();
        }
    }

    private void spawnAquaticAnimals() {
        // Use TestConfig if test mode is enabled
        if (TestConfig.ENABLE_TEST_MODE) {
            // Only spawn if fish or duck are enabled
            if (TestConfig.isAnimalTypeEnabled(6)) { // Fish
                for (int i = 0; i < 10; i++)
                    addAnimalByType(6);
            }
            if (TestConfig.isAnimalTypeEnabled(7)) { // Duck
                for (int i = 0; i < 10; i++)
                    addAnimalByType(7);
            }
            return;
        }

        // Ensure some Fish and Ducks appear right from the start
        for (int i = 0; i < 10; i++) {
            addAnimalByType(6); // Fish
            addAnimalByType(7); // Duck
        }
    }

    private void spawnInitialPlants() {
        for (int i = 0; i < 80; i++) {
            addRandomPlant();
        }
    }

    private void addRandomAnimal() {
        int type = selectRandomAnimalType();
        addAnimalByType(type);
    }

    private int selectRandomAnimalType() {
        // Use TestConfig if test mode is enabled
        if (TestConfig.ENABLE_TEST_MODE) {
            java.util.Set<Integer> enabledTypes = TestConfig.getEnabledAnimalTypes();
            if (enabledTypes.isEmpty()) {
                return -1; // No animals enabled
            }
            // Randomly select from enabled types
            return enabledTypes.toArray(new Integer[0])[random.nextInt(enabledTypes.size())];
        }

        double r = random.nextDouble();

        // Weighted probability (balanced): 60% Prey, 25% Predators, 15% Special
        if (r < 0.6) {
            // Prey types: Rabbit(0), Deer(1), Elephant(4), Fish(6), Duck(7)
            int[] preyTypes = { 0, 1, 4, 6, 7 };
            return preyTypes[random.nextInt(preyTypes.length)];
        } else if (r < 0.85) {
            // Predator types: Wolf(2), Tiger(3), Crocodile(8)
            int[] predatorTypes = { 2, 3, 8 };
            return predatorTypes[random.nextInt(predatorTypes.length)];
        } else {
            // Special: Human(5)
            return 5;
        }
    }

    private void addAnimalByType(int type) {
        Vector2D position = findValidSpawnPosition(type);
        if (position != null) {
            Animal animal = createAnimal(type, position);
            if (animal != null) {
                // Kiểm tra giới hạn quần thể trước khi thêm
                if (!environment.hasReachedPopulationLimit(animal.getClass())) {
                    environment.addAnimal(animal);
                }
            }
        }
    }

    private Vector2D findValidSpawnPosition(int type) {
        int width = environment.getGrid().getWidth();
        int height = environment.getGrid().getHeight();

        // Try up to 10 times to find a valid spot
        for (int attempt = 0; attempt < 10; attempt++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);

            if (isValidSpawnPosition(x, y, type)) {
                return new Vector2D(x, y);
            }
        }
        return null;
    }

    private boolean isValidSpawnPosition(int x, int y, int type) {
        boolean isWater = environment.getGrid().getTile(x, y) != null &&
                environment.getGrid().getTile(x, y).getType() == ecosystem.terrain.TerrainType.WATER;

        if (isWater) {
            // Water animals: Fish(6), Duck(7), Crocodile(8)
            return type == 6 || type == 7 || type == 8;
        } else {
            // Land animals: all except Fish
            return type != 6 && environment.isWalkable(x, y);
        }
    }

    private Animal createAnimal(int type, Vector2D position) {
        switch (type) {
            case 0:
                return new Rabbit(position);
            case 1:
                return new Deer(position);
            case 2:
                return new Wolf(position);
            case 3:
                return new Tiger(position);
            case 4:
                return new Elephant(position);
            case 5:
                return new Human(position);
            case 6:
                return new Fish(position);
            case 7:
                return new Duck(position);
            case 8:
                return new Crocodile(position);
            default:
                return null;
        }
    }

    private void addRandomPlant() {
        int x = random.nextInt(environment.getGrid().getWidth());
        int y = random.nextInt(environment.getGrid().getHeight());

        if (!environment.isWalkable(x, y))
            return;

        Vector2D position = new Vector2D(x, y);
        String type = random.nextBoolean() ? "Cỏ" : "Cây ăn quả";
        Plant plant = new Plant(position, type, true, type.equals("Cỏ") ? 5.0 : 10.0);
        environment.addPlant(plant);
    }

    private void updateSimulation() {
        environment.update();
        view.update();

        // Use season tick for automatic season change
        environment.getSeasonManager().tick();

        SeasonManager.Season season = environment.getSeason();
        int currentAnimals = environment.getAnimals().size();
        int currentPlants = environment.getPlants().size();

        if (!SimulationConfig.MANUAL_SPAWNING) {
            if (currentAnimals < season.getMinAnimals()) {
                addRandomAnimal();
            } else if (currentAnimals < season.getMaxAnimals()) {
                double multiplier = season.getPopulationMultiplier();
                if (random.nextDouble() < 0.05 * multiplier / SimulationConfig.TICK_SCALE
                        * SimulationConfig.WORLD_SPAWN_CHANCE_MULT) {
                    addRandomAnimal();
                }
            }

            if (currentPlants < season.getMinPlants()) {
                addRandomPlant();
            } else if (random.nextDouble() < 0.03 * SimulationConfig.WORLD_SPAWN_CHANCE_MULT) {
                addRandomPlant();
            }
        }
    }

    public void togglePause() {
        if (timer.isRunning()) {
            timer.stop();
        } else {
            timer.start();
        }
    }

    public boolean isPaused() {
        return !timer.isRunning();
    }

    public void stop() {
        timer.stop();
    }
}
