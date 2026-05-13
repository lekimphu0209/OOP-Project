package model.map;


import model.entity.Entity;
import java.util.ArrayList;
import java.util.List;

public class MapManager {

    private static MapManager instance;

    private Grid grid;
    private List<Entity> entities;

    private MapManager() {
        grid = new Grid(10, 10);
        entities = new ArrayList<>();
    }

    public static MapManager getInstance() {
        if (instance == null) {
            instance = new MapManager();
        }
        return instance;
    }

    public Tile getTileAt(int x, int y) {
        return grid.getTile(x, y);
    }

    public void addEntity(Entity e) {
        entities.add(e);
    }

    public void removeEntity(Entity e) {
        entities.remove(e);
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public Tile[][] getGrid() {
        return grid.getAllTiles();
    }
}