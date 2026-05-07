package model;

public class DummyMap implements IMap {

    @Override
    public boolean isWalkable(double x, double y) {
        return !isObstacle(x, y);
    }

    @Override
    public boolean isObstacle(double x, double y) {
        return x >= 5 && x <= 15 && y >= 5 && y <= 15;
    }

    @Override
    public double getTerrainSpeedMultiplier(double x, double y) {
        return 1.0;
    }
}