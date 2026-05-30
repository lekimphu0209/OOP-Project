package ecosystem.view;

import java.awt.Point;

/**
 * Manages camera state for the view (pan, zoom, offset).
 * Extracted from BasicView to reduce file size.
 */
public class CameraController {
    private double zoomLevel = 1.0;
    private int offsetX = 0;
    private int offsetY = 0;
    private int cellSize = 30;

    public double getZoomLevel() {
        return zoomLevel;
    }

    public void setZoomLevel(double zoomLevel) {
        this.zoomLevel = zoomLevel;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    public int getCellSize() {
        return cellSize;
    }

    public void setCellSize(int cellSize) {
        this.cellSize = cellSize;
    }

    public void zoom(double factor) {
        zoomLevel *= factor;
        if (zoomLevel < 0.5) zoomLevel = 0.5;
        if (zoomLevel > 3.0) zoomLevel = 3.0;
    }

    public void pan(int dx, int dy) {
        offsetX += dx;
        offsetY += dy;
    }

    public void reset() {
        zoomLevel = 1.0;
        offsetX = 0;
        offsetY = 0;
    }
}
