package ecosystem;
import java.util.List;

public abstract class Animal {
    protected int x;
    protected int y;
    protected int health;
    protected AIStrategy strategy;

    public Animal(int x, int y, int health) {
        this.x = x;
        this.y = y;
        this.health = health;
    }

    public void setStrategy(AIStrategy strategy) {
        this.strategy = strategy;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getHealth() { return health; }

    public void takeDamage(int damage) {
        this.health -= damage;
    }

    public abstract void update(List<Animal> environment);
}