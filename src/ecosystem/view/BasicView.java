package ecosystem.view;

import ecosystem.entities.Animal;
import ecosystem.entities.Plant;
import ecosystem.environment.Environment;
import ecosystem.physics.Vector2D;
import ecosystem.terrain.Tile;
import ecosystem.terrain.TerrainType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BasicView extends JFrame {
    protected Environment environment;
    protected JPanel mapPanel;
    protected JLabel infoLabel;
    protected int cellSize = 30;
    protected double zoomLevel = 1.0;
    protected int offsetX = 0;
    protected int offsetY = 0;
    protected Point lastMousePos;
    protected String actionMode = "plant_food"; // plant_food or place_obstacle

    public BasicView(Environment environment) {
        this.environment = environment;
        setTitle("Wild-Life Eco Simulation - Basic Mode");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 900);
        setLayout(new BorderLayout());

        mapPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                renderMap(g);
            }
        };

        // Add mouse listeners for manual control and camera panning
        mapPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    handleMapClick(e);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                lastMousePos = e.getPoint();
            }
        });

        mapPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    offsetX += e.getX() - lastMousePos.x;
                    offsetY += e.getY() - lastMousePos.y;
                    lastMousePos = e.getPoint();
                    mapPanel.repaint();
                }
            }
        });

        // Add mouse wheel listener for zooming
        mapPanel.addMouseWheelListener(e -> {
            double oldZoom = zoomLevel;
            if (e.getWheelRotation() < 0) {
                zoomLevel = Math.min(zoomLevel * 1.1, 5.0); // Zoom in
            } else {
                zoomLevel = Math.max(zoomLevel / 1.1, 0.2); // Zoom out
            }
            
            // Adjust offset to zoom towards mouse position
            double zoomFactor = zoomLevel / oldZoom;
            offsetX = (int) (e.getX() - (e.getX() - offsetX) * zoomFactor);
            offsetY = (int) (e.getY() - (e.getY() - offsetY) * zoomFactor);
            
            mapPanel.repaint();
        });

        infoLabel = new JLabel("Thông tin hệ sinh thái");
        
        // Control panel
        JPanel controlPanel = new JPanel();
        JButton plantFoodBtn = new JButton("Trồng thức ăn");
        JButton placeObstacleBtn = new JButton("Đặt vật cản");
        
        plantFoodBtn.addActionListener(e -> {
            actionMode = "plant_food";
            infoLabel.setText("Chế độ: Trồng thức ăn - Click vào bản đồ để trồng");
        });
        
        placeObstacleBtn.addActionListener(e -> {
            actionMode = "place_obstacle";
            infoLabel.setText("Chế độ: Đặt vật cản - Click vào bản đồ để đặt");
        });
        
        controlPanel.add(plantFoodBtn);
        controlPanel.add(placeObstacleBtn);
        
        add(controlPanel, BorderLayout.NORTH);
        add(mapPanel, BorderLayout.CENTER);
        add(infoLabel, BorderLayout.SOUTH);
    }

    protected void handleMapClick(MouseEvent e) {
        int currentCellSize = (int) (cellSize * zoomLevel);
        int x = (e.getX() - offsetX) / currentCellSize;
        int y = (e.getY() - offsetY) / currentCellSize;
        
        if (x < 0 || y < 0 || x >= environment.getGrid().getWidth() || y >= environment.getGrid().getHeight()) {
            return;
        }

        if (actionMode.equals("plant_food")) {
            Tile tile = environment.getGrid().getTile(x, y);
            if (tile != null && (tile.getType() == TerrainType.GRASS || tile.getType() == TerrainType.FOREST)) {
                String plantType = Math.random() < 0.7 ? "Cỏ" : "Cây ăn quả";
                Plant plant = new Plant(new Vector2D(x, y), plantType, true, plantType.equals("Cỏ") ? 5.0 : 10.0);
                environment.addPlant(plant);
                infoLabel.setText("Đã trồng " + plantType + " tại (" + x + ", " + y + ")");
            }
        } else if (actionMode.equals("place_obstacle")) {
            Tile tile = environment.getGrid().getTile(x, y);
            if (tile != null) {
                tile.setType(TerrainType.OBSTACLE);
                infoLabel.setText("Đã đặt vật cản tại (" + x + ", " + y + ")");
            }
        }
        mapPanel.repaint();
    }

    protected void renderMap(Graphics g) {
        int currentCellSize = (int) (cellSize * zoomLevel);
        Tile[][] tiles = environment.getGrid().getAllTiles();

        int startX = Math.max(0, -offsetX / currentCellSize);
        int startY = Math.max(0, -offsetY / currentCellSize);
        int endX = Math.min(tiles.length, (mapPanel.getWidth() - offsetX) / currentCellSize + 1);
        int endY = Math.min(tiles[0].length, (mapPanel.getHeight() - offsetY) / currentCellSize + 1);

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
                if (zoomLevel > 0.5) {
                    g.setColor(new Color(0, 0, 0, 30));
                    g.drawRect(x, y, currentCellSize, currentCellSize);
                }
            }
        }

        for (Plant plant : environment.getPlants()) {
            int i = (int) plant.getPosition().getX();
            int j = (int) plant.getPosition().getY();
            if (i < startX || i >= endX || j < startY || j >= endY) continue;

            int x = i * currentCellSize + offsetX + currentCellSize / 2;
            int y = j * currentCellSize + offsetY + currentCellSize / 2;
            int size = (int) ((plant.getType().equals("Cỏ") ? 10 : 16) * zoomLevel);

            if (plant.getType().equals("Cỏ")) g.setColor(new Color(50, 205, 50));
            else g.setColor(new Color(255, 165, 0));
            g.fillOval(x - size/2, y - size/2, size, size);
        }

        for (Animal animal : environment.getAnimals()) {
            int i = (int) animal.getPosition().getX();
            int j = (int) animal.getPosition().getY();
            if (i < startX || i >= endX || j < startY || j >= endY) continue;

            int x = i * currentCellSize + offsetX + currentCellSize / 2;
            int y = j * currentCellSize + offsetY + currentCellSize / 2;
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

    public void update() {
        mapPanel.repaint();
        infoLabel.setText("Mùa: " + environment.getSeason().getName() + " (" + environment.getSeason().getDescription() + ")" +
                        " | Động vật: " + environment.getAnimals().size() + 
                        " | Thực vật: " + environment.getPlants().size());
    }
}
