package ecosystem;

import ecosystem.controller.SimulationController;

public class Main {
    public static void main(String[] args) {   

        try {
            System.out.println("=== Ecosystem Simulation ===");
            SimulationController controller = new SimulationController(30, 30);
            controller.initialize();
            System.out.println("System started successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
}
