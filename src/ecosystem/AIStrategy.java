package ecosystem;
import java.util.List;

public interface AIStrategy {
    void execute(Animal self, List<Animal> environment);
}