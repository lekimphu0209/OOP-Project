package ecosystem.behavior;

import ecosystem.entities.Animal;
import ecosystem.environment.Environment;

public class HungryState implements State {
    @Override
    public void handle(Animal animal, Environment env) {
        if (animal.findFood(env) != null) {
            animal.eat();
            animal.setHunger(0);
            animal.setState(new WanderingState());
        }
    }

    @Override
    public String getName() {
        return "Hungry";
    }
}
