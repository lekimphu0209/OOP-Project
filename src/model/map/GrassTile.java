package model.map;

public class GrassTile extends Tile {

    public GrassTile(int x, int y) {
        super(x, y);
    }

    @Override
    public boolean isWalkable() {
        return true;
    }

    @Override
    public String getType() {
        return "Grass";
    }
}