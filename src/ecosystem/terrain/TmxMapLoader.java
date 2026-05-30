package ecosystem.terrain;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class TmxMapLoader {
    private TmxMapLoader() {}

    /**
     * Loads a TMX (Tiled) map that uses CSV encoding for tile layers.
     * This project only needs TerrainType, so we map GID -> TerrainType with a simple table.
     *
     * Current default mapping for your TMX:
     * - 94  -> GRASS (base layer)
     * - 274 -> WATER (overlay)
     * - 36  -> FOREST
     * - 172 -> MUD (soil layer)
     * - 164 -> OBSTACLE (rock layer)
     * - 0   -> empty/no override
     *
     * If your tileset uses different IDs, adjust gidToTerrain().
     */
    public static Grid loadGrid(String tmxPath) throws Exception {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(tmxPath));
        doc.getDocumentElement().normalize();

        Element mapEl = (Element) doc.getElementsByTagName("map").item(0);
        int width = Integer.parseInt(mapEl.getAttribute("width"));
        int height = Integer.parseInt(mapEl.getAttribute("height"));

        Grid grid = new Grid(width, height, false);

        NodeList layers = doc.getElementsByTagName("layer");
        List<int[]> layerData = new ArrayList<>();
        List<String> layerNames = new ArrayList<>();
        
        for (int i = 0; i < layers.getLength(); i++) {
            Element layerEl = (Element) layers.item(i);
            String layerName = layerEl.getAttribute("name").toLowerCase();
            NodeList dataNodes = layerEl.getElementsByTagName("data");
            if (dataNodes.getLength() == 0) continue;

            Element dataEl = (Element) dataNodes.item(0);
            String encoding = dataEl.getAttribute("encoding");
            if (!"csv".equalsIgnoreCase(encoding)) {
                throw new IllegalArgumentException("Only CSV TMX is supported. Found encoding=" + encoding);
            }

            int[] data = parseCsvLayer(dataEl.getTextContent(), width * height);
            layerData.add(data);
            layerNames.add(layerName);
        }

        // Apply layers with smart merging
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int idx = y * width + x;
                int finalGid = 0;
                
                for (int i = 0; i < layerData.size(); i++) {
                    int gid = layerData.get(i)[idx];
                    String name = layerNames.get(i);
                    
                    if (gid == 0) continue; // Always skip empty
                    
                    // Smart override:
                    // 1. If it's the first layer, it's the base.
                    // 2. If it's a higher layer, ignore ID 40 (Rock) UNLESS the layer is actually "rock".
                    // This fixes the issue where ID 40 was used as fill in water/forest layers.
                    if (i == 0) {
                        finalGid = gid;
                    } else {
                        if (gid == 40 && !name.contains("rock")) {
                            continue; // Skip this filler tile
                        }
                        finalGid = gid;
                    }
                }
                
                TerrainType type = gidToTerrain(finalGid);
                grid.setTile(x, y, new Tile(x, y, type, finalGid));
            }
        }

        return grid;
    }

    private static int[] parseCsvLayer(String csv, int expectedCount) {
        String[] parts = csv.replace("\r", "").replace("\n", "").split(",");
        int[] out = new int[expectedCount];
        int j = 0;
        for (String p : parts) {
            String t = p.trim();
            if (t.isEmpty()) continue;
            if (j >= expectedCount) break;
            out[j++] = Integer.parseInt(t);
        }
        if (j < expectedCount) {
            throw new IllegalArgumentException("TMX CSV has fewer tiles than expected: " + j + " < " + expectedCount);
        }
        return out;
    }

    private static TerrainType gidToTerrain(int gid) {
        // Default fallback
        if (gid == 0) return TerrainType.GRASS;

        // Mapping based on actual map.tmx GIDs (Updated)
        if (gid == 94) return TerrainType.GRASS;
        if (gid == 274) return TerrainType.WATER;
        if (gid == 172) return TerrainType.MUD;
        if (gid == 36) return TerrainType.FOREST;
        
        if (gid == 15) return TerrainType.OBSTACLE; // Rock

        // Unknown tiles become grass (safe default)
        return TerrainType.GRASS;
    }
}

