package ecosystem.view.render;

import ecosystem.environment.Environment;
import java.awt.Graphics2D;

public interface IRenderStrategy {
    void render(Graphics2D g2d, Environment env, int cellSize, double zoomLevel, int offsetX, int offsetY, int panelWidth, int panelHeight);
}