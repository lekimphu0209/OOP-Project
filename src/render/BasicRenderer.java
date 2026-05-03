package render;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class BasicRenderer implements RenderStrategy {
    @Override
    public void render(GraphicsContext gc, Object model) {
        gc.setFill(Color.GREEN);
        gc.fillOval(50, 50, 30, 30); // Giả lập vẽ một sinh vật
        gc.fillText("Basic Mode", 10, 20);
    }
}