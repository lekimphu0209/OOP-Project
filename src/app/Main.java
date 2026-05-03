package app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.GameView;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Giả lập Model (sau này thay bằng Model thật của nhóm)
        Object dummyModel = new Object(); 
        
        // Khởi tạo View với kích thước 800x600
        GameView gameView = new GameView(800, 600, dummyModel);
        
        Scene scene = new Scene(gameView, 800, 600);
        
        primaryStage.setTitle("Mô phỏng Sinh thái OOP - Nhóm ...");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        
        // Cập nhật lần đầu để vẽ giao diện
        gameView.update();
    }

    public static void main(String[] args) {
        launch(args);
    }
}