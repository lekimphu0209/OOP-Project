// src/ecosystem/behavior/AggressiveStrategy.java

package ecosystem.behavior;

import ecosystem.entities.Animal;
import ecosystem.entities.Plant;
import ecosystem.environment.Environment;
import ecosystem.physics.Vector2D;

import java.util.List;

/**
 * AggressiveStrategy
 *
 * HÀNH VI:
 * - Hung hăng hơn HunterStrategy
 * - Ưu tiên săn mồi khi đói nặng
 * - Tăng tốc mạnh
 * - Bỏ qua thirst trong rage mode
 * - Không sợ nguy hiểm
 *
 * Ví dụ:
 * - Rabbit quá đói -> lao tới carrot/cỏ gần nhất
 * - Wolf quá đói -> frenzy hunt, tăng xác suất bắt được con mồi
 */
public class AggressiveStrategy implements SurvivalStrategy {

    private final double visionRange = 8.0;

    @Override
    public void execute(Animal animal, Environment env) {

        animal.setActionState("Aggressive");

        // Rage mode ignores thirst
        animal.setSpeedBoost(2.0);

        // ===== HERBIVORE =====
        if (!animal.isPredator()) {

            executeHerbivoreAggressive(animal, env);

        } else {

            executePredatorAggressive(animal, env);
        }
    }

    /**
     * Rabbit / Deer aggressive food rush
     */
    private void executeHerbivoreAggressive(
            Animal animal,
            Environment env
    ) {

        Plant food =
                findNearestPlant(animal, env);

        // No food found
        if (food == null) {

            animal.setActionState("Điên cuồng tìm thức ăn");

            new PassiveStrategy()
                    .execute(animal, env);

            return;
        }

        // Move directly toward food
        Vector2D direction =
                food.getPosition()
                        .subtract(animal.getPosition())
                        .normalize();

        animal.setDirection(direction);

        animal.setSpeedBoost(2.2);

        animal.setActionState("Lao tới thức ăn");

        double distance =
                animal.getPosition()
                        .distanceTo(food.getPosition());

        // Eat faster than normal
        if (distance < 1.0) {
            animal.eat();
            animal.setActionState("Ăn điên cuồng");
        }
    }

    /**
     * Wolf / Tiger aggressive hunting
     */
    private void executePredatorAggressive(
            Animal animal,
            Environment env
    ) {

        Animal prey =
                findNearestPrey(
                        animal,
                        env.getAnimals()
                );

        // No prey
        if (prey == null) {

            animal.setActionState("Cuồng nộ tìm con mồi");

            new PassiveStrategy()
                    .execute(animal, env);

            return;
        }

        // Check if prey population is too low - don't hunt to prevent extinction
        int preyCount = 0;
        for (Animal a : env.getAnimals()) {
            if (a != null && a.isAlive() && a.getClass().equals(prey.getClass())) {
                preyCount++;
            }
        }
        // Don't hunt if less than 3 of this prey species
        if (preyCount < 3) {
            animal.setSpeedBoost(1.0);
            animal.setActionState("Bảo tồn");
            new PassiveStrategy().execute(animal, env);
            return;
        }

        // Chase prey directly
        Vector2D direction =
                prey.getPosition()
                        .subtract(animal.getPosition())
                        .normalize();

        animal.setDirection(direction);

        // Strong speed boost
        animal.setSpeedBoost(2.5);

        animal.setActionState("Frenzy Hunt");

        double distance =
                animal.getPosition()
                        .distanceTo(prey.getPosition());

        // Increased catch probability
        if (distance < 1.2) {

            double catchChance = 0.9;

            // Normally HunterStrategy maybe 60-70%
            // AggressiveStrategy = 90%

            if (Math.random() < catchChance) {

                prey.takeDamage(
                        animal.getAttackDamage() * 2
                );

                animal.eat();

                animal.setActionState("Xé xác con mồi");

            } else {

                animal.setActionState("Con mồi trốn thoát");
            }
        }
    }

    /**
     * Find nearest edible plant
     */
    private Plant findNearestPlant(
            Animal animal,
            Environment env
    ) {

        Plant nearest = null;

        double minDistance = Double.MAX_VALUE;

        for (Plant plant : env.getPlants()) {

            if (!plant.isEdible()) continue;

            double distance =
                    animal.getPosition()
                            .distanceTo(
                                    plant.getPosition()
                            );

            if (distance < minDistance &&
                    distance <= visionRange) {

                minDistance = distance;

                nearest = plant;
            }
        }

        return nearest;
    }

    /**
     * Find nearest prey
     */
    private Animal findNearestPrey(
            Animal hunter,
            List<Animal> animals
    ) {

        Animal nearest = null;

        double minDistance = Double.MAX_VALUE;

        for (Animal other : animals) {
            if (other == null) continue;

            if (other == hunter) continue;

            if (!other.isAlive()) continue;

            if (other.isPredator()) continue;

            double distance =
                    hunter.getPosition()
                            .distanceTo(
                                    other.getPosition()
                            );

            if (distance < minDistance &&
                    distance <= visionRange) {

                minDistance = distance;

                nearest = other;
            }
        }

        return nearest;
    }

    @Override
    public String getName() {

        return "Aggressive";
    }
}