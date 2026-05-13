package ecosystem.view;

import ecosystem.environment.Environment;
import ecosystem.view.render.IRenderStrategy;
import ecosystem.view.render.BasicRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class BasicView extends JFrame implements IObserver {
    protected Environment environment;
    protected JPanel mapPanel;
    protected JLabel infoLabel;
    protected JPanel controlPanel;
    
    protected int cellSize = 30;
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
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        controlPanel = new JPanel();
        JButton btnPlant = new JButton("Trồng cây");
        JButton btnObstacle = new JButton("Đặt vật cản");

        btnPlant.addActionListener(e -> actionMode = "plant_food");
        btnObstacle.addActionListener(e -> actionMode = "place_obstacle");

        controlPanel.add(btnPlant);
        controlPanel.add(btnObstacle);
        add(controlPanel, BorderLayout.NORTH);

        mapPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (currentRenderer != null) {
                    currentRenderer.render(g2d, environment, cellSize, offsetX, offsetY);
                }
            }
        };
        mapPanel.setBackground(new Color(240, 240, 240));

        infoLabel = new JLabel("Khởi tạo hệ thống...");
        infoLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        setupInteractions();

        add(mapPanel, BorderLayout.CENTER);
        add(infoLabel, BorderLayout.SOUTH);
        
        setLocationRelativeTo(null);
    }

    private void setupInteractions() {
        mapPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastMousePos = e.getPoint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                int gridX = (e.getX() - offsetX) / cellSize;
                int gridY = (e.getY() - offsetY) / cellSize;
                handleMapClick(gridX, gridY);
            }
        });

        mapPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                offsetX += e.getX() - lastMousePos.x;
                offsetY += e.getY() - lastMousePos.y;
                lastMousePos = e.getPoint();
                repaint();
            }
        });

        mapPanel.addMouseWheelListener((MouseWheelEvent e) -> {
            if (e.getWheelRotation() < 0) cellSize = Math.min(100, cellSize + 2);
            else cellSize = Math.max(10, cellSize - 2);
            repaint();
        });
    }

    protected void handleMapClick(int gridX, int gridY) {
        if (gridX < 0 || gridY < 0 || 
            gridX >= environment.getGrid().getWidth() || 
            gridY >= environment.getGrid().getHeight()) {
            return;
        }

        if ("plant_food".equals(actionMode)) {
            System.out.println("Đã trồng cây tại: " + gridX + ", " + gridY);
            // Mở comment dòng dưới nếu nhóm có hàm addPlant:
            // environment.addPlant(new ecosystem.entities.Plant(new ecosystem.physics.Vector2D(gridX, gridY), "Cây ăn quả", true, 10.0));
        } else if ("place_obstacle".equals(actionMode)) {
            System.out.println("Đã đặt vật cản tại: " + gridX + ", " + gridY);
            // Mở comment dòng dưới nếu nhóm có hàm setType:
            // environment.getGrid().getAllTiles()[gridX][gridY].setType(ecosystem.terrain.TerrainType.OBSTACLE);
        }
        updateView(); 
    }

    @Override
    public void updateView() {
        if (infoLabel != null && environment.getSeason() != null) {
            infoLabel.setText("Mùa: " + environment.getSeason().getName() + 
                            " | Động vật: " + environment.getAnimals().size() + 
                            " | Thực vật: " + environment.getPlants().size());
        }
        mapPanel.repaint();
    }
}