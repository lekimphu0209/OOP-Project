package ecosystem.behavior;

import ecosystem.SimulationConfig;
import ecosystem.audio.SoundManager;
import ecosystem.entities.Animal;
import ecosystem.entities.Entity;
import ecosystem.environment.Environment;
import ecosystem.physics.Vector2D;
import ecosystem.terrain.TerrainType;

public class HunterStrategy implements SurvivalStrategy {
    private double visionRange = 4.0;

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
                    wander(animal);
                }
            }
            return;
        }

        boolean needsFood = animal.getHunger() > SimulationConfig.HUNGER_SEEK_THRESHOLD;

        if (!needsFood && animal.canReproduce(env) && animal.sameSpeciesNearby(env)) {
            animal.setActionState("Sinh sản");
            animal.reproduce(env);
            return;
        }

        if (needsFood) {
            animal.setActionState("Tìm mồi");
        } else {
            animal.setActionState("Săn mồi");
        }

        if (!needsFood && Math.random() < 0.05) {
            animal.setSpeedBoost(1.0);
            animal.setActionState("Bỏ săn");
            return;
        }

        Animal prey = null;
        Entity foodTarget = animal.findFood(env);
        if (foodTarget instanceof Animal) {
            Animal candidate = (Animal) foodTarget;
            double preyDist = animal.getRenderPosition().distanceTo(candidate.getRenderPosition());
            if (preyDist <= visionRange) {
                prey = candidate;
            }
        }

        if (prey != null) {
            // Gầm khi phát hiện con mồi
            String soundAction = "roar";
            if (animal.getClass().getSimpleName().equalsIgnoreCase("Wolf")) {
                soundAction = "howl";
            }
            SoundManager.getInstance().playAnimalSound(animal.getClass().getSimpleName().toLowerCase(), soundAction);
            int preyCount = 0;
            for (Animal a : env.getAnimals()) {
                if (a != null && a.isAlive() && a.getClass().equals(prey.getClass())) {
                    preyCount++;
                }
            }
            if (preyCount < SimulationConfig.minPreyCountToHunt()) {
                animal.setSpeedBoost(1.0);
                animal.setActionState("Bảo tồn");
                wander(animal);
                return;
            }

            Vector2D direction = prey.getRenderPosition()
                    .subtract(animal.getRenderPosition())
                    .normalize();
            if (direction.magnitude() > 0.01) {
                animal.setDirection(direction);
            }

            int preyX = (int) prey.getPosition().getX();
            int preyY = (int) prey.getPosition().getY();

            if (env.getGrid().getTile(preyX, preyY).getType() == TerrainType.FOREST) {
                animal.setSpeedBoost(1.0);
                animal.setActionState("Mất mồi");
                wander(animal);
                return;
            }

            boolean inWater = env.getGrid().getTile((int) animal.getPosition().getX(),
                    (int) animal.getPosition().getY()).getType() == TerrainType.WATER;
            if (!inWater) {
                animal.setSpeedBoost(needsFood ? 1.65 : 1.5);
            }

            if (animal.isWithinRange(prey, SimulationConfig.PREDATOR_ATTACK_RADIUS)) {
                prey.takeDamage(animal.getAttackDamage());
                if (prey.isAlive()) {
                    prey.takeDamage(prey.getHealth());
                }
                if (!prey.isAlive()) {
                    animal.eat();
                    animal.setActionState("Đang ăn");
                } else {
                    animal.setActionState("Tấn công");
                }
            } else {
                animal.setActionState(needsFood ? "Đuổi mồi" : "Săn mồi");
            }
        } else {
            animal.setSpeedBoost(1.0);
            animal.setActionState(needsFood ? "Tìm mồi" : "Tìm mồi");
            if (shouldExpandSearch(animal)) {
                animal.setExplorationRange(animal.getExplorationRange() + 2);
            }
            wander(animal);
        }
    }

    private void wander(Animal animal) {
        animal.pickRandomWanderDirection();
    }

    private boolean shouldExpandSearch(Animal animal) {
        return animal.getHunger() > 50 && animal.getExplorationRange() < 15;
    }

    @Override
    public String getName() {
        return "Hunter";
    }
}
