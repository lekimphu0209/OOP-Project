package model.strategy;

import model.Animal;
import model.IMap;
import model.Vector2D;
import java.util.List;

/**
 * AI săn mồi: tìm con mồi gần nhất trong tầm phát hiện
 * rồi lao thẳng về phía đó.
 * Nếu không có mồi thì đi lang thang ngẫu nhiên.
 *
 * Dùng cho: Wolf, Tiger, Human (khi đang săn)
 */
public class HunterStrategy implements SurvivalStrategy {

    private final double detectionRadius; // Tầm nhìn của kẻ săn

    // Lưu hướng đi lang thang hiện tại (để không đổi hướng liên tục)
    private Vector2D wanderDirection;
    private double wanderTimer = 0;
    private static final double WANDER_CHANGE_INTERVAL = 2.0; // đổi hướng mỗi 2 giây

    public HunterStrategy(double detectionRadius) {
        this.detectionRadius  = detectionRadius;
        this.wanderDirection  = randomDirection();
    }

    @Override
    public Vector2D computeDirection(Animal self, IMap map, List<Animal> nearby) {

        // --- BƯỚC 1: Tìm con mồi gần nhất ---
        Animal closestPrey    = null;
        double closestDistance = Double.MAX_VALUE;

        for (Animal other : nearby) {
            if (other == self)          continue; // bỏ qua chính mình
            if (!other.isAlive())       continue; // bỏ qua con đã chết
            // Con mồi = priority thấp hơn kẻ săn
            if (other.getPriority() >= self.getPriority()) continue;

            double dist = self.getPosition().distanceTo(other.getPosition());

            if (dist <= detectionRadius && dist < closestDistance) {
                closestDistance = dist;
                closestPrey     = other;
            }
        }

        // --- BƯỚC 2: Có mồi → lao về phía mồi ---
        if (closestPrey != null) {
            return closestPrey.getPosition().subtract(self.getPosition());
        }

        // --- BƯỚC 3: Không có mồi → đi lang thang ---
        return wander();
    }

    /**
     * Di chuyển ngẫu nhiên khi không có mồi.
     * Dùng timer để không đổi hướng quá nhanh (trông tự nhiên hơn).
     */
    private Vector2D wander() {
        wanderTimer += 0.016; // ~1 frame (60fps)
        if (wanderTimer >= WANDER_CHANGE_INTERVAL) {
            wanderDirection = randomDirection();
            wanderTimer     = 0;
        }
        return wanderDirection;
    }

    private Vector2D randomDirection() {
        double angle = Math.random() * 2 * Math.PI;
        return new Vector2D(Math.cos(angle), Math.sin(angle));
    }

    public double getDetectionRadius() {
        return detectionRadius;
    }
}