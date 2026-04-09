package ecosystem;
import java.util.List;

public class Human extends Animal {
    public Human(int x, int y) {
        super(x, y, 100);
    }

    @Override
    public void update(List<Animal> environment) {
        if (this.strategy != null) {
            this.strategy.execute(this, environment);
        }
    }
}