package ecosystem.behavior;

import ecosystem.SimulationConfig;
import ecosystem.entities.Animal;
import ecosystem.entities.Entity;
import ecosystem.environment.Environment;
import ecosystem.physics.Vector2D;

public class PassiveStrategy implements SurvivalStrategy {

    @Override
    public void execute(Animal animal, Environment env) {
        if (animal.isTired()) {
            animal.rest();
            return;
        }

        if (animal.getThirst() > SimulationConfig.HUNGER_SEEK_THRESHOLD) {
            if (env.hasWaterNearby(animal)) {
                animal.drink(env);
                animal.setActionState("Đang uống");
            } else {
                animal.setActionState("Tìm nước");
                Vector2D waterDir = env.findDirectionToNearestWater(animal, 6);
                if (waterDir != null) {
                    animal.setDirection(waterDir);
                } else {
                    animal.pickRandomWanderDirection();
                }
            }
            return;
        }

        if (animal.getHunger() > SimulationConfig.HUNGER_SEEK_THRESHOLD) {
            animal.setActionState("Tìm thức ăn");
            Entity food = animal.findFood(env);
            if (food != null) {
                if (animal.isWithinRange(food.getPosition(), SimulationConfig.HERBIVORE_EAT_RADIUS)) {
                    animal.eat(food);
                    animal.setActionState("Đang ăn");
                } else {
                    Vector2D dir = food.getPosition().subtract(animal.getRenderPosition()).normalize();
                    animal.setDirection(dir);
                }
            } else {
                animal.pickRandomWanderDirection();
            }
            return;
        }

        if (animal.canReproduce(env) && animal.sameSpeciesNearby(env)) {
            animal.setActionState("Sinh sản");
            animal.reproduce(env);
            return;
        }

        animal.setActionState("Đi lang thang");
        animal.pickRandomWanderDirection();
    }

    @Override
    public String getName() {
        return "Passive";
    }
}
