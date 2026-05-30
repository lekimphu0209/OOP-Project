package ecosystem.view;

/**
 * LEGACY VIEW - God Class (235 lines)
 * 
 * This is the old view system that combined UI, camera, rendering, and input handling.
 * Still used by MainLegacy.java for backward compatibility.
 * 
 * For the new modular view system, see:
 * - ecosystem.ui.views.SimulationView
 * - ecosystem.ui.panels.MapPanel
 * - ecosystem.ui.panels.ControlToolbar
 * - ecosystem.ui.panels.StatusPanel
 * - ecosystem.ui.camera.CameraController
 */

import ecosystem.controller.SimulationController;
import ecosystem.entities.Animal;
import ecosystem.entities.Plant;
import ecosystem.environment.Environment;
import ecosystem.physics.Vector2D;
import ecosystem.terrain.Tile;
import ecosystem.terrain.TerrainType;
import ecosystem.view.render.IRenderStrategy;
import ecosystem.view.render.legacy.BasicRenderer;
import ecosystem.view.render.legacy.AdvancedRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BasicView extends JFrame implements IObserver {
    protected Environment environment;
    protected SimulationController controller;
    protected JPanel mapPanel;
    protected JLabel infoLabel;
    protected CameraController camera;
    protected InputHandler inputHandler;
    protected String actionMode = "inspect"; // inspect, plant_food, or place_obstacle
    protected IRenderStrategy currentRenderer;
    protected boolean useGraphicMode = false; // false: Basic, true: Đồ họa
    protected ControlPanel controlPanel;

    public BasicView(Environment environment) {
        this.environment = environment;
        this.environment.registerObserver(this);
        this.currentRenderer = new BasicRenderer();
        this.camera = new CameraController();
        setupWindow();
        createMapPanel();
        setupMouseListeners();
        layoutComponents();
    }

    private void setupWindow() {
        setTitle("Wild-Life Eco Simulation - Basic Mode");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 900);
        setLayout(new BorderLayout());
    }

    private void createMapPanel() {
        mapPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (currentRenderer != null) {
                    currentRenderer.render(g2d, environment, camera.getCellSize(), camera.getZoomLevel(), 
                                        camera.getOffsetX(), camera.getOffsetY(), getWidth(), getHeight());
                }
            }
        };
    }

    private void setupMouseListeners() {
        this.inputHandler = new InputHandler(mapPanel, camera);
        mapPanel.addMouseListener(inputHandler.createMousePressListener());
        mapPanel.addMouseMotionListener(inputHandler.createMouseMotionListener());
        mapPanel.addMouseWheelListener(inputHandler.createMouseWheelListener());
        
        // Keep map click handler in BasicView
        mapPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    handleMapClick(e);
                }
            }
        });
    }

    private void layoutComponents() {
        infoLabel = new JLabel("Thông tin hệ sinh thái");
        controlPanel = new ControlPanel(this, controller);
        add(controlPanel.createControlPanel(), BorderLayout.NORTH);
        add(mapPanel, BorderLayout.CENTER);
        add(infoLabel, BorderLayout.SOUTH);
    }

    public void setActionMode(String mode, String message) {
        actionMode = mode;
        infoLabel.setText(message);
    }

    public void toggleRenderer(JButton btn) {
        useGraphicMode = !useGraphicMode;
        if (useGraphicMode) {
            currentRenderer = new AdvancedRenderer();
            btn.setText("Chế độ: Đồ họa");
            setTitle("Wild-Life Eco Simulation - Đồ họa Mode");
        } else {
            currentRenderer = new BasicRenderer();
            btn.setText("Chế độ: Basic");
            setTitle("Wild-Life Eco Simulation - Basic Mode");
        }
        mapPanel.repaint();
    }

    protected void handleMapClick(MouseEvent e) {
        int currentCellSize = (int) (camera.getCellSize() * camera.getZoomLevel());
        int x = (e.getX() - camera.getOffsetX()) / currentCellSize;
        int y = (e.getY() - camera.getOffsetY()) / currentCellSize;
        
        if (x < 0 || y < 0 || x >= environment.getGrid().getWidth() || y >= environment.getGrid().getHeight()) {
            return;
        }

        // Check for animal at clicked position first
        Animal clickedAnimal = null;
        for (Animal animal : environment.getAnimals()) {
            if (animal == null) continue;
            int ax = (int) animal.getPosition().getX();
            int ay = (int) animal.getPosition().getY();
            if (ax == x && ay == y) {
                clickedAnimal = animal;
                break;
            }
        }

        if (clickedAnimal != null) {
            showAnimalInfo(clickedAnimal);
            return;
        }

        // If no animal clicked, proceed with action mode
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
            updateView();
        }
    }

    protected void showAnimalInfo(Animal animal) {
        String strategyName = animal.getStrategy() != null ? animal.getStrategy().getClass().getSimpleName() : "N/A";
        String stateName = animal.getState() != null ? animal.getState().getClass().getSimpleName() : "N/A";
        
        String info = String.format(
            "%s | Máu: %d/%d | Đói: %d | Khát: %d | Tốc độ: %.1f | Ưu tiên: %d | Chiến lược: %s | Trạng thái: %s",
            animal.getName(),
            animal.getHealth(),
            animal.getHealth() > 0 ? 100 : 0,
            animal.getHunger(),
            animal.getThirst(),
            animal.getBaseSpeed(),
            animal.getPriority(),
            strategyName,
            stateName
        );
        infoLabel.setText(info);
    }

    public void setController(SimulationController controller) {
        this.controller = controller;
        if (controlPanel != null) {
            controlPanel.setController(controller);
        }
    }

    public void update() {
        mapPanel.repaint();
        String pauseStatus = controller != null && controller.isPaused() ? " [TẠM DỪNG]" : "";
        infoLabel.setText("Mùa: " + environment.getSeason().getName() + " (" + environment.getSeason().getDescription() + ")" +
                        " | Động vật: " + environment.getAnimals().size() +
                        " | Thực vật: " + environment.getPlants().size() + pauseStatus);
    }

    @Override
    public void updateView() {
        update();
    }
}
