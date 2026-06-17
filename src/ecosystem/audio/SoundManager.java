package ecosystem.audio;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Quản lý âm thanh cho hệ sinh thái
 * - Tiếng động vật (gầm, kêu, bước chân)
 * - Tiếng môi trường (gió, nước, lá xào xạc)
 * - Hiệu ứng đặc biệt
 */
public class SoundManager {
    private static SoundManager instance;
    private final Map<String, Clip> soundCache;
    private final ExecutorService soundExecutor;
    private boolean enabled = true;
    private float volume = 1.0f; // Tăng volume mặc định

    private SoundManager() {
        this.soundCache = new HashMap<>();
        this.soundExecutor = Executors.newFixedThreadPool(3); // 3 luồng phát song song
        preloadSounds();
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    /**
     * Preload các file âm thanh thường dùng
     */
    private void preloadSounds() {
        // Định nghĩa các file âm thanh (sẽ tạo placeholder sau)
        String[] soundFiles = {
            "tiger_roar",      // Hổ gầm
            "wolf_howl",       // Sói hú
            "rabbit_squeak",   // Thỏ kêu
            "deer_bark",       // Hươu kêu
            "elephant_trumpet",// Voi kêu
            "human_shout",     // Người la
            "crocodile_snap",  // Cá sấu
            "duck_quack",      // Vịt kêu
            "fish_splash",     // Cá bắn nước
            "footstep_grass",  // Bước chân trên cỏ
            "footstep_forest", // Bước chân trên rừng
            "footstep_water",  // Bước chân trong nước
            "eating",          // Tiếng ăn
            "drinking",        // Tiếng uống
            "ambient_wind",    // Tiếng gió
            "ambient_water"    // Tiếng nước
        };

        for (String sound : soundFiles) {
            try {
                loadSound(sound);
            } catch (Exception e) {
                // Placeholder - log nhưng không crash nếu file chưa có
                System.out.println("[SoundManager] Sound not found: " + sound);
            }
        }
    }

    /**
     * Load file âm thanh vào cache
     */
    private void loadSound(String soundName) {
        if (soundCache.containsKey(soundName)) {
            return;
        }

        try {
            String path = "resources/audio/" + soundName + ".wav";
            File audioFile = new File(path);
            
            if (!audioFile.exists()) {
                // Tạo silent clip placeholder nếu file không tồn tại
                soundCache.put(soundName, createSilentClip());
                return;
            }

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            soundCache.put(soundName, clip);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            soundCache.put(soundName, createSilentClip());
        }
    }

    /**
     * Tạo silent clip placeholder
     */
    private Clip createSilentClip() {
        try {
            Clip clip = AudioSystem.getClip();
            // Empty clip - không phát âm thanh
            return clip;
        } catch (LineUnavailableException e) {
            return null;
        }
    }

    /**
     * Phát âm thanh (async)
     */
    public void playSound(String soundName) {
        if (!enabled) return;

        soundExecutor.submit(() -> {
            try {
                Clip clip = soundCache.get(soundName);
                if (clip != null) {
                    // Reset clip về đầu
                    clip.setFramePosition(0);
                    
                    // Set volume (có thể không hỗ trợ trên một số systems)
                    try {
                        if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                            FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                            float gain = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
                            volumeControl.setValue(gain);
                        }
                    } catch (Exception volumeError) {
                        // Ignore volume control errors
                    }
                    
                    clip.start();
                }
            } catch (Exception e) {
                // Silent fail
            }
        });
    }

    /**
     * Phát âm thanh với volume tùy chỉnh
     */
    public void playSound(String soundName, float customVolume) {
        float originalVolume = this.volume;
        this.volume = customVolume;
        playSound(soundName);
        this.volume = originalVolume;
    }

    /**
     * Phát tiếng động vật theo loại
     */
    public void playAnimalSound(String animalType, String action) {
        String soundKey = animalType + "_" + action;
        playSound(soundKey, 0.9f); // Tăng volume cho tiếng động vật
    }

    /**
     * Phát tiếng theo terrain
     */
    public void playTerrainSound(String terrainType) {
        // Map enum names to sound file names
        String soundKey;
        switch (terrainType.toLowerCase()) {
            case "grass":
                soundKey = "footstep_grass";
                break;
            case "forest":
                soundKey = "footstep_forest";
                break;
            case "water":
                soundKey = "footstep_water";
                break;
            case "mud":
                soundKey = "footstep_mud";
                break;
            case "obstacle":
                soundKey = "footstep_obstacle";
                break;
            default:
                soundKey = "footstep_grass";
        }
        // Tăng volume tiếng bước chân và giảm tần suất (chỉ phát 30% thời gian)
        if (Math.random() < 0.3) {
            playSound(soundKey, 1.0f); // Volume tối đa
        }
    }

    /**
     * Phát ambient sound (môi trường)
     */
    public void playAmbient(String ambientType) {
        String soundKey = "ambient_" + ambientType;
        playSound(soundKey, 0.4f); // Volume thấp
    }

    /**
     * Bật/tắt âm thanh
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Set volume (0.0 - 1.0)
     */
    public void setVolume(float volume) {
        this.volume = Math.max(0.0f, Math.min(1.0f, volume));
    }

    public float getVolume() {
        return volume;
    }

    /**
     * Dừng tất cả âm thanh
     */
    public void stopAll() {
        for (Clip clip : soundCache.values()) {
            if (clip != null && clip.isRunning()) {
                clip.stop();
            }
        }
    }

    /**
     * Cleanup resources
     */
    public void shutdown() {
        stopAll();
        soundExecutor.shutdown();
        for (Clip clip : soundCache.values()) {
            if (clip != null) {
                clip.close();
            }
        }
        soundCache.clear();
    }
}
