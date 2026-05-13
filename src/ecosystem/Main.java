package ecosystem;

import ecosystem.controller.SimulationController;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Hệ thống Mô phỏng Hệ sinh thái Hoang dã ===");
        System.out.println("Đang khởi tạo hệ thống...");

        SimulationController controller = new SimulationController(20, 20);
        controller.initialize();

        System.out.println("Hệ thống đã khởi động!");
        System.out.println("Đặc điểm:");
        System.out.println("- Hình tròn: Động vật ăn cỏ (Thỏ, Hươu)");
        System.out.println("- Hình vuông: Động vật ăn thịt (Sói, Hổ)");
        System.out.println("- Điểm xanh nhỏ: Cỏ");
        System.out.println("- Điểm cam lớn: Cây ăn quả");
        System.out.println("- Màu nền: Xanh lá (Cỏ), Xanh đậm (Rừng), Xanh dương (Nước), Xám (Vật cản)");
    }
}
