package ecosystem.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelListener;

/**
 * Handles mouse input for camera control (pan, zoom).
 * Extracted from BasicView to reduce file size.
 */
public class InputHandler {
    private JPanel mapPanel;
    private CameraController camera;
    private Point lastMousePos;

    public InputHandler(JPanel mapPanel, CameraController camera) {
        this.mapPanel = mapPanel;
        this.camera = camera;
    }

    public MouseAdapter createMouseMotionListener() {
        return new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    handleCameraPan(e);
                }
            }
        };
    }

    public void recordPress(MouseEvent e) {
        lastMousePos = e.getPoint();
    }

    public MouseWheelListener createMouseWheelListener() {
        return e -> handleZoom(e);
    }

    private void handleCameraPan(MouseEvent e) {
        camera.pan(e.getX() - lastMousePos.x, e.getY() - lastMousePos.y);
        lastMousePos = e.getPoint();
        mapPanel.repaint();
    }

    private void handleZoom(java.awt.event.MouseWheelEvent e) {
        double oldZoom = camera.getZoomLevel();
        updateZoomLevel(e.getWheelRotation());
        adjustOffsetForZoom(e, oldZoom);
        mapPanel.repaint();
    }

    private void updateZoomLevel(int wheelRotation) {
        double newZoom = camera.getZoomLevel();
        if (wheelRotation < 0) {
            newZoom = Math.min(newZoom * 1.1, 5.0);
        } else {
            newZoom = Math.max(newZoom / 1.1, 0.2);
        }
        camera.setZoomLevel(newZoom);
    }

    private void adjustOffsetForZoom(java.awt.event.MouseWheelEvent e, double oldZoom) {
        double zoomFactor = camera.getZoomLevel() / oldZoom;
        int newOffsetX = (int) (e.getX() - (e.getX() - camera.getOffsetX()) * zoomFactor);
        int newOffsetY = (int) (e.getY() - (e.getY() - camera.getOffsetY()) * zoomFactor);
        camera.setOffsetX(newOffsetX);
        camera.setOffsetY(newOffsetY);
    }
}
