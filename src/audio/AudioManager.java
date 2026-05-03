package audio;

import javafx.scene.media.AudioClip;

public class AudioManager {
    public static void playEffect(String fileName) {
        try {
            AudioClip note = new AudioClip("file:resources/sounds/" + fileName);
            note.play();
        } catch (Exception e) {
            System.out.println("Không tìm thấy file âm thanh.");
        }
    }
}