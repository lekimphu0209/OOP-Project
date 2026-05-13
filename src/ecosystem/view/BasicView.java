package ecosystem.view;

import ecosystem.entities.Plant;
import ecosystem.environment.Environment;
import ecosystem.physics.Vector2D;
import ecosystem.terrain.Tile;
import ecosystem.terrain.TerrainType;
import ecosystem.view.render.IRenderStrategy;
import ecosystem.view.render.BasicRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BasicView extends JFrame implements IObserver {
    protected Environment environment;
    protected JPanel mapPanel;
    protected JLabel infoLabel;
    protected JPanel controlPanel; 
    protected int cellSize = 30;
    protected double zoomLevel = 1.0;
    protected int offsetX = 0;
    protected int offsetY = 0;
    protected Point lastMousePos;
    protected String actionMode = "plant_food";
    protected IRenderStrategy currentRenderer;

    public BasicView(Environment environment) {
        this.environment = environment;
        this.environment.registerObserver(this);
        this.currentRenderer = new BasicRenderer();

        setTitle("Wild-Life Eco Simulation - Basic Mode");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLayout(new BorderLayout());

        mapPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (currentRenderer != null) {
                    // Truyền chính xác getWidth() và getHeight() của mapPanel vào để chống lỗi đen màn hình
                    currentRenderer.render(g2d, environment, cellSize, zoomLevel, offsetX, offsetY, getWidth(), getHeight());
                }
            }
        };

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

        mapPanel.addMouseWheelListener(e -> {
            double oldZoom = zoomLevel;
            if (e.getWheelRotation() < 0) zoomLevel = Math.min(zoomLevel * 1.1, 5.0); 
            else zoomLevel = Math.max(zoomLevel / 1.1, 0.2); 
            
            double zoomFactor = zoomLevel / oldZoom;
            offsetX = (int) (e.getX() - (e.getX() - offsetX) * zoomFactor);
            offsetY = (int) (e.getY() - (e.getY() - offsetY) * zoomFactor);
            mapPanel.repaint();
        });

        infoLabel = new JLabel("Thông tin hệ sinh thái");
        
        controlPanel = new JPanel();
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
            // Can thiệp thẳng vào mảng gốc để đảm bảo vật cản thực sự được đặt
            Tile[][] allTiles = environment.getGrid().getAllTiles();
            allTiles[x][y].setType(TerrainType.OBSTACLE);
            infoLabel.setText("Đã đặt vật cản tại (" + x + ", " + y + ")");
        }
        updateView();
    }

    @Override
    public void updateView() {
        if (environment.getSeason() != null) {
            infoLabel.setText("Mùa: " + environment.getSeason().getName() + " (" + environment.getSeason().getDescription() + ")" +
                            " | Động vật: " + environment.getAnimals().size() + 
                            " | Thực vật: " + environment.getPlants().size());
        }
        mapPanel.repaint();
    }
}