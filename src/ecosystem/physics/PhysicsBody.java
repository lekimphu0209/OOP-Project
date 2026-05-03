package ecosystem.physics;

public class PhysicsBody {
    private Vector2D position;
    private Vector2D velocity;
    private final double radius;
    private final double mass;

    public PhysicsBody(Vector2D position, double radius, double mass) {
        this.position = position;
        this.radius = radius;
        this.mass = mass;
        this.velocity = new Vector2D(0, 0);
    }

    public Vector2D getPosition() {
        return position;
    }

    public void setPosition(Vector2D position) {
        this.position = position;
    }

    public Vector2D getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2D velocity) {
        this.velocity = velocity;
    }

    public double getRadius() {
        return radius;
    }

    public double getMass() {
        return mass;
    }
}
