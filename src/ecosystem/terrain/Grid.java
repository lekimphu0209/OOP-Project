package ecosystem.terrain;

import java.util.Random;

public class Grid {
    private Tile[][] grid;
    private final int width;
    private final int height;

    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new Tile[width][height];
        generateMap();
    }

    public Grid(int width, int height, boolean generate) {
        this.width = width;
        this.height = height;
        this.grid = new Tile[width][height];
        if (generate) {
            generateMap();
        } else {
            // Default fill to avoid null tiles before setTile is called
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    grid[i][j] = new Tile(i, j, TerrainType.GRASS);
                }
            }
        }
    }

    private void generateMap() {
        Random rand = new Random();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int r = rand.nextInt(100);

                if (r < 50) {
                    grid[i][j] = new Tile(i, j, TerrainType.GRASS);
                } else if (r < 75) {
                    grid[i][j] = new Tile(i, j, TerrainType.FOREST);
                } else if (r < 90) {
                    grid[i][j] = new Tile(i, j, TerrainType.WATER);
                } else {
                    grid[i][j] = new Tile(i, j, TerrainType.OBSTACLE);
                }
            }
        }
    }

    public Tile getTile(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height) {
            return null;
        }
        return grid[x][y];
    }

    public Tile[][] getAllTiles() {
        return grid;
    }

    public void setTile(int x, int y, Tile tile) {
        if (x < 0 || y < 0 || x >= width || y >= height) return;
        grid[x][y] = tile;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isWalkable(int x, int y) {
        Tile tile = getTile(x, y);
        return tile != null && tile.isWalkable();
    }

    public double getSpeedModifier(int x, int y) {
        Tile tile = getTile(x, y);
        return tile != null ? tile.getSpeedModifier() : 1.0;
    }
}
