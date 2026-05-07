package model;

import model.predator.*;
import java.util.ArrayList;
import java.util.List;

public class TestWeek1 {
    public static void main(String[] args) {

        // Tạo các con vật
        Wolf    wolf     = new Wolf("Sói A",     new Vector2D(0, 0));
        Tiger   tiger    = new Tiger("Hổ B",      new Vector2D(50, 50));
        Human   hunter   = new Human("Thợ Săn C", new Vector2D(20, 20));
        Elephant elephant = new Elephant("Voi D", new Vector2D(80, 80));

        System.out.println("=== KHỞI TẠO ===");
        System.out.println(wolf);
        System.out.println(tiger);
        System.out.println(hunter);
        System.out.println(elephant);

        // Test priority / nhường đường
        System.out.println("\n=== TEST PRIORITY ===");
        System.out.println("Sói nhường Hổ?   " + wolf.mustYieldTo(tiger));     // true
        System.out.println("Hổ nhường Sói?   " + tiger.mustYieldTo(wolf));     // false
        System.out.println("Voi nhường Người? " + elephant.mustYieldTo(hunter)); // true
        System.out.println("Người nhường Voi? " + hunter.mustYieldTo(elephant)); // false

        // Test takeDamage
        System.out.println("\n=== TEST SÁT THƯƠNG ===");
        System.out.println("HP Sói trước: " + wolf.getHealth());
        wolf.takeDamage(30);
        System.out.println("HP Sói sau khi nhận 30 damage: " + wolf.getHealth()); // 70

        // Test update (sinh học: đói/khát tăng)
        System.out.println("\n=== TEST SINH HỌC (update 5 giây) ===");
        List<Animal> group = new ArrayList<>();
        DummyMap map = new DummyMap();

        System.out.println("Hunger Hổ trước: " + tiger.getHunger());
        tiger.update(5.0, map, group);
        System.out.println("Hunger Hổ sau 5s: " + tiger.getHunger()); // ~15.0

        // Test tấn công
        System.out.println("\n=== TEST TẤN CÔNG ===");
        // Đặt thỏ giả (DummyAnimal) gần sói
        
        // Test với Animal thật: Sói tấn công Voi (vô nghĩa thực tế, chỉ để test)
        double hpBefore = elephant.getHealth();
        wolf.attack(elephant);
        System.out.println("HP Voi trước tấn công: " + hpBefore);
        System.out.println("HP Voi sau khi Sói cắn: " + elephant.getHealth()); // 300 - 25 = 275

    }
}