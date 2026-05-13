package view;

import model.map.Tile;

public class MapView {

    public void render(Tile[][] grid) {

        for (int j = 0; j < grid[0].length; j++) {
            for (int i = 0; i < grid.length; i++) {

                String type = grid[i][j].getType();

                switch (type) {
                    case "Grass":
                        System.out.print(" . ");
                        break;
                    case "Water":
                        System.out.print(" ~ ");
                        break;
                    case "Obstacle":
                        System.out.print(" X ");
                        break;
                }
            }
            System.out.println();
        }
    }
}