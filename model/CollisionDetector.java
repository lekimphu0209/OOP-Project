package model;

public class CollisionDetector {

    public boolean isColliding(ICollidable a, ICollidable b) {
        double distance = a.getPosition().distanceTo(b.getPosition());
        return distance < (a.getRadius() + b.getRadius());
    }
}