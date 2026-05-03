module hust.oop.view {
    // Import các module cần thiết của JavaFX
    requires javafx.controls;   // Chứa Scene, Stage, StackPane...
    requires javafx.graphics;   // Chứa Canvas, GraphicsContext, Image, Color...
    requires javafx.media;      // Chứa AudioClip để phát âm thanh

    // Cho phép JavaFX truy cập vào các gói của bạn để khởi chạy
    exports app;
    exports view;
    exports render;
    exports event;
    exports audio;
    exports utils;
}