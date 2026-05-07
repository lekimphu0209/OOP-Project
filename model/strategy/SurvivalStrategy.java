package model.strategy;

import model.Animal;
import model.IMap;
import model.Vector2D;
import java.util.List;

/**
 * Interface định nghĩa "bộ não" AI cho mọi con vật.
 * Đây là Strategy Pattern: mỗi cách di chuyển/hành xử
 * là một implementation khác nhau của interface này.
 */
public interface SurvivalStrategy {

    /**
     * Tính toán hướng di chuyển tiếp theo.
     *
     * @param self    Con vật đang sử dụng strategy này
     * @param map     Bản đồ hiện tại (để kiểm tra tường, biên)
     * @param nearby  Danh sách con vật xung quanh
     * @return Vector2D hướng di chuyển (chưa chuẩn hóa)
     *         MovementEngine của Member 4 sẽ xử lý tốc độ thực tế
     */
    Vector2D computeDirection(Animal self, IMap map, List<Animal> nearby);
}