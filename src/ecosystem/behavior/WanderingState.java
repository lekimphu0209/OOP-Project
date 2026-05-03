package ecosystem.behavior;

import ecosystem.entities.Animal;
import ecosystem.environment.Environment;

public class WanderingState implements State {
    @Override
    public void handle(Animal animal, Environment env) {
        // Random movement handled by strategy
        if (animal.getHunger() > 5) {
            animal.setState(new HungryState());
        } else if (animal.getThirst() > 5) {
            animal.setState(new ThirstyState());
        }
    }

    @Override
    public String getName() {
        return "Wandering";
    }
}
