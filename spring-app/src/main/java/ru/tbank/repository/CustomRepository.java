package ru.tbank.repository;

import ru.tbank.pattern.observer.Observer;
import ru.tbank.repository.memento.StorageChange;
import ru.tbank.repository.memento.TypeStorageChange;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class CustomRepository<T> implements Observer<T> {

    private final ConcurrentHashMap<Long, T> storage = new ConcurrentHashMap<>();

    private final AtomicLong idCounter = new AtomicLong(1);

    private final Stack<StorageChange<T>> storageChanges = new Stack<>();

    public Long save(T model) {
        Long id = idCounter.getAndIncrement();
        storage.put(id, model);

        storageChanges.add(
                StorageChange
                        .<T>builder()
                        .id(id)
                        .model(model)
                        .change(TypeStorageChange.CREATE)
                        .time(LocalDateTime.now())
                        .build()
        );

        return id;
    }

    public T findById(Long id) {
        return storage.get(id);
    }

    public Collection<T> findAll() {
        return storage.values();
    }

    public void update(Long id, T updatedModel) {
        T model = findById(id);

        storageChanges.add(
                StorageChange
                        .<T>builder()
                        .id(id)
                        .model(model)
                        .change(TypeStorageChange.UPDATE)
                        .time(LocalDateTime.now())
                        .build()
        );

        storage.put(id, updatedModel);
    }

    public void delete(Long id) {
        T model = findById(id);

        storageChanges.add(
                StorageChange
                        .<T>builder()
                        .id(id)
                        .model(model)
                        .change(TypeStorageChange.DELETE)
                        .time(LocalDateTime.now())
                        .build()
        );

        storage.remove(id);
    }

    @Override
    public void update(T model) {
        save(model);
    }

    public void restore() {
        if (storageChanges.isEmpty()) {
            throw new NoSuchElementException("There are no changes in the log");
        }

        StorageChange<T> storageChange = storageChanges.pop();

        switch (storageChange.getChange()) {
            case CREATE -> storage.remove(storageChange.getId());
            case UPDATE, DELETE -> storage.put(storageChange.getId(), storageChange.getModel());
        }
    }
}
