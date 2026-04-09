package ecosystem;
import java.util.List;

public class PatrolStrategy implements AIStrategy {
    @Override
    public void execute(Animal self, List<Animal> environment) {
        int newX = self.getX() + (int)(Math.random() * 3) - 1;
        int newY = self.getY() + (int)(Math.random() * 3) - 1;
        
        move(self, newX, newY);
    }

    private void move(Animal self, int targetX, int targetY) {
        self.x = targetX;
        self.y = targetY;
    }
}