package ecosystem.view;

import ecosystem.environment.Environment;
import ecosystem.view.render.legacy.AdvancedRenderer;

public class GraphicalView extends BasicView {

    public GraphicalView(Environment environment) {
        super(environment);
        setTitle("Wild-Life Eco Simulation - Đồ họa Mode");

        // Use AdvancedRenderer (legacy but with tileset support)
        useGraphicMode = true;
        currentRenderer = new AdvancedRenderer();
    }
}
