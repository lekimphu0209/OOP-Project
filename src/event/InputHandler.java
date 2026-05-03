package event;

import javafx.scene.canvas.Canvas;
import view.GameView;
import render.AdvancedRenderer;
import render.BasicRenderer;

public class InputHandler {
    private GameView view;

    public InputHandler(GameView view) {
        this.view = view;
    }

    public void attachTo(Canvas canvas) {
        canvas.setOnMouseClicked(e -> {
            System.out.println("Clicked at: " + e.getX() + ", " + e.getY());

            if (e.getButton().toString().equals("SECONDARY")) {
                view.setRenderer(new AdvancedRenderer());
            }
        });
    }
}