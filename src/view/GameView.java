package view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import render.RenderStrategy;
import render.BasicRenderer;
import event.InputHandler;

public class GameView extends StackPane implements Observer {
    private Canvas canvas;
    private RenderStrategy renderer;
    private Object model; // Thay 'Object' bằng class Model chung của nhóm

    public GameView(double width, double height, Object model) {
        this.canvas = new Canvas(width, height);
        this.model = model;
        this.renderer = new BasicRenderer();
        this.getChildren().add(canvas);
        
        InputHandler inputHandler = new InputHandler(this);
        inputHandler.attachTo(canvas);
    }

    public void setRenderer(RenderStrategy renderer) {
        this.renderer = renderer;
        update();
    }

    @Override
    public void update() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        renderer.render(gc, model);
    }

    public Canvas getCanvas() { return canvas; }
}