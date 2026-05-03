package ecosystem.view;

import ecosystem.entities.Animal;
import ecosystem.entities.Plant;
import ecosystem.environment.Environment;
import ecosystem.terrain.Tile;
import ecosystem.terrain.TerrainType;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GraphicalView extends BasicView {
    private BufferedImage spriteSheet;
    private Map<String, Rectangle> spriteCoords;
    private boolean useSprites = true;

    public GraphicalView(Environment environment) {
        super(environment);
        setTitle("Wild-Life Eco Simulation - Graphical Mode");
        loadSprites();
        
        JButton toggleBtn = new JButton("Chuyển chế độ View");
        toggleBtn.addActionListener(e -> {
            useSprites = !useSprites;
            setTitle("Wild-Life Eco Simulation - " + (useSprites ? "Graphical Mode" : "Basic Mode"));
            repaint();
        });
        
        Component[] components = getContentPane().getComponents();
        for (Component c : components) {
            if (c instanceof JPanel) {
                ((JPanel) c).add(toggleBtn);
                break;
            }
        }
    }

    private void loadSprites() {
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
        } catch (IOException e) {
            useSprites = false;
        }
    }

    @Override
    protected void renderMap(Graphics g) {
        if (!useSprites || spriteSheet == null) {
            super.renderMap(g);
            return;
        }

        int currentCellSize = (int) (cellSize * zoomLevel);
        Tile[][] tiles = environment.getGrid().getAllTiles();

        int startX = Math.max(0, -offsetX / currentCellSize);
        int startY = Math.max(0, -offsetY / currentCellSize);
        int endX = Math.min(tiles.length, (mapPanel.getWidth() - offsetX) / currentCellSize + 1);
        int endY = Math.min(tiles[0].length, (mapPanel.getHeight() - offsetY) / currentCellSize + 1);

        for (int i = startX; i < endX; i++) {
            for (int j = startY; j < endY; j++) {
                Tile tile = tiles[i][j];
                Rectangle r = spriteCoords.get(tile.getType().getName());
                if (r != null) {
                    g.drawImage(spriteSheet, 
                        i * currentCellSize + offsetX, j * currentCellSize + offsetY, 
                        (i + 1) * currentCellSize + offsetX, (j + 1) * currentCellSize + offsetY,
                        r.x, r.y, r.x + r.width, r.y + r.height, null);
                }
            }
        }

        for (Plant plant : environment.getPlants()) {
            int i = (int) plant.getPosition().getX();
            int j = (int) plant.getPosition().getY();
            if (i < startX || i >= endX || j < startY || j >= endY) continue;

            Rectangle r = spriteCoords.get(plant.getType());
            int x = i * currentCellSize + offsetX;
            int y = j * currentCellSize + offsetY;
            if (r != null) {
                g.drawImage(spriteSheet, x, y, x + currentCellSize, y + currentCellSize, 
                    r.x, r.y, r.x + r.width, r.y + r.height, null);
            }
        }

        for (Animal animal : environment.getAnimals()) {
            int i = (int) animal.getPosition().getX();
            int j = (int) animal.getPosition().getY();
            if (i < startX || i >= endX || j < startY || j >= endY) continue;

            Rectangle r = spriteCoords.get(animal.getName());
            if (r != null) {
                int x = i * currentCellSize + offsetX;
                int y = j * currentCellSize + offsetY;
                
                int frameOffset = 0;
                if (animal.getStrategy().getName().equals("Hunter") || animal.getStrategy().getName().equals("Scared")) {
                    frameOffset = 128; 
                }

                if (animal.getVelocity().getX() < 0) {
                    g.drawImage(spriteSheet, x + currentCellSize, y, x, y + currentCellSize, 
                        r.x + frameOffset, r.y, r.x + r.width + frameOffset, r.y + r.height, null);
                } else {
                    g.drawImage(spriteSheet, x, y, x + currentCellSize, y + currentCellSize, 
                        r.x + frameOffset, r.y, r.x + r.width + frameOffset, r.y + r.height, null);
                }
                
                if (zoomLevel > 0.8) {
                    int healthW = (int) (20 * zoomLevel);
                    g.setColor(Color.RED);
                    g.fillRect(x + currentCellSize/2 - healthW/2, y - 5, healthW, 3);
                    g.setColor(Color.GREEN);
                    g.fillRect(x + currentCellSize/2 - healthW/2, y - 5, (int) (healthW * animal.getHealth() / 100.0), 3);
                }
            }
        }
    }
}
