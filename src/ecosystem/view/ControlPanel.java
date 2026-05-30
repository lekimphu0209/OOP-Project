package ecosystem.view;

import ecosystem.controller.SimulationController;
import ecosystem.view.render.IRenderStrategy;
import ecosystem.view.render.legacy.AdvancedRenderer;
import ecosystem.view.render.legacy.BasicRenderer;

import javax.swing.*;

/**
 * Creates and manages the control panel with buttons.
 * Extracted from BasicView to reduce file size.
 */
public class ControlPanel {
    private BasicView view;
    private SimulationController controller;

    public ControlPanel(BasicView view, SimulationController controller) {
        this.view = view;
        this.controller = controller;
    }

    public void setController(SimulationController controller) {
        this.controller = controller;
    }

    public JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.add(createPauseButton());
        panel.add(createInspectButton());
        panel.add(createPlantFoodButton());
        panel.add(createPlaceObstacleButton());
        panel.add(createToggleRendererButton());
        return panel;
    }

    private JButton createPauseButton() {
        JButton btn = new JButton("Tạm dừng");
        btn.addActionListener(e -> {
            if (controller != null) {
                controller.togglePause();
                btn.setText(controller.isPaused() ? "Tiếp tục" : "Tạm dừng");
            }
        });
        return btn;
    }

    private JButton createInspectButton() {
        JButton btn = new JButton("Kiểm tra");
        btn.addActionListener(e -> view.setActionMode("inspect", "Chế độ: Kiểm tra (nhấp vào động vật)"));
        return btn;
    }

    private JButton createPlantFoodButton() {
        JButton btn = new JButton("Trồng cây");
        btn.addActionListener(e -> view.setActionMode("plant_food", "Chế độ: Trồng cây (nhấp vào ô cỏ/rừng)"));
        return btn;
    }

    private JButton createPlaceObstacleButton() {
        JButton btn = new JButton("Đặt vật cản");
        btn.addActionListener(e -> view.setActionMode("place_obstacle", "Chế độ: Đặt vật cản (nhấp vào ô bất kỳ)"));
        return btn;
    }

    private JButton createToggleRendererButton() {
        JButton btn = new JButton("Chế độ: Basic");
        btn.addActionListener(e -> toggleRenderer(btn));
        return btn;
    }

    private void toggleRenderer(JButton btn) {
        view.toggleRenderer(btn);
    }
}
