package ecosystem;

/**
 * Central timing for simulation vs legacy 500ms ticks.
 */
public final class SimulationConfig {
    public static final int TICK_MS = 100;
    public static final int LEGACY_TICK_MS = 500;
    public static final double TICK_SCALE = (double) LEGACY_TICK_MS / TICK_MS;

    /** Đi chậm hơn (1.0 = tốc độ cũ sau khi chuyển 100ms tick). */
    public static final double MOVEMENT_SPEED_MULT = 0.38;

    /** Đói/khát tăng nhanh (>1 = nhanh hơn chuẩn legacy). */
    public static final double METABOLISM_MULT = 1.55;

    /** Sát thương đói/khát (>1 = chết nhanh hơn khi đói/khát). */
    public static final double STARVATION_DAMAGE_MULT = 1.45;

    /** Cooldown sinh sản dài hơn (nhân với số legacy ticks). */
    public static final double REPRODUCTION_DURATION_MULT = 3.5;

    /** Xác suất spawn động vật/plant ngẫu nhiên từ controller/environment. */
    public static final double WORLD_SPAWN_CHANCE_MULT = 0.22;

    /** Map bắt đầu trống; người chơi tự đặt động vật (không spawn tự động). */
    public static final boolean MANUAL_SPAWNING = true;

    /** Ngưỡng bắt đầu chủ động tìm thức ăn / săn mồi. */
    public static final int HUNGER_SEEK_THRESHOLD = 22;

    /** Vài giây sau khi đói mới bị trừ máu (theo tick legacy 500ms). */
    public static final int STARVATION_GRACE_LEGACY_TICKS = 35;

    /** Số mồi tối thiểu để predator được săn (1 khi map thủ công). */
    public static int minPreyCountToHunt() {
        return MANUAL_SPAWNING ? 1 : 3;
    }

    /** Bán kính (ô) sói có thể vồ / ăn thỏ — gần, không cần trùng ô. */
    public static final double PREDATOR_ATTACK_RADIUS = 1.35;

    /** Bán kính ăn cỏ/cây. */
    public static final double HERBIVORE_EAT_RADIUS = 1.2;

    private SimulationConfig() {
    }

    /** Convert a duration expressed in legacy 500ms ticks to current tick count. */
    public static int legacyTicks(int legacyTickCount) {
        return Math.max(1, (int) Math.round(legacyTickCount * TICK_SCALE));
    }

    /** Cooldown sinh sản (chậm hơn legacyTicks thường). */
    public static int reproductionCooldownTicks(int legacyTickCount) {
        return Math.max(1, (int) Math.round(legacyTickCount * TICK_SCALE * REPRODUCTION_DURATION_MULT));
    }

    /** Scale a per-legacy-tick rate down for faster simulation ticks. */
    public static double legacyRate(double perLegacyTick) {
        return perLegacyTick / TICK_SCALE;
    }
}
