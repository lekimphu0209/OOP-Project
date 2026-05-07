package model.strategy;

import model.Animal;
import model.IMap;
import model.Vector2D;
import java.util.List;

/**
 * AI bỏ chạy: tìm mối nguy hiểm gần nhất rồi chạy ngược chiều.
 * Tự động kích hoạt khi Animal bị thương nặng.
 *
 * Dùng cho: bất kỳ con vật nào khi HP thấp
 */
public class FleeStrategy implements SurvivalStrategy {

    private final double dangerRadius; // Tầm phát hiện nguy hiểm

    public FleeStrategy(double dangerRadius) {
        this.dangerRadius = dangerRadius;
    }

    @Override
    public Vector2D computeDirection(Animal self, IMap map, List<Animal> nearby) {
        Vector2D fleeVector = new Vector2D(0, 0);
        boolean foundDanger = false;

        for (Animal other : nearby) {
            if (other == self)    continue;
            if (!other.isAlive()) continue;
            // Nguy hiểm = con vật có priority CAO hơn (kẻ săn mình)
            if (other.getPriority() <= self.getPriority()) continue;

            double dist = self.getPosition().distanceTo(other.getPosition());
            if (dist <= dangerRadius) {
                // Cộng dồn vector chạy trốn (tránh nhiều kẻ thù cùng lúc)
                Vector2D awayFromDanger = self.getPosition()
                                             .subtract(other.getPosition());
                fleeVector  = fleeVector.add(awayFromDanger);
                foundDanger = true;
            }
        }

        if (foundDanger) {
            return fleeVector; // Chạy theo hướng tổng hợp
        }

        // Không còn nguy hiểm → đứng yên (GameLoop sẽ đổi strategy)
        return new Vector2D(0, 0);
    }
}