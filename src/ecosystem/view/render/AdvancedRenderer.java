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
            
            isLoaded = true;
        } catch (IOException e) {
            isLoaded = false;
        }
    }

    @Override
    public void render(Graphics2D g2d, Environment env, int cellSize, int offsetX, int offsetY) {
        if (!isLoaded || spriteSheet == null) {
            fallbackRenderer.render(g2d, env, cellSize, offsetX, offsetY);
            return;
        }

        Tile[][] tiles = env.getGrid().getAllTiles();
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                Tile tile = tiles[i][j];
                Rectangle r = spriteCoords.get(tile.getType().getName());
                if (r != null) {
                    int drawX = i * cellSize + offsetX;
                    int drawY = j * cellSize + offsetY;
                    g2d.drawImage(spriteSheet, drawX, drawY, drawX + cellSize, drawY + cellSize,
                        r.x, r.y, r.x + r.width, r.y + r.height, null);
                }
            }
        }

        for (Plant plant : env.getPlants()) {
            Rectangle r = spriteCoords.get(plant.getType());
            if (r != null) {
                int drawX = (int)plant.getPosition().getX() * cellSize + offsetX;
                int drawY = (int)plant.getPosition().getY() * cellSize + offsetY;
                g2d.drawImage(spriteSheet, drawX, drawY, drawX + cellSize, drawY + cellSize, 
                    r.x, r.y, r.x + r.width, r.y + r.height, null);
            }
        }

        for (Animal animal : env.getAnimals()) {
            Rectangle r = spriteCoords.get(animal.getName());
            if (r != null) {
                int drawX = (int)animal.getPosition().getX() * cellSize + offsetX;
                int drawY = (int)animal.getPosition().getY() * cellSize + offsetY;
                
                int frameOffset = 0;
                if (animal.getStrategy().getName().equals("Hunter") || animal.getStrategy().getName().equals("Scared")) {
                    frameOffset = 128; 
                }

                if (animal.getVelocity().getX() < 0) {
                    g2d.drawImage(spriteSheet, drawX + cellSize, drawY, drawX, drawY + cellSize, 
                        r.x + frameOffset, r.y, r.x + r.width + frameOffset, r.y + r.height, null);
                } else {
                    g2d.drawImage(spriteSheet, drawX, drawY, drawX + cellSize, drawY + cellSize, 
                        r.x + frameOffset, r.y, r.x + r.width + frameOffset, r.y + r.height, null);
                }

                if (animal.getState().getName().equals("Hungry")) {
                    g2d.setColor(Color.RED);
                    g2d.drawString("!", drawX + cellSize - 10, drawY + 15);
                }

                int healthW = (int) (cellSize * 0.66);
                g2d.setColor(Color.RED);
                g2d.fillRect(drawX + cellSize/2 - healthW/2, drawY - 5, healthW, 3);
                g2d.setColor(Color.GREEN);
                g2d.fillRect(drawX + cellSize/2 - healthW/2, drawY - 5, (int) (healthW * animal.getHealth() / 100.0), 3);
            }
        }
    }
}