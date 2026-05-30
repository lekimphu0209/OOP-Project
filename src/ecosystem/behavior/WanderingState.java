package ecosystem.behavior;

import ecosystem.entities.Animal;
import ecosystem.environment.Environment;

public class WanderingState implements State {
    @Override
    public void handle(Animal animal, Environment env) {
        // Random movement handled by strategy
        // State transitions removed - keep simple wandering
    }

    @Override
    public String getName() {
        return "Wandering";
    }
}
