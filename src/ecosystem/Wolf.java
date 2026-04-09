package ecosystem;
import java.util.List;

public class Wolf extends Animal {
    public Wolf(int x, int y) {
        super(x, y, 100);
        this.setStrategy(new HunterStrategy());
    }

    @Override
    public void update(List<Animal> environment) {
        if (this.strategy != null) {
            this.strategy.execute(this, environment);
        }
    }
}