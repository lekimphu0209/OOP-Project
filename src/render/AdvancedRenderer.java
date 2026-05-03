package render;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class AdvancedRenderer implements RenderStrategy {
    private Image sprite = new Image("file:resources/sprites/creature.png");

    @Override
    public void render(GraphicsContext gc, Object model) {
        gc.drawImage(sprite, 50, 50, 40, 40);
        gc.fillText("Advanced Mode (Sprites)", 10, 20);
    }
}