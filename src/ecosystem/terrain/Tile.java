package ecosystem.terrain;

public class Tile {
    private final int x;
    private final int y;
    private TerrainType type;
    private int gid;

    public Tile(int x, int y, TerrainType type) {
        this(x, y, type, 0);
    }

    public Tile(int x, int y, TerrainType type, int gid) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.gid = gid;
    }

    public int getGid() {
        return gid;
    }

    public void setGid(int gid) {
        this.gid = gid;
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
