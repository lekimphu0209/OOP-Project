package ecosystem.environment;

import ecosystem.view.IObserver;

public interface ISubject {
    void registerObserver(IObserver observer);
    void removeObserver(IObserver observer);
    void notifyObservers();
}
