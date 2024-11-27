package org.example;

import org.example.customLinkedList.CustomLinkedListDemo;

import static org.example.memory.OutOfMemoryExample.triggerOutOfMemory;
import static org.example.memory.StackOverflowExample.triggerStackOverflow;

public class App {

    public String getGreeting() {
        return "Hello World!";
    }

    public static void main(String[] args) {
        //CustomLinkedListDemo.demonstrateCustomLinkedList();
        triggerStackOverflow();
    }

}
