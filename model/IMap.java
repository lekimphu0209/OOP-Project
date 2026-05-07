package model;

public interface IMap {
    boolean isWalkable(double x, double y);
    boolean isObstacle(double x, double y);
    double getTerrainSpeedMultiplier(double x, double y);
}