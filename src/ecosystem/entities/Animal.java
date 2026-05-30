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

import ecosystem.behavior.*;
import ecosystem.environment.SeasonManager;
import ecosystem.environment.Environment;
import ecosystem.physics.Vector2D;
import ecosystem.terrain.TerrainType;
import ecosystem.view.render.IRenderStrategy;
import ecosystem.physics.ICollidable;
import ecosystem.physics.IMovable;
import ecosystem.physics.IYieldable;

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
    protected int hungerRate = 3;
    protected int staminaRate = 1;
    protected int reproductionCooldown = 0;
    protected int reproductionCooldownMax = 100; // ticks

    // Action state for display
    protected String actionState = "";
    protected int restTimer = 0;
    protected int eatTimer = 0;
    protected int drinkTimer = 0;
    
    // Movement control for sequential cell movement
    protected Vector2D targetPosition;
    protected double movementProgress = 0.0;
    
    // AI memory
    protected java.util.Set<String> visitedPositions = new java.util.HashSet<>();
    protected int explorationRange = 5;

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
        this.reproductionCooldownMax = 100; // Default: 100 ticks (5 seconds)
    }

    public void act(Environment env) {
        updateBiologicalStats(env);
        handleActionTimers();
        handleBasicNeeds(env);
        decreaseReproductionCooldown();

        state.handle(this, env);
        strategy.execute(this, env);
        move(env);
        updateMemory();
    }

    private void updateBiologicalStats(Environment env) {
        double seasonFactor = env.getSeason() == SeasonManager.Season.WINTER ? 1.5 : 1.0;
        hunger += (int)(hungerRate * seasonFactor);
        thirst += 0.5;
        stamina -= staminaRate;
        if (stamina < 0) stamina = 0;
        applyStarvationDamage(env);
    }

    private void handleActionTimers() {
        // Handle rest timer (predators after eating)
        if (restTimer > 0) {
            restTimer--;
            actionState = "Nghỉ ngơi";
            if (restTimer == 0) {
                actionState = "";
            }
            takeDamage(1); // Small penalty prevents immortality loops
            if (restTimer > 5) {
                return; // Stay still for most of rest time
            }
        }

        // Handle drink timer
        if (drinkTimer > 0) {
            drinkTimer--;
            actionState = "Đang uống";
            if (drinkTimer == 0) {
                actionState = "";
            }
            return; // Stay still while drinking
        }

        // Handle eat timer (chewing time)
        if (eatTimer > 0) {
            eatTimer--;
            actionState = "Đang ăn";
            if (eatTimer == 0) {
                actionState = "";
            }
            return; // Stay still while eating
        }
    }

    private void handleBasicNeeds(Environment env) {
        // Priority: thirst first, then hunger, then normal behavior
        if (thirst > 30) {
            actionState = "Tìm nước";
        } else if (hunger > 30) {
            actionState = "Tìm thức ăn";
        }
    }

    private void updateMemory() {
        String posKey = (int)position.getX() + "," + (int)position.getY();
        visitedPositions.add(posKey);
        if (visitedPositions.size() > 100) {
            visitedPositions.clear();
        }
    }

    public void move(Environment env) {
        double speed = calculateSpeed(env);
        Vector2D targetPosition = calculateTargetPosition();

        if (canMoveTo(env, targetPosition)) {
            executeMove(targetPosition, speed);
        } else {
            // Thử tìm hướng di chuyển hợp lệ
            for (int i = 0; i < 8; i++) {
                changeDirectionRandomly();
                targetPosition = calculateTargetPosition();
                if (canMoveTo(env, targetPosition)) {
                    executeMove(targetPosition, speed);
                    return;
                }
            }
            // Nếu vẫn không tìm được hướng hợp lệ, giữ nguyên vị trí
        }
    }

    private double calculateSpeed(Environment env) {
        return baseSpeed * speedBoost * env.getSpeedModifier((int) position.getX(), (int) position.getY());
    }

    private Vector2D calculateTargetPosition() {
        int currentX = (int) position.getX();
        int currentY = (int) position.getY();
        
        int targetX = currentX;
        int targetY = currentY;
        
        if (direction.getX() > 0.5) targetX++;
        else if (direction.getX() < -0.5) targetX--;
        if (direction.getY() > 0.5) targetY++;
        else if (direction.getY() < -0.5) targetY--;
        
        return new Vector2D(targetX, targetY);
    }

    private boolean canMoveTo(Environment env, Vector2D target) {
        int targetX = (int) target.getX();
        int targetY = (int) target.getY();

        boolean isWater = env.getGrid().getTile(targetX, targetY) != null &&
                         env.getGrid().getTile(targetX, targetY).getType() == ecosystem.terrain.TerrainType.WATER;

        boolean isForest = env.getGrid().getTile(targetX, targetY) != null &&
                          env.getGrid().getTile(targetX, targetY).getType() == ecosystem.terrain.TerrainType.FOREST;

        // Predators and humans cannot enter forest
        if (isForest && (isPredator() || this.getClass().getSimpleName().equals("Human"))) {
            return false;
        }

        // Check collision with other animals (unless hunting)
        int animalsInTargetTile = 0;
        for (Animal other : env.getAnimals()) {
            if (other == null || other == this) continue;
            if (!other.isAlive()) continue;

            int otherX = (int) other.getPosition().getX();
            int otherY = (int) other.getPosition().getY();

            if (otherX == targetX && otherY == targetY) {
                animalsInTargetTile++;
                // Allow collision only if hunting (predator chasing prey) and max 2 animals
                if (isPredator() && canEat(other) && animalsInTargetTile <= 1) {
                    continue; // Allow to attack prey (will be 2 animals total)
                }
                return false; // Block movement to occupied tile
            }
        }

        if (isWater) {
            return canSwim;
        } else {
            return canWalk && env.isWalkable(targetX, targetY);
        }
    }

    private void executeMove(Vector2D target, double speed) {
        position = target;
        velocity = direction.multiply(speed);
    }

    private void changeDirectionRandomly() {
        direction = new Vector2D(Math.random() - 0.5, Math.random() - 0.5).normalize();
        velocity = new Vector2D(0, 0);
    }

    public Entity findFood(Environment env) {
        return env.findNearestFood(this);
    }

    public void eat() {
        eat(null);
    }
    
    public void eat(Entity food) {
        hunger = 0;
        eatTimer = 3; // 3 ticks (1.5 giây) thời gian nhai
        // Hồi máu cho cả predator và herbivore (giúp predator sống sót)
        health = Math.min(health + 3, 100);

        // Nếu ăn thực vật, đánh dấu là đã ăn
        if (food instanceof Plant) {
            ((Plant) food).beEaten();
        }

        // Predator cần nghỉ ngơi sau khi ăn
        if (predator) {
            restTimer = 10; // Nghỉ trong 10 ticks
        }
        // Herbivore không cần thời gian chờ ăn - ăn xong tiếp tục di chuyển
    }

    // Uống nước
    public boolean drink(Environment env) {
        if (env.hasWaterNearby(this)) {
            thirst = 0;
            drinkTimer = 2; // 2 ticks (1 giây) thời gian uống
            return true;
        }
        return false;
    }

    public void takeDamage(int damage) {
        health -= damage;
    }

    private void applyStarvationDamage(Environment env) {
        int threshold = predator ? 40 : 50;
        if (hunger <= threshold && thirst <= 50) return;

        double damageMultiplier = env.getSeason() == SeasonManager.Season.WINTER ? 1.5 : 1.0;
        int baseDamage = predator ? 10 : 3;
        takeDamage((int) (baseDamage * damageMultiplier));

        // Extra penalty for extreme starvation/dehydration.
        if (hunger > 70 || thirst > 70) {
            takeDamage((int) (3 * damageMultiplier));
        }
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setStrategy(SurvivalStrategy strategy) {
        this.strategy = strategy;
    }

    public void setDirection(Vector2D direction) {
        this.direction = direction;
    }

    // Getters and Setters
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
        // Update logic handled by act() method
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
    
    public boolean hasVisitedPosition(int x, int y) {
        return visitedPositions.contains(x + "," + y);
    }
    
    public void setExplorationRange(int range) {
        this.explorationRange = range;
    }
    
    public int getExplorationRange() {
        return explorationRange;
    }
    
    // Enemy detection - override in subclasses for specific enemies
    public boolean isEnemy(Animal other) {
        return other.isPredator() && !this.isPredator();
    }
    
    // Food preference - override in subclasses for specific food
    public boolean canEat(Animal other) {
        // Default: predator eats any prey
        return this.isPredator() && !other.isPredator();
    }
    
    public boolean canEat(Plant plant) {
        // Default: herbivore eats any plant, predators CANNOT eat plants
        return !this.isPredator();
    }
    
    public Animal findNearestEnemy(Environment env) {
        Animal nearest = null;
        double minDistance = Double.MAX_VALUE;
        double visionRange = 5.0;
        
        for (Animal other : env.getAnimals()) {
            if (other == null) continue;
            if (other != this && other.isAlive() && isEnemy(other)) {
                double distance = this.getPosition().distanceTo(other.getPosition());
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

    // Reproduction methods
    public boolean canReproduce(Environment env) {
        return hunger < 30 && thirst < 30 && stamina > 70 &&
               !enemyNearby(env) && reproductionCooldown <= 0 &&
               !env.hasReachedPopulationLimit(this.getClass());
    }

    public boolean sameSpeciesNearby(Environment env) {
        for (Animal other : env.getAnimals()) {
            if (other == null) continue;
            if (other != this && other.isAlive() &&
                this.getClass().equals(other.getClass())) {
                double distance = this.getPosition().distanceTo(other.getPosition());
                if (distance <= 2.0) { // Trong phạm vi gần
                    return true;
                }
            }
        }
        return false;
    }

    public void reproduce(Environment env) {
        // Tạo động vật con ở vị trí gần
        Vector2D childPosition = findRandomNearbyPosition(env);
        if (childPosition != null) {
            Animal child = createChild(childPosition);
            env.addAnimal(child);
            reproductionCooldown = reproductionCooldownMax;
        }
    }

    protected Vector2D findRandomNearbyPosition(Environment env) {
        for (int i = 0; i < 8; i++) {
            int dx = (int) (Math.random() * 3) - 1; // -1, 0, 1
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

    protected Animal createChild(Vector2D position) {
        // Override trong subclass để tạo động vật con cụ thể
        return null;
    }

    public int getReproductionCooldown() {
        return reproductionCooldown;
    }

    public void setReproductionCooldown(int cooldown) {
        this.reproductionCooldown = cooldown;
    }

    public int getReproductionCooldownMax() {
        return reproductionCooldownMax;
    }

    public void decreaseReproductionCooldown() {
        if (reproductionCooldown > 0) {
            reproductionCooldown--;
        }
    }
}
