package ecosystem.behavior;

import ecosystem.entities.Animal;
import ecosystem.environment.Environment;

public interface SurvivalStrategy {
    void execute(Animal animal, Environment env);
    String getName();
}
