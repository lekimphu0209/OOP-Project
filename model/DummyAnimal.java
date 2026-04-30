package model;

public class DummyAnimal implements IMovable, ICollidable, IYieldable {
    private Vector2D position;
    private Vector2D velocity;
    private final double speed;
    private final double radius;
    private final int priority;

    public DummyAnimal(Vector2D position, double speed, double radius, int priority) {
        this.position = position;
        this.speed = speed;
        this.radius = radius;
        this.priority = priority;
        this.velocity = new Vector2D(0, 0);
    }

    @Override
    public Vector2D getPosition() {
        return position;
    }

    @Override
    public void setPosition(Vector2D position) {
        this.position = position;
    }

    @Override
    public Vector2D getVelocity() {
        return velocity;
    }

    @Override
    public void setVelocity(Vector2D velocity) {
        this.velocity = velocity;
    }

    @Override
    public double getBaseSpeed() {
        return speed;
    }

    @Override
    public double getRadius() {
        return radius;
    }

    @Override
    public boolean isSolid() {
        return true;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public boolean mustYieldTo(IYieldable other) {
        return this.priority < other.getPriority();
    }
}