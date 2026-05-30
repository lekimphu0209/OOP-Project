package ecosystem.environment;

import ecosystem.terrain.Grid;
import ecosystem.terrain.TerrainType;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;

/**
 * Loads maps from TMX files.
 * Extracted from Environment to reduce file size.
 */
public class MapLoader {
    public static Environment loadFromTMX(String filePath) {
        try {
            File file = new File(filePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            doc.getDocumentElement().normalize();

            Element mapElement = doc.getDocumentElement();
            int width = Integer.parseInt(mapElement.getAttribute("width"));
            int height = Integer.parseInt(mapElement.getAttribute("height"));

            Environment env = new Environment(width, height);
            
            // Parse layers
            NodeList layers = mapElement.getElementsByTagName("layer");
            for (int i = 0; i < layers.getLength(); i++) {
                Element layer = (Element) layers.item(i);
                String layerName = layer.getAttribute("name");
                NodeList dataNodes = layer.getElementsByTagName("data");
                
                if (dataNodes.getLength() > 0) {
                    Element dataElement = (Element) dataNodes.item(0);
                    String csvData = dataElement.getTextContent();
                    String[] tileIds = csvData.split(",");
                    
                    parseLayer(env, tileIds, layerName);
                }
            }
            
            return env;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void parseLayer(Environment env, String[] tileIds, String layerName) {
        Grid grid = env.getGrid();
        int width = grid.getWidth();
        
        for (int i = 0; i < tileIds.length; i++) {
            int tileId = Integer.parseInt(tileIds[i].trim());
            int x = i % width;
            int y = i / width;
            
            TerrainType terrainType = mapTerrainToTerrainType(tileId, layerName);
            if (terrainType != null && grid.getTile(x, y) != null) {
                grid.getTile(x, y).setType(terrainType);
            }
        }
    }

    private static TerrainType mapTerrainToTerrainType(int tileId, String layerName) {
        // 0 = empty/transparent
        // 94 = grass
        // 40 = rock/obstacle
        // 164 = water (example)
        
        if (tileId == 0) return null; // Empty, don't override
        
        // Layer-specific mappings
        if (layerName.equals("grass")) {
            if (tileId == 94) return TerrainType.GRASS;
        } else if (layerName.equals("rock")) {
            if (tileId == 40 || tileId == 164) return TerrainType.OBSTACLE;
            if (tileId >= 100 && tileId <= 150) return TerrainType.FOREST; // Example range for forest
            if (tileId >= 200 && tileId <= 250) return TerrainType.WATER; // Example range for water
            if (tileId >= 250 && tileId <= 300) return TerrainType.MUD; // Example range for mud
        }
        
        // Default mappings
        if (tileId == 94) return TerrainType.GRASS;
        if (tileId == 40) return TerrainType.OBSTACLE;
        
        return null;
    }
}
