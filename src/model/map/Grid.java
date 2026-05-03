package model.map;

import java.util.Random;

public class Grid {
    private Tile[][] grid;
    private int width, height;

    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
        grid = new Tile[width][height];
        generateMap();
    }

    private void generateMap() {
        Random rand = new Random();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int r = rand.nextInt(3);

                if (r == 0)
                    grid[i][j] = new GrassTile(i, j);
                else if (r == 1)
                    grid[i][j] = new WaterTile(i, j);
                else
                    grid[i][j] = new ObstacleTile(i, j);
            }
        }
    }

    public Tile getTile(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height)
            return null;
        return grid[x][y];
    }

    public Tile[][] getAllTiles() {
        return grid;
    }
}