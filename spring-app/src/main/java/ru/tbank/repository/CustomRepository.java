package ru.tbank.repository;

import ru.tbank.pattern.observer.Observer;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class CustomRepository<T> implements Observer<T> {

    private final ConcurrentHashMap<Long, T> storage = new ConcurrentHashMap<>();

    private final AtomicLong idCounter = new AtomicLong(1);

    public Long save(T model) {
        Long id = idCounter.getAndIncrement();
        storage.put(id, model);

        return id;
    }

    public T findById(Long id) {
        return storage.get(id);
    }

    public Collection<T> findAll() {
        return storage.values();
    }

    public void update(Long id, T model) {
        storage.put(id, model);
    }

    public void delete(Long id) {
        storage.remove(id);
    }

    @Override
    public void update(T model) {
        save(model);
    }
}
