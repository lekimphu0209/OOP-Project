package ecosystem.entities;

import ecosystem.behavior.State;
import ecosystem.behavior.SurvivalStrategy;
import ecosystem.behavior.WanderingState;
import ecosystem.behavior.PassiveStrategy;
import ecosystem.environment.Environment;
import ecosystem.physics.Vector2D;
import ecosystem.physics.ICollidable;
import ecosystem.physics.IMovable;
import ecosystem.physics.IYieldable;

public abstract class Animal extends Entity implements ICollidable, IMovable, IYieldable {
    protected String name;
    protected int health;
    protected int hunger;
    protected int thirst;
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
    protected int hungerRate = 1;

    public Animal(Vector2D position, double radius, String name, int health, double speed, int priority,
            boolean predator) {
        super(position, radius);
        this.name = name;
        this.health = health;
        this.hunger = 0;
        this.thirst = 0;
        this.state = new WanderingState();
        this.strategy = new PassiveStrategy();
        this.velocity = new Vector2D(0, 0);
        this.direction = new Vector2D(0, 0);
        this.baseSpeed = speed;
        this.priority = priority;
        this.predator = predator;
        this.attackDamage = predator ? 10 : 0;
    }

    public void act(Environment env) {
        double seasonFactor = env.getSeason() == Environment.Season.WINTER ? 1.5 : 1.0;
        hunger += (int)(hungerRate * seasonFactor);
        thirst++;
        
        // Starvation logic: lose health if too hungry or thirsty
        int threshold = predator ? 40 : 50;
        if (hunger > threshold || thirst > 50) {
            double damageMultiplier = env.getSeason() == Environment.Season.WINTER ? 1.5 : 1.0;
            int baseDamage = predator ? 10 : 2;
            takeDamage((int)(baseDamage * damageMultiplier));
        }
        
        state.handle(this, env);
        strategy.execute(this, env);
        move(env);
    }

    public void move(Environment env) {
        double speed = baseSpeed * speedBoost * env.getSpeedModifier((int) position.getX(), (int) position.getY());
        Vector2D movement = direction.multiply(speed);
        Vector2D newPosition = position.add(movement);

        int nx = (int) newPosition.getX();
        int ny = (int) newPosition.getY();
        
        boolean isWater = env.getGrid().getTile(nx, ny) != null && 
                         env.getGrid().getTile(nx, ny).getType() == ecosystem.terrain.TerrainType.WATER;
        
        boolean allowed = false;
        if (isWater) {
            if (canSwim) allowed = true;
        } else {
            if (canWalk && env.isWalkable(nx, ny)) allowed = true;
        }

        if (allowed) {
            position = newPosition;
            velocity = movement;
        } else {
            velocity = new Vector2D(0, 0);
        }
    }

    public Entity findFood(Environment env) {
        return env.findNearestFood(this);
    }

    public void eat() {
        hunger = 0;
        health = Math.min(health + 5, 100);
    }

    public boolean drink(Environment env) {
        if (env.hasWaterNearby(this)) {
            thirst = 0;
            return true;
        }
        return false;
    }

    public void takeDamage(int damage) {
        health -= damage;
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
}
