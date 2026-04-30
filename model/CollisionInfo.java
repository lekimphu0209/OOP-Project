package model;

public class CollisionInfo {
    private final boolean collided;
    private final Vector2D correctedPosition;

    public CollisionInfo(boolean collided, Vector2D correctedPosition) {
        this.collided = collided;
        this.correctedPosition = correctedPosition;
    }

    public boolean isCollided() {
        return collided;
    }

    public Vector2D getCorrectedPosition() {
        return correctedPosition;
    }
}