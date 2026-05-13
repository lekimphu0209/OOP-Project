package ecosystem.physics;

public interface ICollidable {
    Vector2D getPosition();
    double getRadius();
    boolean isSolid();
}
