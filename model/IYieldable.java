package model;

public interface IYieldable {
    int getPriority();
    boolean mustYieldTo(IYieldable other);
}