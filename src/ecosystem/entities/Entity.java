package ecosystem.entities;

import ecosystem.physics.Vector2D;

public abstract class Entity {
    protected Vector2D position;
    protected double radius;

    public Entity(Vector2D position, double radius) {
        this.position = position;
        this.radius = radius;
    }

    public Vector2D getPosition() {
        return position;
    }

    public void setPosition(Vector2D position) {
        this.position = position;
    }

    public double getRadius() {
        return radius;
    }

    public abstract void update();
}
