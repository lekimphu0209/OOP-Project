package ecosystem.terrain;

public enum TerrainType {
    GRASS("Cỏ", true, 1.0),
    FOREST("Rừng", true, 0.7),
    WATER("Nước", false, 1.0),
    MUD("Bùn", true, 0.4),
    OBSTACLE("Vật cản", false, 0.0);

    private final String name;
    private final boolean walkable;
    private final double speedModifier;

    TerrainType(String name, boolean walkable, double speedModifier) {
        this.name = name;
        this.walkable = walkable;
        this.speedModifier = speedModifier;
    }

    public String getName() {
        return name;
    }

    public boolean isWalkable() {
        return walkable;
    }

    public double getSpeedModifier() {
        return speedModifier;
    }
}
