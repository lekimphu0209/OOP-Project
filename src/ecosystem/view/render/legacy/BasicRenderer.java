package ecosystem.view.render.legacy;

import ecosystem.entities.Animal;
import ecosystem.entities.Plant;
import ecosystem.environment.Environment;
import ecosystem.physics.Vector2D;
import ecosystem.terrain.Tile;
import ecosystem.view.render.IRenderStrategy;

import java.awt.Color;
import java.awt.Graphics2D;

public class BasicRenderer implements IRenderStrategy {

    @Override
    public void render(Graphics2D g, Environment env, int cellSize, double zoomLevel, int offsetX, int offsetY, int panelWidth, int panelHeight) {
        int currentCellSize = (int) (cellSize * zoomLevel);
        Tile[][] tiles = env.getGrid().getAllTiles();

        // Sử dụng panelWidth và panelHeight để tính toán an toàn, không dùng clipBounds nữa
        int startX = Math.max(0, -offsetX / currentCellSize);
        int startY = Math.max(0, -offsetY / currentCellSize);
        int endX = Math.min(tiles.length, (panelWidth - offsetX) / currentCellSize + 1);
        int endY = Math.min(tiles[0].length, (panelHeight - offsetY) / currentCellSize + 1);

        for (int i = startX; i < endX; i++) {
            for (int j = startY; j < endY; j++) {
                Tile tile = tiles[i][j];
                int x = i * currentCellSize + offsetX;
                int y = j * currentCellSize + offsetY;

                switch (tile.getType()) {
                    case GRASS: g.setColor(new Color(34, 139, 34)); break;
                    case FOREST: g.setColor(new Color(0, 100, 0)); break;
                    case WATER: g.setColor(new Color(30, 144, 255)); break;
                    case MUD: g.setColor(new Color(139, 69, 19)); break;
                    case OBSTACLE: g.setColor(new Color(128, 128, 128)); break;
                }
                g.fillRect(x, y, currentCellSize, currentCellSize);
                
                // Nét vẽ vật cản tảng đá
                if (tile.getType().name().equals("OBSTACLE")) {
                    g.setColor(new Color(105, 105, 105)); 
                    int rockSize = (int)(currentCellSize * 0.7);
                    g.fillOval(x + (currentCellSize - rockSize)/2, y + (currentCellSize - rockSize)/2, rockSize, rockSize);
                    g.setColor(Color.DARK_GRAY);
                    g.drawOval(x + (currentCellSize - rockSize)/2, y + (currentCellSize - rockSize)/2, rockSize, rockSize);
                }

                if (zoomLevel > 0.5) {
                    g.setColor(new Color(0, 0, 0, 30));
                    g.drawRect(x, y, currentCellSize, currentCellSize);
                }
            }
        }

        for (Plant plant : env.getPlants()) {
            if (!plant.isAlive()) continue; // Chỉ render plant khi còn sống
            int i = (int) plant.getPosition().getX();
            int j = (int) plant.getPosition().getY();
            if (i < startX || i >= endX || j < startY || j >= endY) continue;

            int x = i * currentCellSize + offsetX + currentCellSize / 2;
            int y = j * currentCellSize + offsetY + currentCellSize / 2;
            int size = (int) ((plant.getType().equals("Cỏ") ? 10 : 16) * zoomLevel);

            if (plant.getType().equals("Cỏ")) g.setColor(new Color(50, 205, 50));
            else g.setColor(new Color(255, 69, 0)); // Chấm thức ăn màu cam đỏ
            g.fillOval(x - size/2, y - size/2, size, size);
        }

        for (Animal animal : env.getAnimals()) {
            if (animal == null) continue;
            Vector2D renderPos = animal.getRenderPosition();
            double px = renderPos.getX();
            double py = renderPos.getY();
            if (px < startX - 1 || px >= endX || py < startY - 1 || py >= endY) continue;

            int x = (int) (px * currentCellSize + offsetX + currentCellSize / 2);
            int y = (int) (py * currentCellSize + offsetY + currentCellSize / 2);
            int size = (int) (16 * zoomLevel);

            g.setColor(getAnimalColor(animal));
            if (animal.isPredator()) g.fillRect(x - size/2, y - size/2, size, size);
            else g.fillOval(x - size/2, y - size/2, size, size);

            if (zoomLevel > 0.8) {
                int healthW = (int) (20 * zoomLevel);
                g.setColor(Color.RED);
                g.fillRect(x - healthW/2, y - size/2 - 5, healthW, 3);
                g.setColor(Color.GREEN);
                g.fillRect(x - healthW/2, y - size/2 - 5, (int) (healthW * animal.getHealth() / 100.0), 3);
            }
        }
    }

    protected Color getAnimalColor(Animal animal) {
        switch (animal.getName()) {
            case "Thỏ": return Color.WHITE;
            case "Hươu": return new Color(139, 69, 19);
            case "Sói": return Color.GRAY;
            case "Hổ": return Color.ORANGE;
            case "Voi": return new Color(169, 169, 169);
            case "Người": return Color.BLUE;
            case "Cá": return new Color(0, 255, 255);
            case "Vịt": return Color.YELLOW;
            case "Cá sấu": return new Color(0, 100, 0);
            default: return Color.BLACK;
        }
    }
}
