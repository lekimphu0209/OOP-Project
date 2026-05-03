package render;

import javafx.scene.canvas.GraphicsContext;

public interface RenderStrategy {
    void render(GraphicsContext gc, Object model);
}