package model;

import java.util.List;

import model.strategy.SurvivalStrategy;

/**
 * Abstract class đại diện cho mọi sinh vật trong hệ sinh thái.
 *
 * Implements 3 interface của Member 4:
 *  - IMovable   : có thể di chuyển qua MovementEngine
 *  - ICollidable: có thể va chạm qua CollisionDetector
 *  - IYieldable : có thể nhường đường qua InteractionMediator
 */
public abstract class Animal implements IMovable, ICollidable, IYieldable {

    // --- Vị trí & vật lý ---
    protected Vector2D position;
    protected Vector2D velocity;

    // --- Thông tin cơ bản ---
    protected String name;

    // --- Sinh học ---
    protected double health;
    protected double maxHealth;
    protected double hunger;   // 0 = no đói, 100 = chết đói
    protected double thirst;   // 0 = không khát, 100 = chết khát
    protected boolean alive;

    // --- Thuộc tính vật lý / di chuyển ---
    protected double baseSpeed;
    protected double radius;
    protected int priority;    // Dùng cho IYieldable: cao hơn = được nhường đường
 // --- Strategy Pattern: "bộ não" AI ---
    protected SurvivalStrategy strategy;
    // =========================================================
    // CONSTRUCTOR
    // =========================================================
    public Animal(String name, Vector2D position,
                  double baseSpeed, double radius,
                  int priority, double maxHealth) {
        this.name      = name;
        this.position  = position;
        this.baseSpeed = baseSpeed;
        this.radius    = radius;
        this.priority  = priority;
        this.maxHealth = maxHealth;

        // Giá trị khởi tạo mặc định
        this.health   = maxHealth;
        this.hunger   = 0.0;
        this.thirst   = 0.0;
        this.alive    = true;
        this.velocity = new Vector2D(0, 0);
    }

    // =========================================================
    // ABSTRACT METHODS — mỗi loài PHẢI tự implement
    // =========================================================

    /**
     * Cập nhật logic mỗi frame.
     * Member 5 (GameLoop) sẽ gọi method này liên tục.
     * Đây là nơi áp dụng POLYMORPHISM: Sói, Hổ, Thỏ đều có
     * update() khác nhau dù cùng được gọi qua Animal.
     */
    public abstract void update(double deltaTime, IMap map, List<Animal> nearby);

    /** Trả về tên loài. VD: "Sói", "Hổ", "Thỏ" */
    public abstract String getSpeciesName();

    /** Hành động tấn công một con vật khác */
    public abstract void attack(Animal prey);

    /** Hành động ăn, nhận dinh dưỡng */
    public abstract void eat(double nutritionValue);

    // =========================================================
    // CONCRETE METHODS — logic dùng chung cho tất cả loài
    // =========================================================

    /**
     * Cập nhật sinh học: tăng đói/khát theo thời gian.
     * Các lớp con gọi super.updateBiology(deltaTime) ở đầu update().
     */
    protected void updateBiology(double deltaTime) {
        if (!alive) return;

        hunger = Math.min(100.0, hunger + 3.0 * deltaTime);
        thirst = Math.min(100.0, thirst + 5.0 * deltaTime);

        // Chết nếu quá đói, quá khát, hoặc hết máu
        if (hunger >= 100.0 || thirst >= 100.0 || health <= 0) {
            alive = false;
        }
    }

    /** Nhận sát thương từ kẻ tấn công */
    public void takeDamage(double damage) {
        health = Math.max(0, health - damage);
        if (health <= 0) {
            alive = false;
        }
    }

    // =========================================================
    // IMPLEMENTS IMovable (Member 4 yêu cầu)
    // =========================================================
    @Override
    public Vector2D getPosition() { return position; }

    @Override
    public void setPosition(Vector2D position) { this.position = position; }

    @Override
    public Vector2D getVelocity() { return velocity; }

    @Override
    public void setVelocity(Vector2D velocity) { this.velocity = velocity; }

    @Override
    public double getBaseSpeed() { return baseSpeed; }

    // =========================================================
    // IMPLEMENTS ICollidable (Member 4 yêu cầu)
    // =========================================================
    @Override
    public double getRadius() { return radius; }

    @Override
    public boolean isSolid() { return true; }

    // =========================================================
    // IMPLEMENTS IYieldable (Member 4 yêu cầu)
    // =========================================================
    @Override
    public int getPriority() { return priority; }

    @Override
    public boolean mustYieldTo(IYieldable other) {
        return this.priority < other.getPriority();
    }

    // =========================================================
    // GETTERS & SETTERS thông thường
    // =========================================================
    public String getName()    { return name; }
    public double getHealth()  { return health; }
    public double getHunger()  { return hunger; }
    public double getThirst()  { return thirst; }
    public boolean isAlive()   { return alive; }

    public void setHunger(double hunger) {
        this.hunger = Math.max(0, Math.min(100, hunger));
    }
    public void setThirst(double thirst) {
        this.thirst = Math.max(0, Math.min(100, thirst));
    }
    public void setStrategy(SurvivalStrategy strategy) {
        this.strategy = strategy;
    }

    public SurvivalStrategy getStrategy() {
        return strategy;
    }
    @Override
    public String toString() {
        return String.format("%s[%s] pos=%s HP=%.1f Hunger=%.1f Thirst=%.1f alive=%b",
                getSpeciesName(), name, position, health, hunger, thirst, alive);
    }
}