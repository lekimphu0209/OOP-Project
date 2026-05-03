package utils;

import javafx.animation.AnimationTimer;

public class FPSCounter extends AnimationTimer {
    private long lastUpdate = 0;
    private int frameCount = 0;

    @Override
    public void handle(long now) {
        if (lastUpdate > 0) {
            frameCount++;
            if (now - lastUpdate >= 1_000_000_000L) {
                System.out.println("FPS: " + frameCount);
                frameCount = 0;
                lastUpdate = now;
            }
        } else {
            lastUpdate = now;
        }
    }
}