package model.map;

public class ObstacleTile extends Tile {

    public ObstacleTile(int x, int y) {
        super(x, y);
    }

    @Override
    public boolean isWalkable() {
        return false;
    }

    @Override
    public String getType() {
        return "Obstacle";
    }
}