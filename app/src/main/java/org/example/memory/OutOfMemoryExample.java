package org.example.memory;

import java.util.ArrayList;
import java.util.List;

public class OutOfMemoryExample {
    public static void triggerOutOfMemory() {
        List<Object> list = new ArrayList<>();

        while (true) {
            list.add(new Object());
        }
    }
}
