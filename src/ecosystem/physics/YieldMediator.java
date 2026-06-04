package ecosystem.physics;

import ecosystem.entities.Animal;
import ecosystem.environment.Environment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Mediator: coordinates yielding between animals without each species knowing others.
 * Higher {@link IYieldable#getPriority()} acts first; lower priority is displaced.
 */
public class YieldMediator {

    private final CollisionDetector collisionDetector;

    public YieldMediator(CollisionDetector collisionDetector) {
        this.collisionDetector = collisionDetector;
    }

    /**
     * Order animals for interaction (high priority first).
     */
    public List<Animal> sortForInteraction(List<Animal> animals) {
        List<Animal> sorted = new ArrayList<>();
        for (Animal animal : animals) {
            if (animal != null && animal.isAlive()) {
                sorted.add(animal);
            }
        }
        sorted.sort(Comparator.comparingInt(IYieldable::getPriority).reversed());
        return sorted;
    }

    public void onEnterCell(Animal mover, Environment env, Vector2D target) {
        displaceYieldingAnimals(mover, env, target);
    }

    private void displaceYieldingAnimals(Animal mover, Environment env, Vector2D target) {
        int targetX = (int) target.getX();
        int targetY = (int) target.getY();

        for (Animal other : env.getAnimals()) {
            if (other == null || other == mover || !other.isAlive()) {
                continue;
            }
            int otherX = (int) other.getPosition().getX();
            int otherY = (int) other.getPosition().getY();
            if (otherX != targetX || otherY != targetY) {
                continue;
            }
            if (mover.isPredator() && mover.canEat(other)) {
                continue;
            }
            if (other.mustYieldTo(mover)) {
                displaceToAdjacentTile(other, env);
            }
        }
    }

    public void displaceToAdjacentTile(Animal animal, Environment env) {
        int ox = (int) animal.getPosition().getX();
        int oy = (int) animal.getPosition().getY();
        int[][] offsets = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1},
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };
        Random rnd = animal.getPathRandom();
        int start = rnd.nextInt(offsets.length);
        for (int i = 0; i < offsets.length; i++) {
            int[] d = offsets[(start + i) % offsets.length];
            Vector2D candidate = new Vector2D(ox + d[0], oy + d[1]);
            if (collisionDetector.canEnterTile(animal, env, candidate)) {
                animal.snapToCell(candidate);
                animal.setActionState("Nhường đường");
                collisionDetector.rebuildOccupancyIndex(env);
                return;
            }
        }
    }
}
