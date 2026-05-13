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
            spriteSheet = ImageIO.read(new File("resources\\spritesheet.png"));
            spriteCoords = new HashMap<>();
            int s = 128;
            
            // Hàng 0: ĐỊA HÌNH
            spriteCoords.put("Cỏ", new Rectangle(0, 0, s, s));
            spriteCoords.put("Rừng", new Rectangle(s, 0, s, s));
            spriteCoords.put("Nước", new Rectangle(2*s, 0, s, s));
            spriteCoords.put("Bùn", new Rectangle(3*s, 0, s, s));
            spriteCoords.put("Vật cản", new Rectangle(4*s, 0, s, s));
            
            // Hàng 1: ĐỘNG VẬT
            spriteCoords.put("Thỏ", new Rectangle(0, s, s, s));
            spriteCoords.put("Hươu", new Rectangle(s, s, s, s));
            spriteCoords.put("Sói", new Rectangle(2*s, s, s, s));
            spriteCoords.put("Hổ", new Rectangle(3*s, s, s, s));
            spriteCoords.put("Voi", new Rectangle(4*s, s, s, s));
            spriteCoords.put("Người", new Rectangle(5*s, s, s, s));
            spriteCoords.put("Cá", new Rectangle(6*s, s, s, s));
            spriteCoords.put("Vịt", new Rectangle(7*s, s, s, s));
            
            // Hàng 2: BÒ SÁT & THỰC VẬT
            spriteCoords.put("Cá sấu", new Rectangle(0, 2*s, s, s));
            spriteCoords.put("Cây ăn quả", new Rectangle(s, 2*s, s, s));
            spriteCoords.put("Thức ăn", new Rectangle(2*s, 2*s, s, s));
            
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

        // 1. VẼ ĐỊA HÌNH VÀ VẬT CẢN
        for (int i = startX; i < endX; i++) {
            for (int j = startY; j < endY; j++) {
                Tile tile = tiles[i][j];
                int drawX = i * currentCellSize + offsetX;
                int drawY = j * currentCellSize + offsetY;

                if (tile.getType().name().equals("OBSTACLE")) {
                    // Vẽ cỏ trước, sau đó đặt đá lên trên
                    Rectangle grass = spriteCoords.get("Cỏ");
                    if (grass != null) {
                        g2d.drawImage(spriteSheet, drawX, drawY, drawX + currentCellSize, drawY + currentCellSize,
                            grass.x, grass.y, grass.x + grass.width, grass.y + grass.height, null);
                    }
                    Rectangle rock = spriteCoords.get("Vật cản");
                    if (rock != null) {
                        int pad = (int)(currentCellSize * 0.15); // Thu nhỏ đá 15%
                        g2d.drawImage(spriteSheet, drawX + pad, drawY + pad, drawX + currentCellSize - pad, drawY + currentCellSize - pad,
                            rock.x, rock.y, rock.x + rock.width, rock.y + rock.height, null);
                    }
                } else {
                    Rectangle r = spriteCoords.get(tile.getType().getName());
                    if (r != null) {
                        g2d.drawImage(spriteSheet, drawX, drawY, drawX + currentCellSize, drawY + currentCellSize,
                            r.x, r.y, r.x + r.width, r.y + r.height, null);
                    }
                }
            }
        }

        // 2. VẼ THỰC VẬT
        for (Plant plant : env.getPlants()) {
            Rectangle r = spriteCoords.get(plant.getType());
            if (r != null) {
                int drawX = (int)plant.getPosition().getX() * currentCellSize + offsetX;
                int drawY = (int)plant.getPosition().getY() * currentCellSize + offsetY;
                
                int pad = (int)(currentCellSize * 0.1); // Cây gần vừa ô
                g2d.drawImage(spriteSheet, drawX + pad, drawY + pad, drawX + currentCellSize - pad, drawY + currentCellSize - pad, 
                    r.x, r.y, r.x + r.width, r.y + r.height, null);
            }
        }

        // 3. VẼ ĐỘNG VẬT
        for (Animal animal : env.getAnimals()) {
            Rectangle r = spriteCoords.get(animal.getName());
            if (r != null) {
                int drawX = (int)animal.getPosition().getX() * currentCellSize + offsetX;
                int drawY = (int)animal.getPosition().getY() * currentCellSize + offsetY;
                
                int pad = (int)(currentCellSize * 0.15); // Thú đứng chính giữa ô cỏ
                boolean flip = animal.getVelocity() != null && animal.getVelocity().getX() > 0;
                
                // Mặc định các thú trong ảnh đang quay mặt sang trái. Đi sang phải thì lật ảnh.
                int dx1 = flip ? (drawX + currentCellSize - pad) : (drawX + pad);
                int dx2 = flip ? (drawX + pad) : (drawX + currentCellSize - pad);
                
                g2d.drawImage(spriteSheet, dx1, drawY + pad, dx2, drawY + currentCellSize - pad, 
                    r.x, r.y, r.x + r.width, r.y + r.height, null);

                // Biểu tượng đói (Dấu chấm than đỏ)
                boolean isHungry = animal.getState() != null && "Hungry".equals(animal.getState().getName());
                if (isHungry) {
                    g2d.setColor(Color.RED);
                    g2d.setFont(new Font("Arial", Font.BOLD, Math.max(12, (int)(16*zoomLevel))));
                    g2d.drawString("!", drawX + currentCellSize - 15, drawY + 20);
                }
            }
        }
    }
}