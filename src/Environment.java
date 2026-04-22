import java.util.*;

class Environment {
    List<Grass> grasses = new ArrayList<>();
    List<FruitTree> trees = new ArrayList<>();

    boolean hasWater = true;

    public Grass findGrass() {
        return grasses.isEmpty() ? null : grasses.get(0);
    }

    public FruitTree findFruitTree() {
        return trees.isEmpty() ? null : trees.get(0);
    }
}