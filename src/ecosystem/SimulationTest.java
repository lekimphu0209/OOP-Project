package ecosystem;
import java.util.ArrayList;
import java.util.List;

public class SimulationTest {
    public static void main(String[] args) {
        List<Animal> environment = new ArrayList<>();
        
        Wolf wolf = new Wolf(2, 2);
        Rabbit rabbit = new Rabbit(4, 4);
        
        environment.add(wolf);
        environment.add(rabbit);
        
        System.out.println("Vi tri ban dau Soi: " + wolf.getX() + ", " + wolf.getY());
        System.out.println("Vi tri ban dau Tho: " + rabbit.getX() + ", " + rabbit.getY() + " - Mau: " + rabbit.getHealth());
        
        for (int i = 1; i <= 6; i++) {
            System.out.println("--- Luot " + i + " ---");
            wolf.update(environment);
            
            System.out.println("Soi di chuyen den: " + wolf.getX() + ", " + wolf.getY());
            System.out.println("Mau cua Tho con: " + rabbit.getHealth());
            
            if (rabbit.getHealth() <= 0) {
                System.out.println("Tho da bi Soi tieu diet!");
                break;
            }
        }
    }
}