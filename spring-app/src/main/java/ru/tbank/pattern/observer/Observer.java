package ru.tbank.pattern.observer;

public interface Observer<T> {
    void update(T model);
}
