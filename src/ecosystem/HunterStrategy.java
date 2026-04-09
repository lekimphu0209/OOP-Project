package ecosystem;
import java.util.List;

public class HunterStrategy implements AIStrategy {
    private double visionRange = 5.0;

    @Override
    public void execute(Animal self, List<Animal> environment) {
        Animal prey = findNearestPrey(self, environment);
        
        if (prey != null) {
            moveToPrey(self, prey);
            attack(self, prey);
        } else {
            new PatrolStrategy().execute(self, environment);
        }
    }

    private Animal findNearestPrey(Animal self, List<Animal> environment) {
        Animal nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Animal other : environment) {
            if (other != self && isPrey(self, other)) {
                double distance = calculateDistance(self, other);
                if (distance < minDistance && distance <= visionRange) {
                    minDistance = distance;
                    nearest = other;
                }
            }
        }
        return nearest;
    }

    private boolean isPrey(Animal hunter, Animal other) {
        if (hunter instanceof Wolf && other instanceof Elephant) {
            return false;
        }
        return true; 
    }

    private double calculateDistance(Animal a, Animal b) {
        return Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2));
    }

    private void moveToPrey(Animal self, Animal prey) {
        if (self.getX() < prey.getX()) self.x++;
        else if (self.getX() > prey.getX()) self.x--;

        if (self.getY() < prey.getY()) self.y++;
        else if (self.getY() > prey.getY()) self.y--;
    }

    private void attack(Animal self, Animal prey) {
        if (self.getX() == prey.getX() && self.getY() == prey.getY()) {
            prey.takeDamage(10);
        }
    }
}