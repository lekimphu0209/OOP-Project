package ecosystem.environment;

import ecosystem.entities.Animal;
import ecosystem.physics.Vector2D;
import ecosystem.terrain.Grid;

/**
 * Helper methods for grid operations and water finding.
 * Extracted from Environment to reduce file size.
 */
public class GridHelper {
    private Grid grid;

    public GridHelper(Grid grid) {
        this.grid = grid;
    }

    public boolean isWalkable(int x, int y) {
        return grid.isWalkable(x, y);
    }

    public double getSpeedModifier(int x, int y) {
        return grid.getSpeedModifier(x, y);
    }

    public boolean hasWaterNearby(Animal animal) {
        Vector2D position = animal.getPosition();
        int searchRange = 2;
        return isWaterInRange(position, searchRange);
    }

    public Vector2D findDirectionToNearestWater(Animal animal, int searchRange) {
        Vector2D nearestWaterPosition = findNearestWaterPosition(animal, searchRange);
        if (nearestWaterPosition == null) return null;
        return nearestWaterPosition.subtract(animal.getPosition()).normalize();
    }

    private boolean isWaterInRange(Vector2D center, int range) {
        int centerX = (int) center.getX();
        int centerY = (int) center.getY();

        for (int dx = -range; dx <= range; dx++) {
            for (int dy = -range; dy <= range; dy++) {
                if (isWaterTile(centerX + dx, centerY + dy)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isWaterTile(int x, int y) {
        if (grid.getTile(x, y) == null) return false;
        return grid.getTile(x, y).getType().getName().equals("Nước");
    }

    private Vector2D findNearestWaterPosition(Animal animal, int searchRange) {
        Vector2D animalPosition = animal.getPosition();
        Vector2D nearest = null;
        double minDist = Double.MAX_VALUE;

        for (int dx = -searchRange; dx <= searchRange; dx++) {
            for (int dy = -searchRange; dy <= searchRange; dy++) {
                int x = (int) animalPosition.getX() + dx;
                int y = (int) animalPosition.getY() + dy;
                
                if (!isWaterTile(x, y)) continue;

                Vector2D waterPosition = new Vector2D(x, y);
                double distance = animalPosition.distanceTo(waterPosition);
                if (distance < minDist) {
                    minDist = distance;
                    nearest = waterPosition;
                }
            }
        }

        return nearest;
    }
}
