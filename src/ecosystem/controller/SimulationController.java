package ecosystem.controller;

import ecosystem.entities.*;
import ecosystem.environment.Environment;
import ecosystem.physics.Vector2D;
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
        this.environment = new Environment(width, height);
        this.view = new GraphicalView(environment);
        this.timer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateSimulation();
            }
        });
    }

    public void initialize() {
        // Add initial animals
        for (int i = 0; i < 40; i++) {
            addRandomAnimal();
        }
        
        // Ensure some Fish and Ducks appear right from the start
        for (int i = 0; i < 10; i++) {
            addAnimalByType(6); // Fish
            addAnimalByType(7); // Duck
        }

        // Add initial plants
        for (int i = 0; i < 80; i++) {
            addRandomPlant();
        }

        view.setVisible(true);
        timer.start();
    }

    private void addRandomAnimal() {
        double r = random.nextDouble();
        int type;
        
        // Weighted probability: 70% Prey, 20% Predators, 10% Special
        if (r < 0.7) {
            // Prey types: Rabbit(0), Deer(1), Elephant(4), Fish(6), Duck(7)
            int[] preyTypes = {0, 1, 4, 6, 7};
            type = preyTypes[random.nextInt(preyTypes.length)];
        } else if (r < 0.9) {
            // Predator types: Wolf(2), Tiger(3), Crocodile(8)
            int[] predatorTypes = {2, 3, 8};
            type = predatorTypes[random.nextInt(predatorTypes.length)];
        } else {
            // Special: Human(5)
            type = 5;
        }
        
        addAnimalByType(type);
    }

    private void addAnimalByType(int type) {
        int width = environment.getGrid().getWidth();
        int height = environment.getGrid().getHeight();
        
        // Try up to 10 times to find a valid spot
        for (int attempt = 0; attempt < 10; attempt++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            Vector2D position = new Vector2D(x, y);
            
            boolean isWater = environment.getGrid().getTile(x, y) != null && 
                             environment.getGrid().getTile(x, y).getType() == ecosystem.terrain.TerrainType.WATER;
            
            Animal animal = null;
            switch (type) {
                case 0: animal = new Rabbit(position); break;
                case 1: animal = new Deer(position); break;
                case 2: animal = new Wolf(position); break;
                case 3: animal = new Tiger(position); break;
                case 4: animal = new Elephant(position); break;
                case 5: animal = new Human(position); break;
                case 6: animal = new Fish(position); break;
                case 7: animal = new Duck(position); break;
                case 8: animal = new Crocodile(position); break;
            }

            if (animal != null) {
                boolean allowed = false;
                if (isWater) {
                    if (type == 6 || type == 7 || type == 8) allowed = true; // Fish, Duck, Crocodile
                } else {
                    if (type != 6 && environment.isWalkable(x, y)) allowed = true; // Not Fish, on land
                }

                if (allowed) {
                    environment.addAnimal(animal);
                    return;
                }
            }
        }
    }

    private void addRandomPlant() {
        int x = random.nextInt(environment.getGrid().getWidth());
        int y = random.nextInt(environment.getGrid().getHeight());
        
        if (!environment.isWalkable(x, y)) return;

        Vector2D position = new Vector2D(x, y);
        String type = random.nextBoolean() ? "Cỏ" : "Cây ăn quả";
        Plant plant = new Plant(position, type, true, type.equals("Cỏ") ? 5.0 : 10.0);
        environment.addPlant(plant);
    }

    private void updateSimulation() {
        environment.update();

        // Change season every 20 ticks
        if (random.nextInt(20) == 0) {
            environment.nextSeason();
        }

        // Occasionally add new animals based on season
        double multiplier = environment.getSeason().getPopulationMultiplier();
        if (random.nextDouble() < 0.05 * multiplier) {
            addRandomAnimal();
        }
    }

    public void stop() {
        timer.stop();
    }
}
