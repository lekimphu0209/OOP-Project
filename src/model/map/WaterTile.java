package model.map;

public class WaterTile extends Tile {

    public WaterTile(int x, int y) {
        super(x, y);
    }

    @Override
    public boolean isWalkable() {
        return false;
    }

    @Override
    public String getType() {
        return "Water";
    }
}