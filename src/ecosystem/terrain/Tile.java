package ecosystem.terrain;

public class Tile {
    private final int x;
    private final int y;
    private TerrainType type;

    public Tile(int x, int y, TerrainType type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public TerrainType getType() {
        return type;
    }

    public void setType(TerrainType type) {
        this.type = type;
    }

    public boolean isWalkable() {
        return type.isWalkable();
    }

    public double getSpeedModifier() {
        return type.getSpeedModifier();
    }
}
