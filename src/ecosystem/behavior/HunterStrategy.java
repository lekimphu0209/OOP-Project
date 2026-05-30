package ecosystem.behavior;

import ecosystem.entities.Animal;
import ecosystem.environment.Environment;
import ecosystem.physics.Vector2D;
import ecosystem.terrain.TerrainType;

import java.util.List;

public class HunterStrategy implements SurvivalStrategy {
    // Phạm vi nhìn thấy của predator (tính theo ô)
    private double visionRange = 4.0;

    @Override
    public void execute(Animal animal, Environment env) {
        // Nếu động vật đang ăn/uống, không ghi đè action state
        if (animal.getEatTimer() > 0 || animal.getDrinkTimer() > 0) {
            return;
        }

        // Ưu tiên: kiểm tra mệt mỏi trước
        if (animal.isTired()) {
            animal.rest();
            return;
        }

        // Ưu tiên: kiểm tra khát và đói trước
        if (animal.getThirst() > 30) {
            if (env.hasWaterNearby(animal)) {
                animal.drink(env);
                animal.setActionState("Đang uống");
            } else {
                animal.setActionState("Tìm nước");
                Vector2D waterDir = env.findDirectionToNearestWater(animal, 6);
                if (waterDir != null) animal.setDirection(waterDir);
            }
            return;
        }

        // Ưu tiên: sinh sản (sau khi đã xử lý mệt, khát, đói)
        if (animal.canReproduce(env) && animal.sameSpeciesNearby(env)) {
            animal.setActionState("Sinh sản");
            animal.reproduce(env);
            return;
        }

        // Đặt action state săn mồi
        animal.setActionState("Săn mồi");

        // 5% cơ hội bỏ săn (để tránh săn quá dễ dàng)
        if (Math.random() < 0.05) {
            animal.setSpeedBoost(1.0);
            animal.setActionState("Bỏ săn");
            return;
        }

        // Tìm con mồi gần nhất
        Animal prey = findNearestPrey(animal, env.getAnimals());

        if (prey != null) {
            // Kiểm tra số lượng con mồi - không săn nếu quá ít để tránh tuyệt chủng
            int preyCount = 0;
            for (Animal a : env.getAnimals()) {
                if (a != null && a.isAlive() && a.getClass().equals(prey.getClass())) {
                    preyCount++;
                }
            }
            // Không săn nếu ít hơn 3 con của loài đó
            if (preyCount < 3) {
                animal.setSpeedBoost(1.0);
                animal.setActionState("Bảo tồn");
                return;
            }

            // Đặt hướng về phía con mồi
            Vector2D direction = prey.getPosition().subtract(animal.getPosition()).normalize();
            animal.setDirection(direction);

            // Kiểm tra xem con mồi có trong rừng không (sói không vào được)
            int preyX = (int) prey.getPosition().getX();
            int preyY = (int) prey.getPosition().getY();

            if (env.getGrid().getTile(preyX, preyY).getType() == TerrainType.FOREST) {
                // Con mồi trốn trong rừng, không đuổi theo
                animal.setSpeedBoost(1.0);
                animal.setActionState("Mất mồi");
                return;
            }

            // Tăng tốc độ khi đuổi theo con mồi
            // Kiểm tra xem predator có trong nước không (như Cá sấu) - giữ nguyên tốc độ nước
            boolean inWater = env.getGrid().getTile((int)animal.getPosition().getX(), (int)animal.getPosition().getY()).getType() == TerrainType.WATER;
            if (!inWater) {
                animal.setSpeedBoost(1.5);
            }
            // Nếu trong nước, để move() method xử lý tốc độ (Cá sấu: 1.2x trong nước)

            // Kiểm tra xem có trong cùng ô không (phạm vi 2.0 để bắt con mồi đang chạy)
            boolean inSameTile = animal.getPosition().distanceTo(prey.getPosition()) < 2.0;

            if (inSameTile) {
                // Săn thành công ngay lập tức khi trong phạm vi
                // Giết con mồi ngay lập tức
                prey.takeDamage(animal.getAttackDamage()); // 100 damage = giết ngay
                // Đảm bảo con mồi chết bằng cách set health = 0
                if (prey.isAlive()) {
                    prey.takeDamage(prey.getHealth()); // Giết buộc
                }
                // Chỉ ăn khi đói để tránh hiển thị UI "Đang ăn" không cần thiết
                if (animal.getHunger() > 0) {
                    animal.eat();
                    animal.setActionState("Đang ăn");
                } else {
                    animal.setActionState("Săn thành công");
                }
            } else {
                // Không trong cùng ô - con mồi chạy thoát
                animal.setActionState("Săn mồi");
            }
        } else {
            // Không tìm thấy con mồi
            animal.setSpeedBoost(1.0); // Reset tốc độ
            animal.setActionState("Tìm mồi");

            // Sử dụng trí nhớ AI để tránh lặp lại
            if (shouldExpandSearch(animal)) {
                animal.setExplorationRange(animal.getExplorationRange() + 2);
            }

            // Đi lang thang không ăn thực vật - predator chỉ ăn thịt
            double angle = Math.random() * 2 * Math.PI;
            Vector2D direction = new Vector2D(Math.cos(angle), Math.sin(angle));
            animal.setDirection(direction);
        }
    }

    // Mở rộng phạm vi tìm kiếm nếu động vật đã tìm kiếm lâu
    private boolean shouldExpandSearch(Animal animal) {
        return animal.getHunger() > 50 && animal.getExplorationRange() < 15;
    }

    // Tìm con mồi gần nhất trong phạm vi nhìn thấy
    private Animal findNearestPrey(Animal hunter, List<Animal> animals) {
        Animal nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Animal other : animals) {
            if (other == null) continue;
            if (other != hunter && isPrey(hunter, other)) {
                double distance = hunter.getPosition().distanceTo(other.getPosition());
                if (distance < minDistance && distance <= visionRange) {
                    minDistance = distance;
                    nearest = other;
                }
            }
        }
        return nearest;
    }

    // Kiểm tra xem animal có phải là con mồi không
    private boolean isPrey(Animal hunter, Animal other) {
        return hunter.canEat(other);
    }

    @Override
    public String getName() {
        return "Hunter";
    }
}
