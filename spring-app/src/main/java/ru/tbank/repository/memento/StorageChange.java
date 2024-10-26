package ru.tbank.repository.memento;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class StorageChange<T> {
    private Long id;
    private T model;
    private TypeStorageChange change;
    private LocalDateTime time;
}
