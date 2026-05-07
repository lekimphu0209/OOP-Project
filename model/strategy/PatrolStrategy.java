package model.strategy;

import model.Animal;
import model.IMap;
import model.Vector2D;
import java.util.List;

/**
 * AI tuần tra: di chuyển tuần tự qua các điểm định sẵn.
 * Đến điểm này → chuyển sang điểm tiếp theo → lặp lại.
 *
 * Dùng cho: Human (mặc định), Elephant
 */
public class PatrolStrategy implements SurvivalStrategy {

    private final Vector2D[] patrolPoints;   // Danh sách điểm tuần tra
    private int currentIndex = 0;            // Đang đi đến điểm nào
    private static final double ARRIVE_THRESHOLD = 3.0; // Coi là "đến nơi" khi cách < 3 đơn vị

    /**
     * @param patrolPoints Các điểm tuần tra theo thứ tự.
     *                     VD: new PatrolStrategy(A, B, C, D) → A→B→C→D→A→B→...
     */
    public PatrolStrategy(Vector2D... patrolPoints) {
        if (patrolPoints == null || patrolPoints.length == 0) {
            throw new IllegalArgumentException("Cần ít nhất 1 điểm tuần tra!");
        }
        this.patrolPoints = patrolPoints;
    }

    @Override
    public Vector2D computeDirection(Animal self, IMap map, List<Animal> nearby) {
        Vector2D target = patrolPoints[currentIndex];
        double distToTarget = self.getPosition().distanceTo(target);

        // Đến nơi rồi → chuyển sang điểm tiếp theo (vòng tròn)
        if (distToTarget < ARRIVE_THRESHOLD) {
            currentIndex = (currentIndex + 1) % patrolPoints.length;
            target       = patrolPoints[currentIndex];
        }

        // Trả về vector hướng về điểm mục tiêu
        return target.subtract(self.getPosition());
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public Vector2D[] getPatrolPoints() {
        return patrolPoints;
    }
}