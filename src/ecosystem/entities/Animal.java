package ecosystem.entities;

/**
 * LEGACY ANIMAL - God Class (330 lines)
 * 
 * This is the old animal system that combined biology, movement, AI, and memory.
 * Still used by MainLegacy.java for backward compatibility.
 * 
 * For the new component-based animal system, see:
 * - ecosystem.entities.ComponentAnimal
 * - ecosystem.entities.components.BioStats
 * - ecosystem.entities.components.MovementController
 * - ecosystem.entities.components.AIBrain
 * - ecosystem.entities.components.Memory
 * - ecosystem.entities.components.AnimalFactory
 * - ecosystem.entities.components.Animation
 */

import ecosystem.SimulationConfig;
import ecosystem.audio.SoundManager;
import ecosystem.behavior.*;
import ecosystem.environment.SeasonManager;
import ecosystem.environment.Environment;
import ecosystem.physics.Vector2D;
import ecosystem.physics.ICollidable;
import ecosystem.physics.IMovable;
import ecosystem.physics.IYieldable;

import java.util.Objects;
import java.util.Random;

public abstract class Animal extends Entity implements ICollidable, IMovable, IYieldable {
    protected String name;
    protected int health;
    protected int hunger;
    protected int thirst;
    protected int stamina;
    protected State state;
    protected SurvivalStrategy strategy;
    protected Vector2D velocity;
    protected Vector2D direction;
    protected double baseSpeed;
    protected double speedBoost = 1.0;
    protected int priority;
    protected boolean predator;
    protected int attackDamage;
    protected boolean canSwim = false;
    protected boolean canWalk = true;
    protected int hungerRate = 4;
    /** Hệ số đói/khát theo loài (1.0 = mặc định; <1 chậm hơn; >1 nhanh hơn). */
    protected double metabolismFactor = 1.0;
    /** Hệ số sát thương khi đói/khát (<1 = chết chậm hơn dù đói). */
    protected double starvationDamageFactor = 1.0;
    /** Ngưỡng đói/khát trước khi mất máu (-1 = dùng mặc định theo loài). */
    protected int hungerDamageThreshold = -1;
    protected int thirstDamageThreshold = -1;
    protected int staminaRate = 1;
    protected int reproductionCooldown = 0;
    protected int reproductionCooldownMax = 100; // ticks

    // Action state for display
    protected String actionState = "";
    protected int restTimer = 0;
    protected int eatTimer = 0;
    protected int drinkTimer = 0;
    /** Sau khi đói, được ân đại trước khi starvation gây damage. */
    protected int starvationGraceTicks = 0;
    private int stuckTicks = 0;
    private double lastDisplayX;
    private double lastDisplayY;

    // Continuous world position (cells as floats); grid cell kept in Entity.position
    private double displayX;
    private double displayY;

    // AI memory (không dùng để chọn hướng — tránh đi cùng một quỹ đạo)
    protected java.util.Set<String> visitedPositions = new java.util.HashSet<>();
    protected int explorationRange = 5;

    /** RNG riêng từng con → hướng lang thang khác nhau. */
    private final Random pathRandom;
    private int wanderTurnTicks;

    public Animal(Vector2D position, double radius, String name, int health, double speed, int priority,
            boolean predator) {
        super(position, radius);
        this.name = name;
        this.health = health;
        this.hunger = 0;
        this.thirst = 0;
        this.stamina = 100;
        this.state = new WanderingState();
        this.strategy = new PassiveStrategy();
        this.velocity = new Vector2D(0, 0);
        this.direction = new Vector2D(0, 0);
        this.baseSpeed = speed;
        this.priority = priority;
        this.predator = predator;
        this.attackDamage = predator ? 100 : 0;
        setLegacyReproductionCooldown(100);
        pathRandom = new Random(Objects.hash(name, (int) position.getX(), (int) position.getY(),
                System.identityHashCode(this)));
        wanderTurnTicks = pathRandom.nextInt(8);
        syncDisplayToGrid();
        lastDisplayX = displayX;
        lastDisplayY = displayY;
        pickRandomWanderDirectionNow();
    }

    /** Đổi hướng ngẫu nhiên; giữ vài tick rồi mới đổi lại (đi tự nhiên, không trùng đàn). */
    public void pickRandomWanderDirection() {
        if (wanderTurnTicks > 0) {
            wanderTurnTicks--;
            return;
        }
        pickRandomWanderDirectionNow();
        wanderTurnTicks = 3 + pathRandom.nextInt(12);
    }

    /** Đổi hướng ngay lập tức. */
    public void pickRandomWanderDirectionNow() {
        double angle = pathRandom.nextDouble() * 2 * Math.PI;
        setDirection(new Vector2D(Math.cos(angle), Math.sin(angle)));
    }

    public int pickPathRandomIndex(int bound) {
        if (bound <= 0) {
            return 0;
        }
        return pathRandom.nextInt(bound);
    }

    protected void setLegacyReproductionCooldown(int legacyTicks) {
        this.reproductionCooldownMax = SimulationConfig.reproductionCooldownTicks(legacyTicks);
    }

    /** Vị trí float dùng để vẽ; logic lưới dùng {@link #getPosition()}. */
    public Vector2D getRenderPosition() {
        return new Vector2D(displayX, displayY);
    }

    /** +1 = phải, -1 = trái (lật sprite). */
    public int getFacingSign() {
        if (Math.abs(direction.getX()) > 0.05) {
            return direction.getX() > 0 ? 1 : -1;
        }
        if (Math.abs(velocity.getX()) > 0.01) {
            return velocity.getX() > 0 ? 1 : -1;
        }
        return 1;
    }

    public boolean isMoving() {
        return Math.abs(velocity.getX()) > 0.01 || Math.abs(velocity.getY()) > 0.01;
    }

    private void syncDisplayToGrid() {
        displayX = position.getX();
        displayY = position.getY();
    }

    public void snapToCell(Vector2D cell) {
        position = cell;
        displayX = cell.getX();
        displayY = cell.getY();
    }

    @Override
    public void setPosition(Vector2D position) {
        super.setPosition(position);
        syncDisplayToGrid();
    }

    public void act(Environment env) {
        updateMetabolism(env);
        handleActionTimers();
        handleBasicNeeds(env);
        decreaseReproductionCooldown();

        // Chuyển sang AggressiveStrategy khi đói quá cao (hunger > 70)
        if (hunger > 70 && !(strategy instanceof AggressiveStrategy)) {
            strategy = new AggressiveStrategy();
            setActionState("Cuồng nộ vì đói!");
            System.out.println("[Aggressive] " + name + " switched to AggressiveStrategy (hunger=" + hunger + ")");
        }
        // Quay lại strategy cũ khi no hơn (hunger < 40)
        else if (hunger < 40 && strategy instanceof AggressiveStrategy) {
            if (isPredator()) {
                strategy = new HunterStrategy();
            } else {
                strategy = new ScaredStrategy();
            }
            System.out.println("[Aggressive] " + name + " reverted to normal strategy (hunger=" + hunger + ")");
        }

        state.handle(this, env);
        strategy.execute(this, env);
        ensureMovementDirection();
        move(env);
        applyStarvationDamage(env);
        updateMemory();
    }

    /** Cùng một ô lưới (ăn / săn phải chạm ô). */
    public boolean sharesTileWith(Animal other) {
        if (other == null) {
            return false;
        }
        return (int) position.getX() == (int) other.getPosition().getX()
                && (int) position.getY() == (int) other.getPosition().getY();
    }

    public boolean sharesTileWith(Vector2D otherPos) {
        if (otherPos == null) {
            return false;
        }
        return (int) position.getX() == (int) otherPos.getX()
                && (int) position.getY() == (int) otherPos.getY();
    }

    public boolean isWithinRange(Vector2D otherPos, double radius) {
        if (otherPos == null) {
            return false;
        }
        return getRenderPosition().distanceTo(otherPos) <= radius;
    }

    public boolean isWithinRange(Animal other, double radius) {
        return other != null && isWithinRange(other.getRenderPosition(), radius);
    }

    private void updateMetabolism(Environment env) {
        double seasonFactor = env.getSeason() == SeasonManager.Season.WINTER ? 1.5 : 1.0;
        double metabolism = SimulationConfig.METABOLISM_MULT * metabolismFactor;
        hunger += Math.max(0, (int) Math.round(
                hungerRate * seasonFactor * SimulationConfig.legacyRate(1.0) * metabolism));
        thirst += SimulationConfig.legacyRate(0.5) * metabolism;
        stamina -= staminaRate;
        if (stamina < 0) {
            stamina = 0;
        }

        if (hunger > SimulationConfig.HUNGER_SEEK_THRESHOLD) {
            if (starvationGraceTicks <= 0) {
                starvationGraceTicks = SimulationConfig.legacyTicks(SimulationConfig.STARVATION_GRACE_LEGACY_TICKS);
            }
        } else {
            starvationGraceTicks = 0;
        }
    }

    private void handleActionTimers() {
        if (restTimer > 0) {
            restTimer--;
            if (predator && restTimer > 0) {
                actionState = "Nghỉ ngơi";
            }
            if (restTimer == 0) {
                if ("Nghỉ ngơi".equals(actionState)) {
                    actionState = "";
                }
            }
            if (restTimer % SimulationConfig.legacyTicks(5) == 0) {
                takeDamage(Math.max(1, (int) Math.round(SimulationConfig.STARVATION_DAMAGE_MULT)));
            }
        }

        if (drinkTimer > 0) {
            drinkTimer--;
            actionState = "Đang uống";
            if (drinkTimer == 0 && "Đang uống".equals(actionState)) {
                actionState = "";
            }
        }

        if (eatTimer > 0) {
            eatTimer--;
            actionState = "Đang ăn";
            if (eatTimer == 0 && "Đang ăn".equals(actionState)) {
                actionState = "";
            }
        }
    }

    /** Chỉ chặn di chuyển ngắn sau khi săn xong; ăn/uống không đứng hình. */
    private boolean isMovementBlocked() {
        return predator && restTimer > SimulationConfig.legacyTicks(1);
    }

    private void handleBasicNeeds(Environment env) {
        if (thirst > SimulationConfig.HUNGER_SEEK_THRESHOLD) {
            actionState = "Tìm nước";
        } else if (hunger > SimulationConfig.HUNGER_SEEK_THRESHOLD) {
            actionState = "Tìm thức ăn";
        }
    }

    private void updateMemory() {
        String posKey = (int) position.getX() + "," + (int) position.getY();
        visitedPositions.add(posKey);
        if (visitedPositions.size() > 100) {
            visitedPositions.clear();
        }
    }

    public void move(Environment env) {
        if (isMovementBlocked()) {
            velocity = new Vector2D(0, 0);
            return;
        }
        
        env.getPhysicsSystem().getMovementEngine().step(this, env);
        
        // Phát tiếng bước chân theo terrain
        int x = (int) getPosition().getX();
        int y = (int) getPosition().getY();
        if (env.getGrid().getTile(x, y) != null) {
            String terrainType = env.getGrid().getTile(x, y).getType().name().toLowerCase();
            SoundManager.getInstance().playTerrainSound(terrainType);
        }
    }

    /** Delegates tile collision to {@link ecosystem.physics.CollisionDetector}. */
    public boolean canEnterTile(Environment env, Vector2D target) {
        return env.getPhysicsSystem().getCollisionDetector().canEnterTile(this, env, target);
    }

    public boolean canSwimOnTerrain() {
        return canSwim;
    }

    public boolean canWalkOnTerrain() {
        return canWalk;
    }

    public Random getPathRandom() {
        return pathRandom;
    }

    public double getDisplayX() {
        return displayX;
    }

    public double getDisplayY() {
        return displayY;
    }

    public void addDisplayOffset(double dx, double dy) {
        displayX += dx;
        displayY += dy;
    }

    public Vector2D getMovementDirection() {
        return direction;
    }

    public double getSpeedBoost() {
        return speedBoost;
    }

    public void changeDirectionForPhysics() {
        changeDirectionRandomly();
    }

    public void trackStuckAndRecoverPhysics() {
        trackStuckAndRecover();
    }

    private void trackStuckAndRecover() {
        double dx = displayX - lastDisplayX;
        double dy = displayY - lastDisplayY;
        if (dx * dx + dy * dy < 0.0004) {
            stuckTicks++;
        } else {
            stuckTicks = 0;
        }
        lastDisplayX = displayX;
        lastDisplayY = displayY;

        if (stuckTicks >= 10 && !isMovementBlocked()) {
            changeDirectionRandomly();
            stuckTicks = 0;
        }
    }

    private void ensureMovementDirection() {
        if (direction.magnitude() < 0.01) {
            changeDirectionRandomly();
        }
    }

    private void changeDirectionRandomly() {
        pickRandomWanderDirectionNow();
        velocity = new Vector2D(0, 0);
        wanderTurnTicks = 2 + pathRandom.nextInt(6);
    }

    public Entity findFood(Environment env) {
        return env.findNearestFood(this);
    }

    public void eat() {
        eat(null);
    }

    public void eat(Entity food) {
        if (eatTimer > 0) {
            return;
        }
        hunger = 0;
        starvationGraceTicks = SimulationConfig.legacyTicks(SimulationConfig.STARVATION_GRACE_LEGACY_TICKS);
        eatTimer = SimulationConfig.legacyTicks(1);
        health = Math.min(health + 3, 100);

        if (food instanceof Plant) {
            ((Plant) food).beEaten();
        }

        if (predator) {
            restTimer = SimulationConfig.legacyTicks(3);
        }
    }

    public boolean drink(Environment env) {
        if (drinkTimer > 0 || !env.hasWaterNearby(this)) {
            return false;
        }
        thirst = 0;
        drinkTimer = SimulationConfig.legacyTicks(1);
        return true;
    }

    public void takeDamage(int damage) {
        health -= damage;
    }

    private void applyStarvationDamage(Environment env) {
        if (starvationGraceTicks > 0) {
            starvationGraceTicks--;
            return;
        }

        int hungerThreshold = hungerDamageThreshold >= 0 ? hungerDamageThreshold : (predator ? 35 : 45);
        int thirstThreshold = thirstDamageThreshold >= 0 ? thirstDamageThreshold : 42;
        if (hunger <= hungerThreshold && thirst <= thirstThreshold) {
            return;
        }

        double damageMultiplier = env.getSeason() == SeasonManager.Season.WINTER ? 1.5 : 1.0;
        int baseDamage = predator ? 12 : 5;
        double damageScale = SimulationConfig.STARVATION_DAMAGE_MULT * starvationDamageFactor;
        takeDamage((int) Math.max(1, baseDamage * damageMultiplier * damageScale));

        int extremeThreshold = predator ? 72 : 62;
        if (hunger > extremeThreshold || thirst > extremeThreshold) {
            takeDamage((int) Math.max(1, 4 * damageMultiplier * damageScale));
        }
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setStrategy(SurvivalStrategy strategy) {
        this.strategy = strategy;
    }

    public void setDirection(Vector2D direction) {
        if (direction == null || direction.magnitude() < 0.01) {
            changeDirectionRandomly();
            return;
        }
        this.direction = direction.normalize();
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public int getEatTimer() {
        return eatTimer;
    }

    public int getDrinkTimer() {
        return drinkTimer;
    }

    public int getHunger() {
        return hunger;
    }

    public void setHunger(int hunger) {
        this.hunger = hunger;
    }

    public int getThirst() {
        return thirst;
    }

    public void setThirst(int thirst) {
        this.thirst = thirst;
    }

    public int getStamina() {
        return stamina;
    }

    public void setStamina(int stamina) {
        this.stamina = stamina;
        if (this.stamina > 100) this.stamina = 100;
        if (this.stamina < 0) this.stamina = 0;
    }

    public boolean isTired() {
        return stamina < 30;
    }

    public void rest() {
        stamina += 20;
        if (stamina > 100) stamina = 100;
        actionState = "Nghỉ ngơi";
    }

    public State getState() {
        return state;
    }

    public SurvivalStrategy getStrategy() {
        return strategy;
    }

    @Override
    public Vector2D getVelocity() {
        return velocity;
    }

    @Override
    public void setVelocity(Vector2D velocity) {
        this.velocity = velocity;
    }

    @Override
    public double getBaseSpeed() {
        return baseSpeed;
    }

    @Override
    public boolean isSolid() {
        return true;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    /** Con priority thấp hơn phải nhường đường cho con priority cao hơn. */
    @Override
    public boolean mustYieldTo(IYieldable other) {
        return other.getPriority() > this.priority;
    }

    public boolean isPredator() {
        return predator;
    }

    public int getAttackDamage() {
        return attackDamage;
    }

    public void setSpeedBoost(double speedBoost) {
        this.speedBoost = speedBoost;
    }

    @Override
    public void update() {
    }

    public boolean isAlive() {
        return health > 0;
    }

    public String getActionState() {
        return actionState;
    }

    public void setActionState(String state) {
        this.actionState = state;
    }

    public void decreaseReproductionCooldown() {
        if (reproductionCooldown > 0) {
            reproductionCooldown--;
        }
    }

    public void setExplorationRange(int range) {
        this.explorationRange = range;
    }

    public int getExplorationRange() {
        return explorationRange;
    }

    public boolean isEnemy(Animal other) {
        return other.isPredator() && !this.isPredator();
    }

    public boolean canEat(Animal other) {
        return this.isPredator() && !other.isPredator();
    }

    public boolean canEat(Plant plant) {
        return !this.isPredator();
    }

    public Animal findNearestEnemy(Environment env) {
        Animal nearest = null;
        double minDistance = Double.MAX_VALUE;
        double visionRange = 5.0;

        for (Animal other : env.getAnimals()) {
            if (other == null) continue;
            if (other != this && other.isAlive() && isEnemy(other)) {
                double distance = getRenderPosition().distanceTo(other.getRenderPosition());
                if (distance < minDistance && distance <= visionRange) {
                    minDistance = distance;
                    nearest = other;
                }
            }
        }
        return nearest;
    }

    public boolean enemyNearby(Environment env) {
        return findNearestEnemy(env) != null;
    }

    public boolean canReproduce(Environment env) {
        if (SimulationConfig.MANUAL_SPAWNING) {
            return false;
        }
        return hunger < 30 && thirst < 30 && stamina > 70 &&
               !enemyNearby(env) && reproductionCooldown <= 0 &&
               !env.hasReachedPopulationLimit(this.getClass());
    }

    public boolean sameSpeciesNearby(Environment env) {
        for (Animal other : env.getAnimals()) {
            if (other == null) continue;
            if (other != this && other.isAlive() &&
                this.getClass().equals(other.getClass())) {
                double distance = getRenderPosition().distanceTo(other.getRenderPosition());
                if (distance <= 2.0) {
                    return true;
                }
            }
        }
        return false;
    }

    public void reproduce(Environment env) {
        Vector2D childPosition = findRandomNearbyPosition(env);
        if (childPosition != null) {
            Animal child = createChild(childPosition);
            env.addAnimal(child);
            reproductionCooldown = reproductionCooldownMax;
        }
    }

    protected Vector2D findRandomNearbyPosition(Environment env) {
        for (int i = 0; i < 8; i++) {
            int dx = (int) (Math.random() * 3) - 1;
            int dy = (int) (Math.random() * 3) - 1;
            int newX = (int) this.getPosition().getX() + dx;
            int newY = (int) this.getPosition().getY() + dy;
            if (newX >= 0 && newX < env.getGrid().getWidth() &&
                newY >= 0 && newY < env.getGrid().getHeight() &&
                env.isWalkable(newX, newY)) {
                return new Vector2D(newX, newY);
            }
        }
        return null;
    }

    protected abstract Animal createChild(Vector2D position);

    public boolean hasVisitedPosition(int x, int y) {
        return visitedPositions.contains(x + "," + y);
    }

    public int getReproductionCooldown() {
        return reproductionCooldown;
    }

    public int getReproductionCooldownMax() {
        return reproductionCooldownMax;
    }
}
