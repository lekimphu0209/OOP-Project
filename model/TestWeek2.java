package model;

import model.predator.*;
import model.strategy.*;
import java.util.ArrayList;
import java.util.List;

public class TestWeek2 {
    public static void main(String[] args) {

        DummyMap map = new DummyMap();

        Wolf  wolf  = new Wolf("Sói A",  new Vector2D(0, 0));
        Tiger tiger = new Tiger("Hổ B",  new Vector2D(5, 0)); // Gần sói

        List<Animal> group = new ArrayList<>();
        group.add(wolf);
        group.add(tiger);

        // --- Test 1: HunterStrategy tìm mồi ---
        System.out.println("=== TEST HUNTER STRATEGY ===");
        System.out.println("Strategy của Sói: " + wolf.getStrategy().getClass().getSimpleName());

        Vector2D dir = wolf.getStrategy().computeDirection(wolf, map, group);
        System.out.println("Hướng Sói muốn đi: " + dir);
        // Hổ có priority 5 > Sói priority 4 → Sói KHÔNG săn Hổ
        // Không có con mồi → Sói đi lang thang
        System.out.println("(Sói không săn Hổ vì Hổ mạnh hơn → đi lang thang)");

        // --- Test 2: Đặt Hổ làm mồi của Người ---
        System.out.println("\n=== TEST HUMAN PHÁT HIỆN MỒI ===");
        Human hunter = new Human("Thợ Săn C", new Vector2D(0, 0));
        List<Animal> hunterGroup = new ArrayList<>();
        hunterGroup.add(hunter);
        hunterGroup.add(tiger); // Hổ ở gần

        System.out.println("Strategy Người trước: " + hunter.getStrategy().getClass().getSimpleName());
        hunter.update(0.016, map, hunterGroup); // 1 frame
        System.out.println("Strategy Người sau khi thấy Hổ: " + hunter.getStrategy().getClass().getSimpleName());
        // Kết quả mong đợi: chuyển từ PatrolStrategy → HunterStrategy

        // --- Test 3: Sói bị thương → chạy trốn ---
        System.out.println("\n=== TEST AUTO FLEE KHI HP THẤP ===");
        Wolf weakWolf = new Wolf("Sói Yếu", new Vector2D(0, 0));
        weakWolf.takeDamage(80); // Còn 20 HP (dưới ngưỡng 30)
        System.out.println("HP Sói: " + weakWolf.getHealth());
        System.out.println("Strategy trước update: " + weakWolf.getStrategy().getClass().getSimpleName());

        weakWolf.update(0.016, map, new ArrayList<>());
        System.out.println("Strategy sau update: " + weakWolf.getStrategy().getClass().getSimpleName());
        // Kết quả mong đợi: chuyển sang FleeStrategy

        // --- Test 4: PatrolStrategy di chuyển qua các điểm ---
        System.out.println("\n=== TEST PATROL STRATEGY ===");
        Elephant elephant = new Elephant("Voi D", new Vector2D(0, 0));
        System.out.println("Strategy Voi: " + elephant.getStrategy().getClass().getSimpleName());
        Vector2D patrolDir = elephant.getStrategy().computeDirection(elephant, map, new ArrayList<>());
        System.out.println("Hướng Voi đi tuần tra: " + patrolDir);

    }
}