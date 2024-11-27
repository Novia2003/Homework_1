package org.example.memory;

public class StackOverflowExample {
    public static void triggerStackOverflow() {
        triggerStackOverflow();
    }
}
