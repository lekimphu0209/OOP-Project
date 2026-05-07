package model;

public interface ICollidable {
    Vector2D getPosition();
    double getRadius();
    boolean isSolid();
}