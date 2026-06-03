package ecosystem.view.render.legacy;

import ecosystem.entities.Animal;
import ecosystem.entities.Plant;
import ecosystem.environment.Environment;
import ecosystem.physics.Vector2D;
import ecosystem.terrain.Tile;
import ecosystem.view.render.IRenderStrategy;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AdvancedRenderer implements IRenderStrategy {
    private BufferedImage tileset16x16;
    private Map<String, BufferedImage> animalImages;
    private Map<String, BufferedImage> plantImages;
    private Map<String, Rectangle> tileCoords;
    private boolean isTilesetLoaded = false;
    private boolean isAnimalImagesLoaded = false;
    private boolean isPlantImagesLoaded = false;
    private BasicRenderer fallbackRenderer;

    public AdvancedRenderer() {
        System.out.println("AdvancedRenderer constructor called!");
        fallbackRenderer = new BasicRenderer();

        // Load tileset_16x16 for terrain
        try {
            File tilesetFile = new File(getResourcePath("resources/images"), "tileset_16x16_final_1.png");
            System.out.println("Loading tileset from: " + tilesetFile.getAbsolutePath());
            tileset16x16 = ImageIO.read(tilesetFile);
            tileCoords = new HashMap<>();
            int s = 16;
            int cols = 17; // Columns in tileset_16x16_final_1.png

            // Map GID to tile positions (based on TmxMapLoader mapping)
            // 94 -> GRASS, 274 -> WATER, 36 -> FOREST, 172 -> MUD, 164 -> OBSTACLE
            // Formula: x = (gid - 1) % cols * s, y = (gid - 1) / cols * s

            tileCoords.put("Cỏ", getTileRect(94, cols, s));
            tileCoords.put("Rừng", getTileRect(36, cols, s));
            tileCoords.put("Nước", getTileRect(274, cols, s));
            tileCoords.put("Bùn", getTileRect(172, cols, s));
            tileCoords.put("Vật cản", getTileRect(15, cols, s));

            isTilesetLoaded = true;
        } catch (IOException e) {
            System.out.println("Failed to load tileset: " + e.getMessage());
            isTilesetLoaded = false;
        }

        // Load animal images from activities folder
        try {
            animalImages = new HashMap<>();
            System.out.println("Loading animal animation images...");

            String currentDir = new File(".").getAbsolutePath();
            System.out.println("Current working directory: " + currentDir);

            // Load rabbit animations
            loadAnimalAnimations("Thỏ", "rabbit");
            loadAnimalAnimations("Hươu", "deer");
            loadAnimalAnimations("Sói", "wolf");
            loadAnimalAnimations("Hổ", "tiger");
            loadAnimalAnimations("Voi", "elephant");
            loadAnimalAnimations("Người", "human");
            loadAnimalAnimations("Cá", "fish");
            loadAnimalAnimations("Vịt", "duck");
            loadAnimalAnimations("Cá sấu", "crocodile");

            System.out.println("Loaded " + animalImages.size() + " animal animation images successfully!");
            isAnimalImagesLoaded = true;
        } catch (IOException e) {
            System.out.println("Failed to load animal images: " + e.getMessage());
            e.printStackTrace();
            isAnimalImagesLoaded = false;
        }

        // Load plant images from plant folder
        try {
            plantImages = new HashMap<>();
            System.out.println("Loading plant images...");

            loadPlantImage("Cỏ", "grass");
            loadPlantImage("Cây ăn quả", "tree");

            System.out.println("Loaded " + plantImages.size() + " plant images successfully!");
            isPlantImagesLoaded = true;
        } catch (IOException e) {
            System.out.println("Failed to load plant images: " + e.getMessage());
            e.printStackTrace();
            isPlantImagesLoaded = false;
        }
    }

    private void loadAnimalAnimations(String animalName, String folderName) throws IOException {
        String[] actions = { "idle", "eat", "run" };
        File resourceBase = new File(getResourcePath("resources/activities"));
        File animalFolder = new File(resourceBase, folderName);
        BufferedImage idleImage = null;

        // First, load idle image as fallback
        File idleFile = new File(animalFolder, "idle.png");
        if (idleFile.exists()) {
            idleImage = ImageIO.read(idleFile);
            animalImages.put(animalName + "_idle", idleImage);
            System.out.println("Loaded: " + animalName + "_idle from " + idleFile.getAbsolutePath());
        }

        // Load other actions, use idle as fallback if missing
        for (String action : actions) {
            if (action.equals("idle"))
                continue; // Already loaded

            String key = animalName + "_" + action;
            File imageFile = new File(animalFolder, action + ".png");

            if (imageFile.exists()) {
                animalImages.put(key, ImageIO.read(imageFile));
                System.out.println("Loaded: " + key + " from " + imageFile.getAbsolutePath());
            } else {
                System.out.println("Not found: " + imageFile.getAbsolutePath() + " - using idle as fallback");
                // Use idle image as fallback
                if (idleImage != null) {
                    animalImages.put(key, idleImage);
                }
            }
        }
    }

    private void loadPlantImage(String plantName, String fileName) throws IOException {
        File resourceBase = new File(getResourcePath("resources/plant"));
        File imageFile = new File(resourceBase, fileName + ".png");

        if (imageFile.exists()) {
            plantImages.put(plantName, ImageIO.read(imageFile));
            System.out.println("Loaded plant: " + plantName + " from " + imageFile.getAbsolutePath());
        } else {
            System.out.println("Not found: " + imageFile.getAbsolutePath());
        }
    }

    private void drawAnimalSprite(Graphics2D g2d, BufferedImage img, int x, int y, int size, int facingSign) {
        if (facingSign < 0) {
            java.awt.geom.AffineTransform saved = g2d.getTransform();
            g2d.translate(x + size, y);
            g2d.scale(-1, 1);
            g2d.drawImage(img, 0, 0, size, size, null);
            g2d.setTransform(saved);
        } else {
            g2d.drawImage(img, x, y, size, size, null);
        }
    }

    private String mapActionToImage(String actionState) {
        if (actionState == null || actionState.isEmpty() || actionState.equals("Nghỉ ngơi")) {
            return "idle";
        } else if (actionState.equals("Đang ăn") || actionState.equals("Đang uống")) {
            return "eat";
        } else {
            // "Tìm nước", "Tìm thức ăn", "Điên cuồng tìm thức ăn", etc.
            return "run";
        }
    }

    private String getResourcePath(String relativePath) {
        // Get the project root directory (current working directory)
        String projectRoot = System.getProperty("user.dir");
        File resourceFile = new File(projectRoot, relativePath);
        return resourceFile.getAbsolutePath();
    }

    private Rectangle getTileRect(int gid, int cols, int tileSize) {
        int index = gid - 1;
        int x = (index % cols) * tileSize;
        int y = (index / cols) * tileSize;
        return new Rectangle(x, y, tileSize, tileSize);
    }

    @Override
    public void render(Graphics2D g2d, Environment env, int cellSize, double zoomLevel, int offsetX, int offsetY,
            int panelWidth, int panelHeight) {
        if (!isTilesetLoaded || tileset16x16 == null) {
            fallbackRenderer.render(g2d, env, cellSize, zoomLevel, offsetX, offsetY, panelWidth, panelHeight);
            return;
        }

        int currentCellSize = (int) (cellSize * zoomLevel);
        Tile[][] tiles = env.getGrid().getAllTiles();

        int startX = Math.max(0, -offsetX / currentCellSize);
        int startY = Math.max(0, -offsetY / currentCellSize);
        int endX = Math.min(tiles.length, (panelWidth - offsetX) / currentCellSize + 1);
        int endY = Math.min(tiles[0].length, (panelHeight - offsetY) / currentCellSize + 1);

        // 1. VẼ ĐỊA HÌNH từ tileset_16x16
        for (int i = startX; i < endX; i++) {
            for (int j = startY; j < endY; j++) {
                Tile tile = tiles[i][j];
                int drawX = i * currentCellSize + offsetX;
                int drawY = j * currentCellSize + offsetY;

                Rectangle r = tileCoords.get(tile.getType().getName());
                if (r != null) {
                    g2d.drawImage(tileset16x16, drawX, drawY, drawX + currentCellSize, drawY + currentCellSize,
                            r.x, r.y, r.x + r.width, r.y + r.height, null);
                }
            }
        }

        // 2. VẼ THỰC VẬT bằng hình ảnh từ folder plant
        for (Plant plant : env.getPlants()) {
            if (!plant.isAlive())
                continue; // Chỉ render plant khi còn sống
            int i = (int) plant.getPosition().getX();
            int j = (int) plant.getPosition().getY();
            if (i < startX || i >= endX || j < startY || j >= endY)
                continue;

            int drawX = i * currentCellSize + offsetX;
            int drawY = j * currentCellSize + offsetY;

            if (isPlantImagesLoaded && plantImages != null) {
                BufferedImage plantImg = plantImages.get(plant.getType());
                if (plantImg != null) {
                    int pad = (int) (currentCellSize * 0.1);
                    int size = currentCellSize - pad * 2;
                    int x = drawX + pad;
                    int y = drawY + pad;

                    g2d.drawImage(plantImg, x, y, size, size, null);
                } else {
                    // Fallback to shapes
                    int x = drawX + currentCellSize / 2;
                    int y = drawY + currentCellSize / 2;
                    int size = (int) ((plant.getType().equals("Cỏ") ? 10 : 16) * zoomLevel);

                    if (plant.getType().equals("Cỏ"))
                        g2d.setColor(new Color(50, 205, 50));
                    else
                        g2d.setColor(new Color(255, 69, 0));
                    g2d.fillOval(x - size / 2, y - size / 2, size, size);
                }
            } else {
                // Fallback to shapes
                int x = drawX + currentCellSize / 2;
                int y = drawY + currentCellSize / 2;
                int size = (int) ((plant.getType().equals("Cỏ") ? 10 : 16) * zoomLevel);

                if (plant.getType().equals("Cỏ"))
                    g2d.setColor(new Color(50, 205, 50));
                else
                    g2d.setColor(new Color(255, 69, 0));
                g2d.fillOval(x - size / 2, y - size / 2, size, size);
            }
        }

        // 3. VẼ ĐỘNG VẬT bằng sprites từ spritesheet.png
        for (Animal animal : env.getAnimals()) {
            if (animal == null)
                continue;
            Vector2D renderPos = animal.getRenderPosition();
            double px = renderPos.getX();
            double py = renderPos.getY();
            if (px < startX - 1 || px >= endX || py < startY - 1 || py >= endY)
                continue;

            int drawX = (int) (px * currentCellSize + offsetX);
            int drawY = (int) (py * currentCellSize + offsetY);

            if (isAnimalImagesLoaded && animalImages != null) {
                String imageKey = animal.getName() + "_" + mapActionToImage(animal.getActionState());
                BufferedImage animalImg = animalImages.get(imageKey);
                if (animalImg != null) {
                    try {
                        int pad = (int) (currentCellSize * 0.15);
                        int size = currentCellSize - pad * 2;
                        int x = drawX + pad;
                        int y = drawY + pad;

                        drawAnimalSprite(g2d, animalImg, x, y, size, animal.getFacingSign());

                        // Health bar
                        if (zoomLevel > 0.8) {
                            int healthW = (int) (20 * zoomLevel);
                            g2d.setColor(Color.RED);
                            g2d.fillRect(drawX + currentCellSize / 2 - healthW / 2, drawY + 5, healthW, 3);
                            g2d.setColor(Color.GREEN);
                            g2d.fillRect(drawX + currentCellSize / 2 - healthW / 2, drawY + 5,
                                    (int) (healthW * animal.getHealth() / 100.0), 3);
                        }
                    } catch (Exception e) {
                        System.out.println("Error drawing animal image: " + e.getMessage() + ", using fallback");
                        // Fallback to basic shapes
                        int x = drawX + currentCellSize / 2;
                        int y = drawY + currentCellSize / 2;
                        int size = (int) (16 * zoomLevel);

                        g2d.setColor(getAnimalColor(animal));
                        if (animal.isPredator())
                            g2d.fillRect(x - size / 2, y - size / 2, size, size);
                        else
                            g2d.fillOval(x - size / 2, y - size / 2, size, size);
                    }
                } else {
                    // Fallback to basic shapes
                    int x = drawX + currentCellSize / 2;
                    int y = drawY + currentCellSize / 2;
                    int size = (int) (16 * zoomLevel);

                    g2d.setColor(getAnimalColor(animal));
                    if (animal.isPredator())
                        g2d.fillRect(x - size / 2, y - size / 2, size, size);
                    else
                        g2d.fillOval(x - size / 2, y - size / 2, size, size);
                }
            } else {
                // Fallback to shapes if animal images not loaded
                int x = drawX + currentCellSize / 2;
                int y = drawY + currentCellSize / 2;
                int size = (int) (16 * zoomLevel);

                g2d.setColor(getAnimalColor(animal));
                if (animal.isPredator())
                    g2d.fillRect(x - size / 2, y - size / 2, size, size);
                else
                    g2d.fillOval(x - size / 2, y - size / 2, size, size);

                if (zoomLevel > 0.8) {
                    int healthW = (int) (20 * zoomLevel);
                    g2d.setColor(Color.RED);
                    g2d.fillRect(x - healthW / 2, y - size / 2 - 5, healthW, 3);
                    g2d.setColor(Color.GREEN);
                    g2d.fillRect(x - healthW / 2, y - size / 2 - 5, (int) (healthW * animal.getHealth() / 100.0), 3);
                }
            }
        }
    }

    private Color getAnimalColor(Animal animal) {
        switch (animal.getName()) {
            case "Thỏ":
                return Color.WHITE;
            case "Hươu":
                return new Color(139, 69, 19);
            case "Sói":
                return Color.GRAY;
            case "Hổ":
                return Color.ORANGE;
            case "Voi":
                return new Color(169, 169, 169);
            case "Người":
                return Color.BLUE;
            case "Cá":
                return new Color(0, 255, 255);
            case "Vịt":
                return Color.YELLOW;
            case "Cá sấu":
                return new Color(0, 100, 0);
            default:
                return Color.BLACK;
        }
    }
}
