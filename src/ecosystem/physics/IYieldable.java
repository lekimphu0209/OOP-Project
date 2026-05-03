package ecosystem.physics;

public interface IYieldable {
    int getPriority();
    boolean mustYieldTo(IYieldable other);
}
