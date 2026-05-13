package ecosystem.behavior;

import ecosystem.entities.Animal;
import ecosystem.environment.Environment;
import ecosystem.physics.Vector2D;

import java.util.Random;

public class PassiveStrategy implements SurvivalStrategy {
    private Random random = new Random();

    @Override
    public void execute(Animal animal, Environment env) {
        // Random wandering
        double angle = random.nextDouble() * 2 * Math.PI;
        Vector2D direction = new Vector2D(Math.cos(angle), Math.sin(angle));
        animal.setDirection(direction);
    }

    @Override
    public String getName() {
        return "Passive";
    }
}
