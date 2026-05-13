package controller;

import model.map.MapManager;
import model.map.Tile;
import view.MapView;

public class MapController {

    private MapManager mapManager;
    private MapView view;

    public MapController() {
        mapManager = MapManager.getInstance();
        view = new MapView();
    }

    public void showMap() {
        view.render(mapManager.getGrid());
    }

    public void inspectTile(int x, int y) {
        Tile t = mapManager.getTileAt(x, y);

        if (t == null) {
            System.out.println("Out of bounds!");
        } else {
            System.out.println("Tile (" + x + "," + y + "): " + t.getType());
        }
    }
}