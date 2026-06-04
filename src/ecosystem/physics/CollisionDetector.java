package ecosystem.physics;

import ecosystem.entities.Animal;
import ecosystem.entities.Human;
import ecosystem.environment.Environment;
import ecosystem.terrain.TerrainType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Encapsulates tile occupancy and terrain collision (Single Responsibility).
 * Uses a per-tick spatial index for faster lookups (Week 4 optimization).
 */
public class CollisionDetector {

    private final Map<String, List<Animal>> cellOccupancy = new HashMap<>();

    public void rebuildOccupancyIndex(Environment env) {
        cellOccupancy.clear();
        for (Animal animal : env.getAnimals()) {
            if (animal == null || !animal.isAlive()) {
                continue;
            }
            String key = cellKey(animal.getPosition());
            cellOccupancy.computeIfAbsent(key, k -> new ArrayList<>()).add(animal);
        }
    }

    public boolean canEnterTile(Animal mover, Environment env, Vector2D target) {
        int targetX = (int) target.getX();
        int targetY = (int) target.getY();

        if (!isTerrainPassable(mover, env, targetX, targetY)) {
            return false;
        }

        List<Animal> onTile = cellOccupancy.get(cellKey(target));
        if (onTile == null || onTile.isEmpty()) {
            return true;
        }

        int animalsInTargetTile = 0;
        for (Animal other : onTile) {
            if (other == null || other == mover || !other.isAlive()) {
                continue;
            }

            animalsInTargetTile++;
            if (mover.isPredator() && mover.canEat(other) && animalsInTargetTile <= 1) {
                continue;
            }
            if (other.mustYieldTo(mover)) {
                continue;
            }
            return false;
        }

        return true;
    }

    public boolean isTerrainPassable(Animal mover, Environment env, int targetX, int targetY) {
        var tile = env.getGrid().getTile(targetX, targetY);
        if (tile == null) {
            return false;
        }
        boolean isWater = tile.getType() == TerrainType.WATER;
        boolean isForest = tile.getType() == TerrainType.FOREST;

        if (isForest && (mover.isPredator() || mover instanceof Human)) {
            return false;
        }
        if (isWater) {
            return mover.canSwimOnTerrain();
        }
        return mover.canWalkOnTerrain() && env.isWalkable(targetX, targetY);
    }

    private static String cellKey(Vector2D position) {
        return (int) position.getX() + "," + (int) position.getY();
    }
}
