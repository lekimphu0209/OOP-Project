package ecosystem.view;

import ecosystem.environment.Environment;
import ecosystem.view.render.AdvancedRenderer;
import ecosystem.view.render.BasicRenderer;

import javax.swing.*;

public class GraphicalView extends BasicView {

    public GraphicalView(Environment environment) {
        super(environment);
        
        this.currentRenderer = new AdvancedRenderer();
        setTitle("Wild-Life Eco Simulation - Graphical Mode");
        
        // Nút chuyển chế độ (Task 39)
        JButton toggleBtn = new JButton("Chuyển chế độ View");
        toggleBtn.addActionListener(e -> {
            if (currentRenderer instanceof AdvancedRenderer) {
                currentRenderer = new BasicRenderer();
                setTitle("Wild-Life Eco Simulation - Basic Mode");
            } else {
                currentRenderer = new AdvancedRenderer();
                setTitle("Wild-Life Eco Simulation - Graphical Mode");
            }
            mapPanel.repaint();
        });
        
        // Gắn nút chuyển chế độ vào thanh công cụ đã có từ BasicView
        if (controlPanel != null) {
            controlPanel.add(toggleBtn);
        }
    }
}