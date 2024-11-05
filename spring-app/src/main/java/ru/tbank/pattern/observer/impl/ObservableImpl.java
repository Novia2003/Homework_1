package ru.tbank.pattern.observer.impl;

import ru.tbank.pattern.observer.Observable;
import ru.tbank.pattern.observer.Observer;

import java.util.ArrayList;
import java.util.List;

public class ObservableImpl<T> implements Observable<T> {

    private final List<Observer<T>> observers = new ArrayList<>();

    @Override
    public void addObserver(Observer<T> observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer<T> observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(T model) {
        for (Observer<T> observer : observers) {
            observer.update(model);
        }
    }
}
