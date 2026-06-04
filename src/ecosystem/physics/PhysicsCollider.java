package ecosystem.physics;

/**
 * Utility for geometric collision checks (Interface Segregation + static helper).
 * Grid-based gameplay still uses tile occupancy; this supports radius overlap.
 */
public final class PhysicsCollider {

    private PhysicsCollider() {
    }

    public static boolean circlesOverlap(ICollidable a, Vector2D posA, ICollidable b, Vector2D posB) {
        if (a == null || b == null || posA == null || posB == null) {
            return false;
        }
        if (!a.isSolid() || !b.isSolid()) {
            return false;
        }
        double minDist = a.getRadius() + b.getRadius();
        return posA.distanceTo(posB) < minDist;
    }

    public static boolean sameGridCell(Vector2D a, Vector2D b) {
        return a != null && b != null
                && (int) a.getX() == (int) b.getX()
                && (int) a.getY() == (int) b.getY();
    }
}
