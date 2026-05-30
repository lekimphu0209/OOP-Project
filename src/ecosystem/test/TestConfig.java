package ecosystem.test;

import java.util.HashSet;
import java.util.Set;

/**
 * Test Configuration for enabling/disabling specific animal types
 * Set ENABLE_TEST_MODE = true to use this configuration
 */
public class TestConfig {
    public static final boolean ENABLE_TEST_MODE = true;
    
    // Enable/disable specific animal types
    private static final boolean ENABLE_RABBIT = true;
    private static final boolean ENABLE_DEER = true;
    private static final boolean ENABLE_WOLF = true;
    private static final boolean ENABLE_TIGER = true;
    private static final boolean ENABLE_ELEPHANT = true;
    private static final boolean ENABLE_HUMAN = true;
    private static final boolean ENABLE_FISH = true;
    private static final boolean ENABLE_DUCK = true;
    private static final boolean ENABLE_CROCODILE = true;
    
    // Get enabled animal types as set of type IDs
    public static Set<Integer> getEnabledAnimalTypes() {
        Set<Integer> enabledTypes = new HashSet<>();
        
        if (ENABLE_RABBIT) enabledTypes.add(0);  // Rabbit
        if (ENABLE_DEER) enabledTypes.add(1);    // Deer
        if (ENABLE_WOLF) enabledTypes.add(2);    // Wolf
        if (ENABLE_TIGER) enabledTypes.add(3);   // Tiger
        if (ENABLE_ELEPHANT) enabledTypes.add(4); // Elephant
        if (ENABLE_HUMAN) enabledTypes.add(5);   // Human
        if (ENABLE_FISH) enabledTypes.add(6);    // Fish
        if (ENABLE_DUCK) enabledTypes.add(7);    // Duck
        if (ENABLE_CROCODILE) enabledTypes.add(8); // Crocodile
        
        return enabledTypes;
    }
    
    // Check if a specific animal type is enabled
    public static boolean isAnimalTypeEnabled(int type) {
        return getEnabledAnimalTypes().contains(type);
    }
    
    // Print current configuration
    public static void printConfig() {
        System.out.println("=== TEST MODE CONFIGURATION ===");
        System.out.println("ENABLE_TEST_MODE: " + ENABLE_TEST_MODE);
        System.out.println("Enabled animals:");
        if (ENABLE_RABBIT) System.out.println("  - Rabbit");
        if (ENABLE_DEER) System.out.println("  - Deer");
        if (ENABLE_WOLF) System.out.println("  - Wolf");
        if (ENABLE_TIGER) System.out.println("  - Tiger");
        if (ENABLE_ELEPHANT) System.out.println("  - Elephant");
        if (ENABLE_HUMAN) System.out.println("  - Human");
        if (ENABLE_FISH) System.out.println("  - Fish");
        if (ENABLE_DUCK) System.out.println("  - Duck");
        if (ENABLE_CROCODILE) System.out.println("  - Crocodile");
        System.out.println("===============================");
    }
}
