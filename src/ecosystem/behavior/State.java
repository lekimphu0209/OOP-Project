package ecosystem.behavior;

import ecosystem.entities.Animal;
import ecosystem.environment.Environment;

public interface State {
    void handle(Animal animal, Environment env);
    String getName();
}
