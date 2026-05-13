package ecosystem.view.render;

import ecosystem.entities.Animal;
import ecosystem.entities.Plant;
import ecosystem.environment.Environment;
import ecosystem.terrain.Tile;

import java.awt.Color;
import java.awt.Graphics2D;

public class BasicRenderer implements IRenderStrategy {

    @Override
    public void render(Graphics2D g2d, Environment env, int cellSize, int offsetX, int offsetY) {
        Tile[][] tiles = env.getGrid().getAllTiles();

        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                Tile tile = tiles[i][j];
                switch (tile.getType().name()) {
                    case "GRASS": g2d.setColor(new Color(34, 139, 34)); break;
                    case "FOREST": g2d.setColor(new Color(0, 100, 0)); break;
                    case "WATER": g2d.setColor(new Color(30, 144, 255)); break;
                    case "MUD": g2d.setColor(new Color(139, 69, 19)); break;
                    case "OBSTACLE": g2d.setColor(new Color(128, 128, 128)); break;
                    default: g2d.setColor(Color.WHITE);
                }
                
                int drawX = i * cellSize + offsetX;
                int drawY = j * cellSize + offsetY;
                g2d.fillRect(drawX, drawY, cellSize, cellSize);
                g2d.setColor(new Color(0, 0, 0, 30)); 
                g2d.drawRect(drawX, drawY, cellSize, cellSize);
            }
        }

        for (Plant plant : env.getPlants()) {
            g2d.setColor(new Color(50, 205, 50));
            int size = (int)(cellSize * 0.4);
            int drawX = (int)plant.getPosition().getX() * cellSize + offsetX + cellSize / 2 - size / 2;
            int drawY = (int)plant.getPosition().getY() * cellSize + offsetY + cellSize / 2 - size / 2;
            g2d.fillOval(drawX, drawY, size, size);
        }

        for (Animal animal : env.getAnimals()) {
            if (animal.isPredator()) g2d.setColor(Color.RED);
            else g2d.setColor(Color.WHITE);

            int size = (int)(cellSize * 0.6);
            int drawX = (int)animal.getPosition().getX() * cellSize + offsetX + cellSize / 2 - size / 2;
            int drawY = (int)animal.getPosition().getY() * cellSize + offsetY + cellSize / 2 - size / 2;
            
            if (animal.isPredator()) g2d.fillRect(drawX, drawY, size, size);
            else g2d.fillOval(drawX, drawY, size, size);
            
            int healthW = (int) (cellSize * 0.66);
            g2d.setColor(Color.RED);
            g2d.fillRect(drawX + size/2 - healthW/2, drawY - 5, healthW, 3);
            g2d.setColor(Color.GREEN);
            g2d.fillRect(drawX + size/2 - healthW/2, drawY - 5, (int) (healthW * animal.getHealth() / 100.0), 3);
        }
    }
}