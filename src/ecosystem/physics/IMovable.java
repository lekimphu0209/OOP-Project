package ecosystem.physics;

public interface IMovable {
    Vector2D getPosition();
    void setPosition(Vector2D position);
    Vector2D getVelocity();
    void setVelocity(Vector2D velocity);
    double getBaseSpeed();
    double getRadius();
}
