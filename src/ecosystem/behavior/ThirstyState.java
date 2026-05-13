package ecosystem.behavior;

import ecosystem.entities.Animal;
import ecosystem.environment.Environment;

public class ThirstyState implements State {
    @Override
    public void handle(Animal animal, Environment env) {
        if (animal.drink(env)) {
            animal.setThirst(0);
            animal.setState(new WanderingState());
        }
    }

    @Override
    public String getName() {
        return "Thirsty";
    }
}
