package com.example.test_assignment.dao;

import com.example.test_assignment.model.User;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryDatabase {
    @Getter
    private static final Map<Long, User> users = new ConcurrentHashMap<>();
    private static final AtomicLong currentId = new AtomicLong(0);

    public static Long getNextId() {
        return currentId.incrementAndGet();
    }
}
