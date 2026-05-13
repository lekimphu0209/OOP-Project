package ecosystem.view.render;

import ecosystem.entities.Animal;
import ecosystem.entities.Plant;
import ecosystem.environment.Environment;
import ecosystem.terrain.Tile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AdvancedRenderer implements IRenderStrategy {
    private BufferedImage spriteSheet;
    private Map<String, Rectangle> spriteCoords;
    private boolean isLoaded = false;
    private BasicRenderer fallbackRenderer;

    public AdvancedRenderer() {
        fallbackRenderer = new BasicRenderer();
        try {
            spriteSheet = ImageIO.read(new File("resources/spritesheet.png"));
            spriteCoords = new HashMap<>();
            int s = 128;
            spriteCoords.put("Cỏ", new Rectangle(0, 0, s, s));
            spriteCoords.put("Rừng", new Rectangle(s, 0, s, s));
            spriteCoords.put("Nước", new Rectangle(2*s, 0, s, s));
            spriteCoords.put("Bùn", new Rectangle(3*s, 0, s, s));
            spriteCoords.put("Vật cản", new Rectangle(4*s, 0, s, s));
            
            spriteCoords.put("Thỏ", new Rectangle(0, s, s, s));
            spriteCoords.put("Hươu", new Rectangle(s, s, s, s));
            spriteCoords.put("Sói", new Rectangle(2*s, s, s, s));
            spriteCoords.put("Hổ", new Rectangle(3*s, s, s, s));
            spriteCoords.put("Voi", new Rectangle(4*s, s, s, s));
            spriteCoords.put("Người", new Rectangle(5*s, s, s, s));
            spriteCoords.put("Cá", new Rectangle(6*s, s, s, s));
            spriteCoords.put("Vịt", new Rectangle(7*s, s, s, s));
            spriteCoords.put("Cá sấu", new Rectangle(0, 3*s, s, s));
            spriteCoords.put("Cây ăn quả", new Rectangle(0, 2*s, s, s));
            spriteCoords.put("Thức ăn", new Rectangle(0, 2*s, s, s)); // Thêm ánh xạ cho thức ăn
            isLoaded = true;
        } catch (IOException e) {
            isLoaded = false;
        }
    }

    @Override
    public void render(Graphics2D g2d, Environment env, int cellSize, double zoomLevel, int offsetX, int offsetY, int panelWidth, int panelHeight) {
        if (!isLoaded || spriteSheet == null) {
            fallbackRenderer.render(g2d, env, cellSize, zoomLevel, offsetX, offsetY, panelWidth, panelHeight);
            return;
        }

        int currentCellSize = (int) (cellSize * zoomLevel);
        Tile[][] tiles = env.getGrid().getAllTiles();

        int startX = Math.max(0, -offsetX / currentCellSize);
        int startY = Math.max(0, -offsetY / currentCellSize);
        int endX = Math.min(tiles.length, (panelWidth - offsetX) / currentCellSize + 1);
        int endY = Math.min(tiles[0].length, (panelHeight - offsetY) / currentCellSize + 1);

        for (int i = startX; i < endX; i++) {
            for (int j = startY; j < endY; j++) {
                Tile tile = tiles[i][j];
                Rectangle r = spriteCoords.get(tile.getType().getName());
                if (r != null) {
                    int drawX = i * currentCellSize + offsetX;
                    int drawY = j * currentCellSize + offsetY;
                    g2d.drawImage(spriteSheet, drawX, drawY, drawX + currentCellSize, drawY + currentCellSize,
                        r.x, r.y, r.x + r.width, r.y + r.height, null);
                }
            }
        }

        for (Plant plant : env.getPlants()) {
            Rectangle r = spriteCoords.get(plant.getType());
            if (r != null) {
                int drawX = (int)plant.getPosition().getX() * currentCellSize + offsetX;
                int drawY = (int)plant.getPosition().getY() * currentCellSize + offsetY;
                g2d.drawImage(spriteSheet, drawX, drawY, drawX + currentCellSize, drawY + currentCellSize, 
                    r.x, r.y, r.x + r.width, r.y + r.height, null);
            }
        }

        for (Animal animal : env.getAnimals()) {
            Rectangle r = spriteCoords.get(animal.getName());
            if (r != null) {
                int drawX = (int)animal.getPosition().getX() * currentCellSize + offsetX;
                int drawY = (int)animal.getPosition().getY() * currentCellSize + offsetY;
                
                // LỚP BẢO VỆ CHỐNG CRASH: So sánh an toàn đảo ngược String
                boolean isHunter = animal.getStrategy() != null && "Hunter".equals(animal.getStrategy().getName());
                boolean isScared = animal.getStrategy() != null && "Scared".equals(animal.getStrategy().getName());
                int frameOffset = (isHunter || isScared) ? 128 : 0;

                if (animal.getVelocity().getX() < 0) {
                    g2d.drawImage(spriteSheet, drawX + currentCellSize, drawY, drawX, drawY + currentCellSize, 
                        r.x + frameOffset, r.y, r.x + r.width + frameOffset, r.y + r.height, null);
                } else {
                    g2d.drawImage(spriteSheet, drawX, drawY, drawX + currentCellSize, drawY + currentCellSize, 
                        r.x + frameOffset, r.y, r.x + r.width + frameOffset, r.y + r.height, null);
                }

                // LỚP BẢO VỆ TƯƠNG TỰ
                boolean isHungry = animal.getState() != null && "Hungry".equals(animal.getState().getName());
                if (isHungry) {
                    g2d.setColor(Color.RED);
                    g2d.drawString("!", drawX + currentCellSize - 10, drawY + 15);
                }
            }
        }
    }
}