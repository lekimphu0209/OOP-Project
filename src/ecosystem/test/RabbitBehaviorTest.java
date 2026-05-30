package ecosystem.test;

import ecosystem.behavior.ScaredStrategy;
import ecosystem.entities.Plant;
import ecosystem.entities.Rabbit;
import ecosystem.environment.Environment;
import ecosystem.physics.Vector2D;
import ecosystem.terrain.Grid;
import ecosystem.terrain.TerrainType;

public class RabbitBehaviorTest {
    public static void main(String[] args) {
        System.out.println("=== RABBIT BEHAVIOR TESTS ===\n");
        
        testRabbitEat();
        testRabbitIdle();
        testRabbitRunFromWolf();
        testRabbitDrink();
        testRabbitPlantDisappears();
        
        System.out.println("\n=== ALL TESTS COMPLETED ===");
    }
    
    private static void testRabbitEat() {
        System.out.println("Test 1: Rabbit eats plant");
        System.out.println("------------------------");
        
        Grid grid = new Grid(10, 10);
        Environment env = new Environment(grid);
        
        // Create rabbit at position (5, 5)
        Rabbit rabbit = new Rabbit(new Vector2D(5, 5));
        rabbit.setHunger(50); // Make rabbit hungry
        env.addAnimal(rabbit);
        
        // Create plant at same position
        Plant plant = new Plant(new Vector2D(5, 5), "carrot", true, 10.0);
        env.addPlant(plant);
        
        System.out.println("Before eat:");
        System.out.println("  Rabbit hunger: " + rabbit.getHunger());
        System.out.println("  Plant alive: " + plant.isAlive());
        System.out.println("  Plant count: " + env.getPlants().size());
        
        // Eat the plant
        rabbit.eat(plant);
        
        System.out.println("After eat:");
        System.out.println("  Rabbit hunger: " + rabbit.getHunger());
        System.out.println("  Plant alive: " + plant.isAlive());
        System.out.println("  Plant count: " + env.getPlants().size());
        
        boolean passed = rabbit.getHunger() == 0 && !plant.isAlive();
        System.out.println("Result: " + (passed ? "✅ PASSED" : "❌ FAILED"));
        System.out.println();
    }
    
    private static void testRabbitIdle() {
        System.out.println("Test 2: Rabbit idle state");
        System.out.println("------------------------");
        
        Grid grid = new Grid(10, 10);
        Environment env = new Environment(grid);
        
        Rabbit rabbit = new Rabbit(new Vector2D(5, 5));
        rabbit.setHunger(0); // Not hungry
        rabbit.setThirst(0); // Not thirsty
        env.addAnimal(rabbit);
        
        ScaredStrategy strategy = new ScaredStrategy();
        strategy.execute(rabbit, env);
        
        System.out.println("Rabbit action state: " + rabbit.getActionState());
        
        boolean passed = rabbit.getActionState().isEmpty() || rabbit.getActionState().equals("Nghỉ ngơi");
        System.out.println("Result: " + (passed ? "✅ PASSED" : "❌ FAILED"));
        System.out.println();
    }
    
    private static void testRabbitRunFromWolf() {
        System.out.println("Test 3: Rabbit runs from wolf");
        System.out.println("------------------------------");
        
        Grid grid = new Grid(10, 10);
        Environment env = new Environment(grid);
        
        // Create rabbit
        Rabbit rabbit = new Rabbit(new Vector2D(5, 5));
        rabbit.setHunger(0);
        rabbit.setThirst(0);
        env.addAnimal(rabbit);
        
        // Create wolf nearby
        ecosystem.entities.Wolf wolf = new ecosystem.entities.Wolf(new Vector2D(3, 5));
        env.addAnimal(wolf);
        
        Vector2D initialPos = rabbit.getPosition();
        
        ScaredStrategy strategy = new ScaredStrategy();
        strategy.execute(rabbit, env);
        
        Vector2D newPos = rabbit.getPosition();
        double distanceMoved = initialPos.distanceTo(newPos);
        
        System.out.println("Initial position: " + initialPos);
        System.out.println("New position: " + newPos);
        System.out.println("Distance moved: " + distanceMoved);
        System.out.println("Action state: " + rabbit.getActionState());
        
        boolean passed = rabbit.getActionState().contains("Chạy") || rabbit.getActionState().contains("Trốn");
        System.out.println("Result: " + (passed ? "✅ PASSED" : "❌ FAILED"));
        System.out.println();
    }
    
    private static void testRabbitDrink() {
        System.out.println("Test 4: Rabbit drinks water");
        System.out.println("---------------------------");
        
        Grid grid = new Grid(10, 10);
        // Set tile at (5,5) to water
        grid.getTile(5, 5).setType(TerrainType.WATER);
        
        Environment env = new Environment(grid);
        
        Rabbit rabbit = new Rabbit(new Vector2D(5, 5));
        rabbit.setThirst(50); // Make thirsty
        env.addAnimal(rabbit);
        
        System.out.println("Before drink:");
        System.out.println("  Rabbit thirst: " + rabbit.getThirst());
        
        rabbit.drink(env);
        
        System.out.println("After drink:");
        System.out.println("  Rabbit thirst: " + rabbit.getThirst());
        
        boolean passed = rabbit.getThirst() == 0;
        System.out.println("Result: " + (passed ? "✅ PASSED" : "❌ FAILED"));
        System.out.println();
    }
    
    private static void testRabbitPlantDisappears() {
        System.out.println("Test 5: Plant disappears when eaten");
        System.out.println("------------------------------------");
        
        Grid grid = new Grid(10, 10);
        Environment env = new Environment(grid);
        
        Rabbit rabbit = new Rabbit(new Vector2D(5, 5));
        rabbit.setHunger(50);
        env.addAnimal(rabbit);
        
        Plant plant = new Plant(new Vector2D(5, 5), "carrot", true, 10.0);
        env.addPlant(plant);
        
        int plantCountBefore = env.getPlants().size();
        System.out.println("Plant count before: " + plantCountBefore);
        
        rabbit.eat(plant);
        env.update(); // Remove dead plants
        
        int plantCountAfter = env.getPlants().size();
        System.out.println("Plant count after: " + plantCountAfter);
        
        boolean passed = plantCountAfter == plantCountBefore - 1;
        System.out.println("Result: " + (passed ? "✅ PASSED" : "❌ FAILED"));
        System.out.println();
    }
}
