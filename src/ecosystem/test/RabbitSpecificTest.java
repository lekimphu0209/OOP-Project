package ecosystem.test;

import ecosystem.behavior.ScaredStrategy;
import ecosystem.entities.Plant;
import ecosystem.entities.Rabbit;
import ecosystem.entities.Wolf;
import ecosystem.environment.Environment;
import ecosystem.physics.Vector2D;
import ecosystem.terrain.Grid;
import ecosystem.terrain.TerrainType;

public class RabbitSpecificTest {
    public static void main(String[] args) {
        System.out.println("=== RABBIT SPECIFIC BEHAVIOR TESTS ===\n");
        
        test1_RabbitFearsWolf();
        test2_RabbitAbandonsEatingForWolf();
        test3_RabbitFindsFoodWhenHungry();
        test4_RabbitFindsWaterWhenThirsty();
        test5_RabbitAvoidsObstacles();
        test6_RabbitHidesInBush();
        test7_RabbitRestsWhenTired();
        test8_RabbitNotDumb();
        
        System.out.println("\n=== ALL SPECIFIC TESTS COMPLETED ===");
    }
    
    // Test 1: Thỏ có thật sự sợ Sói không?
    // Kịch bản: Rabbit at (10,10), Wolf at (12,10), không có cỏ, không có nước
    // Đúng: Rabbit phát hiện Wolf → chuyển sang RUN_AWAY → khoảng cách tới Wolf tăng dần
    private static void test1_RabbitFearsWolf() {
        System.out.println("Test 1: Thỏ có thật sự sợ Sói không?");
        System.out.println("Kịch bản: Rabbit at (10,10), Wolf at (12,10)");
        System.out.println("----------------------------------------");
        
        Grid grid = new Grid(20, 20);
        Environment env = new Environment(grid);
        
        Rabbit rabbit = new Rabbit(new Vector2D(10, 10));
        rabbit.setHunger(0);
        rabbit.setThirst(0);
        env.addAnimal(rabbit);
        
        Wolf wolf = new Wolf(new Vector2D(12, 10));
        env.addAnimal(wolf);
        
        double initialDistance = rabbit.getPosition().distanceTo(wolf.getPosition());
        System.out.println("Khoảng cách ban đầu: " + initialDistance);
        
        ScaredStrategy strategy = new ScaredStrategy();
        strategy.execute(rabbit, env);
        
        double newDistance = rabbit.getPosition().distanceTo(wolf.getPosition());
        System.out.println("Khoảng cách sau: " + newDistance);
        System.out.println("Action state: " + rabbit.getActionState());
        
        boolean isRunningAway = rabbit.getActionState().contains("Chạy") || rabbit.getActionState().contains("Trốn");
        boolean distanceIncreased = newDistance > initialDistance;
        
        boolean passed = isRunningAway && distanceIncreased;
        System.out.println("Kết quả: " + (passed ? "✅ PASSED - Thỏ chạy trốn sói" : "❌ FAILED"));
        System.out.println();
    }
    
    // Test 2: Khi đang ăn có bỏ chạy không?
    // Kịch bản: Rabbit đang ăn cỏ, Wolf xuất hiện trong phạm vi phát hiện
    // Đúng: EAT → RUN_AWAY (Nguy hiểm > Đói)
    private static void test2_RabbitAbandonsEatingForWolf() {
        System.out.println("Test 2: Khi đang ăn có bỏ chạy không?");
        System.out.println("Kịch bản: Rabbit đang ăn, Wolf xuất hiện");
        System.out.println("----------------------------------------");
        
        Grid grid = new Grid(20, 20);
        Environment env = new Environment(grid);
        
        Rabbit rabbit = new Rabbit(new Vector2D(10, 10));
        rabbit.setHunger(50);
        // Note: eatTimer is private, cannot set directly
        rabbit.setActionState("Đang ăn");
        env.addAnimal(rabbit);
        
        Wolf wolf = new Wolf(new Vector2D(12, 10));
        env.addAnimal(wolf);
        
        System.out.println("Trước: Action state = " + rabbit.getActionState());
        
        ScaredStrategy strategy = new ScaredStrategy();
        strategy.execute(rabbit, env);
        
        System.out.println("Sau: Action state = " + rabbit.getActionState());
        
        boolean isRunningAway = rabbit.getActionState().contains("Chạy") || rabbit.getActionState().contains("Trốn");
        
        boolean passed = isRunningAway;
        System.out.println("Kết quả: " + (passed ? "✅ PASSED - Thỏ bỏ ăn để chạy trốn" : "❌ FAILED"));
        System.out.println();
    }
    
    // Test 3: Có tìm thức ăn khi đói không?
    // Kịch bản: Hunger = 90%, Có cỏ gần đó
    // Đúng: Rabbit di chuyển về phía cỏ → ăn cỏ → Hunger giảm
    private static void test3_RabbitFindsFoodWhenHungry() {
        System.out.println("Test 3: Có tìm thức ăn khi đói không?");
        System.out.println("Kịch bản: Hunger = 90%, có cỏ gần đó");
        System.out.println("----------------------------------------");
        
        Grid grid = new Grid(20, 20);
        Environment env = new Environment(grid);
        
        Rabbit rabbit = new Rabbit(new Vector2D(10, 10));
        rabbit.setHunger(90);
        rabbit.setThirst(0);
        env.addAnimal(rabbit);
        
        Plant plant = new Plant(new Vector2D(12, 10), "carrot", true, 10.0);
        env.addPlant(plant);
        
        System.out.println("Trước: Hunger = " + rabbit.getHunger());
        System.out.println("Vị trí cỏ: " + plant.getPosition());
        
        ScaredStrategy strategy = new ScaredStrategy();
        strategy.execute(rabbit, env);
        
        System.out.println("Action state: " + rabbit.getActionState());
        
        boolean isSeekingFood = rabbit.getActionState().equals("Tìm thức ăn") || rabbit.getActionState().equals("Đang ăn");
        
        boolean passed = isSeekingFood;
        System.out.println("Kết quả: " + (passed ? "✅ PASSED - Thỏ tìm thức ăn khi đói" : "❌ FAILED"));
        System.out.println();
    }
    
    // Test 4: Có tìm nước khi khát không?
    // Kịch bản: Thirst = 95%, Lake gần đó
    // Đúng: Rabbit đi tới hồ → uống nước
    private static void test4_RabbitFindsWaterWhenThirsty() {
        System.out.println("Test 4: Có tìm nước khi khát không?");
        System.out.println("Kịch bản: Thirst = 95%, có lake gần đó");
        System.out.println("----------------------------------------");
        
        Grid grid = new Grid(20, 20);
        grid.getTile(12, 10).setType(TerrainType.WATER);
        
        Environment env = new Environment(grid);
        
        Rabbit rabbit = new Rabbit(new Vector2D(10, 10));
        rabbit.setHunger(0);
        rabbit.setThirst(95);
        env.addAnimal(rabbit);
        
        System.out.println("Trước: Thirst = " + rabbit.getThirst());
        
        ScaredStrategy strategy = new ScaredStrategy();
        strategy.execute(rabbit, env);
        
        System.out.println("Action state: " + rabbit.getActionState());
        
        boolean isSeekingWater = rabbit.getActionState().equals("Tìm nước") || rabbit.getActionState().equals("Đang uống");
        
        boolean passed = isSeekingWater;
        System.out.println("Kết quả: " + (passed ? "✅ PASSED - Thỏ tìm nước khi khát" : "❌ FAILED"));
        System.out.println();
    }
    
    // Test 5: Có tránh vật cản không?
    // Kịch bản: Rabbit → Rock
    // Đúng: Rabbit đổi hướng hoặc tìm đường vòng
    private static void test5_RabbitAvoidsObstacles() {
        System.out.println("Test 5: Có tránh vật cản không?");
        System.out.println("Kịch bản: Rabbit đi về phía Rock");
        System.out.println("----------------------------------------");
        
        Grid grid = new Grid(20, 20);
        grid.getTile(12, 10).setType(TerrainType.OBSTACLE); // Rock ở phía trước
        
        Environment env = new Environment(grid);
        
        Rabbit rabbit = new Rabbit(new Vector2D(10, 10));
        rabbit.setHunger(0);
        rabbit.setThirst(0);
        // Note: direction is private, cannot get directly
        env.addAnimal(rabbit);
        
        System.out.println("Vị trí Rabbit: " + rabbit.getPosition());
        System.out.println("Vị trí Rock: (12,10)");
        
        // Thử di chuyển
        rabbit.move(env);
        
        System.out.println("Vị trí sau khi di chuyển: " + rabbit.getPosition());
        double newX = rabbit.getPosition().getX();
        boolean passedThroughRock = newX >= 12;
        System.out.println("Có đi xuyên qua Rock: " + passedThroughRock);
        
        boolean passed = !passedThroughRock; // Không đi xuyên qua Rock
        System.out.println("Kết quả: " + (passed ? "✅ PASSED - Thỏ tránh vật cản" : "❌ FAILED - Thỏ đi xuyên qua"));
        System.out.println();
    }
    
    // Test 6: Có vào bụi rậm để trốn không?
    // Kịch bản: Rabbit, Bush, Wolf
    // Đúng: Rabbit chạy tới Bush → HIDE
    private static void test6_RabbitHidesInBush() {
        System.out.println("Test 6: Có vào bụi rậm để trốn không?");
        System.out.println("Kịch bản: Rabbit, Bush gần đó, Wolf ở xa");
        System.out.println("----------------------------------------");
        
        Grid grid = new Grid(20, 20);
        grid.getTile(12, 10).setType(TerrainType.FOREST); // Bush/Forest gần đó
        
        Environment env = new Environment(grid);
        
        Rabbit rabbit = new Rabbit(new Vector2D(10, 10));
        rabbit.setHunger(0);
        rabbit.setThirst(0);
        env.addAnimal(rabbit);
        
        Wolf wolf = new Wolf(new Vector2D(8, 10)); // Wolf ở phía sau
        env.addAnimal(wolf);
        
        System.out.println("Vị trí Rabbit: " + rabbit.getPosition());
        System.out.println("Vị trí Bush: (12,10)");
        System.out.println("Vị trí Wolf: " + wolf.getPosition());
        
        ScaredStrategy strategy = new ScaredStrategy();
        strategy.execute(rabbit, env);
        
        System.out.println("Action state: " + rabbit.getActionState());
        
        boolean isHiding = rabbit.getActionState().equals("Trốn trong rừng") || rabbit.getActionState().equals("Chạy vào rừng");
        
        boolean passed = isHiding;
        System.out.println("Kết quả: " + (passed ? "✅ PASSED - Thỏ tìm bụi rậm trốn" : "❌ FAILED"));
        System.out.println();
    }
    
    // Test 7: Có nghỉ khi mệt không?
    // Kịch bản: Stamina = 0
    // Đúng: REST
    private static void test7_RabbitRestsWhenTired() {
        System.out.println("Test 7: Có nghỉ khi mệt không?");
        System.out.println("Kịch bản: Stamina = 0");
        System.out.println("----------------------------------------");
        
        Grid grid = new Grid(20, 20);
        Environment env = new Environment(grid);
        
        Rabbit rabbit = new Rabbit(new Vector2D(10, 10));
        rabbit.setHunger(0);
        rabbit.setThirst(0);
        // Note: Animal class không có stamina field, test này không thể thực hiện
        env.addAnimal(rabbit);
        
        System.out.println("⚠️ WARNING: Animal class không có field stamina");
        System.out.println("Kết quả: ⚠️ SKIPPED - Cần thêm stamina vào Animal class");
        System.out.println();
    }
    
    // Test 8: Thỏ có bị "ngu" không?
    // Kịch bản: Wolf bên trái, Bush bên phải
    // Đúng: Rabbit chạy về Bush
    // Sai: Rabbit chạy lung tung, chạy vòng tròn, chạy vào góc chết
    private static void test8_RabbitNotDumb() {
        System.out.println("Test 8: Thỏ có bị 'ngu' không?");
        System.out.println("Kịch bản: Wolf bên trái (8,10), Bush bên phải (12,10)");
        System.out.println("----------------------------------------");
        
        Grid grid = new Grid(20, 20);
        grid.getTile(12, 10).setType(TerrainType.FOREST); // Bush bên phải
        
        Environment env = new Environment(grid);
        
        Rabbit rabbit = new Rabbit(new Vector2D(10, 10));
        rabbit.setHunger(0);
        rabbit.setThirst(0);
        env.addAnimal(rabbit);
        
        Wolf wolf = new Wolf(new Vector2D(8, 10)); // Wolf bên trái
        env.addAnimal(wolf);
        
        System.out.println("Vị trí Rabbit: " + rabbit.getPosition());
        System.out.println("Vị trí Wolf: " + wolf.getPosition() + " (bên trái)");
        System.out.println("Vị trí Bush: (12,10) (bên phải)");
        
        ScaredStrategy strategy = new ScaredStrategy();
        strategy.execute(rabbit, env);
        
        System.out.println("Action state: " + rabbit.getActionState());
        
        Vector2D bushPos = new Vector2D(12, 10);
        Vector2D wolfPos = wolf.getPosition();
        double distanceToBush = rabbit.getPosition().distanceTo(bushPos);
        double distanceToWolf = rabbit.getPosition().distanceTo(wolfPos);
        
        System.out.println("Khoảng cách đến Bush: " + distanceToBush);
        System.out.println("Khoảng cách đến Wolf: " + distanceToWolf);
        
        boolean isRunningToBush = rabbit.getActionState().equals("Chạy vào rừng");
        boolean movingAwayFromWolf = distanceToWolf > 2.0;
        
        boolean passed = isRunningToBush || movingAwayFromWolf;
        System.out.println("Kết quả: " + (passed ? "✅ PASSED - Thỏ chọn hướng thông minh" : "❌ FAILED - Thỏ bị 'ngu'"));
        System.out.println();
    }
}
