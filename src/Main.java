import controller.MapController;

public class Main {
    public static void main(String[] args) {

        MapController controller = new MapController();

        controller.showMap();

        System.out.println("\n--- Inspect tile ---");
        controller.inspectTile(2, 3);
    }
}